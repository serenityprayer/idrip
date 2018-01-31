package com.github.serenity.drip;

import com.github.serenity.drip.base.DripWorker;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestSnowflake {

    static Set<Long> set = new ConcurrentSkipListSet<>();

    public static void main(String[] args) throws Exception {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        DripWorker idWorker = new DripWorker(4, 2);

        for (int i = 0; i < 10; i++) {
            fixedThreadPool.execute(() -> {
                while (true) {
                    long l = idWorker.nextIdWithGene(7878);
                    boolean add = set.add(l);
                    if (!add) {
                        System.out.println(System.currentTimeMillis() + ";" + l + ":" + Long.toBinaryString(l));
                        System.exit(0);
                    }
                }
            });
        }
    }
}
