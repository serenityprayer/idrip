package com.github.serenity.drip.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能测试工具类
 */
public class PerformRunner {

    private int threadCount;

    private int executeTime;

    private Runnable runnable;

    final AtomicLong count = new AtomicLong(0);
    final AtomicBoolean end = new AtomicBoolean(false);

    public static void test(Runnable runnable, Integer count, Integer time) throws InterruptedException {
        PerformRunner performTest = new PerformRunner();
        performTest.setRunnable(runnable);
        performTest.setThreadCount(count);
        performTest.setExecuteTime(time);
        performTest.start();
    }

    private void start() throws InterruptedException {
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    count.incrementAndGet();
                    runnable.run();
                    if (end.get()) return;
                }
            });
            thread.start();
            list.add(thread);
        }

        final long start = System.currentTimeMillis();
        Thread g = new Thread(() -> {
            while (true) {
                long t = count.get();
                long z = System.currentTimeMillis() - start;
                if (t > 0 && z > 0) {
                    System.out.println("tps:" + (t * 1000 / z));
                }
                try {
                    Thread.sleep(1000);
                    if (z > executeTime * 1000) {
                        end.set(true);
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        g.start();

        for (Thread thread : list) {
            thread.join();
        }
        System.out.println("END. total: " + count.get());
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount == null ? 1 : threadCount;
    }

    public void setExecuteTime(Integer executeTime) {
        this.executeTime = executeTime == null ? 10 : executeTime;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
}
