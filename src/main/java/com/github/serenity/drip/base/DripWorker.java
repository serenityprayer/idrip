package com.github.serenity.drip.base;

import java.security.SecureRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于 twitter Snowflake 算法的分布式生成唯一id解决方案
 * <p>
 * 不同节点指定workerId datacenterId实例化，保证nextId唯一
 */
public class DripWorker {
    // 起始时间原点，指定该值小于并接近当前System.currentTimeMillis()
    private final long twepoch = 1504862701664L;
    // 分片基因埋点位
    private final long geneBits = 3L;
    // 毫秒内自增位
    private final long sequenceBits = 9L;
    // 机器标识位数
    private final long workerIdBits = 5L;
    // 数据中心标识位数
    private final long datacenterIdBits = 5L;
    // 毫秒时间位
    private final long timestampBits = 41L;
    // 机器ID最大值
    private final long maxWorkerId = ~(-1L << workerIdBits);
    // 数据中心ID最大值
    private final long maxDatacenterId = ~(-1L << datacenterIdBits);
    // sequence偏左移0位
    private final long sequenceShift = geneBits;
    // 机器ID偏左移12位
    private final long workerIdShift = geneBits + sequenceBits;
    // 数据中心ID左移17位
    private final long datacenterIdShift = geneBits + sequenceBits + workerIdBits;
    // 时间毫秒左移22位
    private final long timestampLeftShift = geneBits + sequenceBits + workerIdBits + datacenterIdBits;
    // 毫秒内sequence最大值
    private final long sequenceMask = ~(-1L << sequenceBits);
    private final long timestampMask = ~(-1L << timestampBits);
    private final long geneMask = ~(-1L << geneBits);

    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private Lock lock = new ReentrantLock();
    private SecureRandom random = new SecureRandom();

    public DripWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0)
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        if (datacenterId > maxDatacenterId || datacenterId < 0)
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public long nextId() {
        return nextId(random.nextInt((int) geneMask + 1));
    }

    public long nextIdWithGene(long geneId) {
        if (geneMask <= 0L)
            throw new IllegalStateException("no gene bits. Refusing to generate id with gene.");
        return nextId(geneId & geneMask);
    }

    protected long nextId(long geneId) {
        Drip drip = new Drip();
        drip.setWorkerId(workerId);
        drip.setDatacenterId(datacenterId);
        drip.setGeneId(geneId);
        this.assemble(drip);
        return composeId(drip);
    }

    public Drip resolveId(long id) {
        Drip drip = new Drip();
        drip.setGeneId(id & geneMask);
        drip.setSequence((id >>> sequenceShift) & sequenceMask);
        drip.setWorkerId((id >>> workerIdShift) & maxWorkerId);
        drip.setDatacenterId((id >>> datacenterIdShift) & maxDatacenterId);
        drip.setTimestamp(((id >>> timestampLeftShift) & timestampMask) + twepoch);
        return drip;
    }

    public long composeId(Drip drip) {
        long id = 0L;
        id |= (drip.getTimestamp() - twepoch) << timestampLeftShift;
        id |= drip.getDatacenterId() << datacenterIdShift;
        id |= drip.getWorkerId() << workerIdShift;
        id |= drip.getSequence() << sequenceShift;
        id |= drip.getGeneId();
        return id;
    }

    public long addGene(long id, long geneId) {
        return (~geneMask & id) | (geneMask & geneId);
    }

    protected void assemble(Drip drip) {
        lock.lock();
        try {
            long timestamp = timeGen();
            if (timestamp < lastTimestamp)
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));

            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask;
                if (sequence == 0) timestamp = tilNextMillis(lastTimestamp);
            } else {
                sequence = 0L;
            }
            drip.setTimestamp(timestamp);
            drip.setSequence(sequence);

            lastTimestamp = timestamp;
        } finally {
            lock.unlock();
        }
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }


}
