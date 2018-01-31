package com.github.serenity.drip;

import com.github.serenity.drip.base.Drip;
import com.github.serenity.drip.base.DripWorker;
import com.github.serenity.drip.base.RippleWorker;
import com.github.serenity.drip.support.PerformRunner;
import com.github.serenity.drip.support.RsaHelper;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.IntStream;

public class TestSimpleClient {

    private static DripWorker dripWorker = new DripWorker(30,15);

    private static RippleWorker rippleWorker = new RippleWorker();

    public static void main(String[] args) {
        try {
            PerformRunner.test(() -> dripWorker.nextId(), 4, 10);
            PerformRunner.test(() -> rippleWorker.nextId(), 1, 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddGene() {
        Long[] ids = new Long[100];
        for (int i = 0; i < 100; i+=2) {
            ids[i] = dripWorker.nextIdWithGene(3);
            ids[i+1] = dripWorker.addGene(dripWorker.nextId(), 3);
        }
        for (Long id : ids) {
            String s = Long.toBinaryString(id) + ":" + id + ":" + id % 16;
            print(s);
            print(dripWorker.resolveId(id));
        }
    }

    @Test
    public void testResolve() {
        while (true) {
            long id = dripWorker.nextId();
            print("1st gen:" + id + ".\n" + Long.toBinaryString(id));
            Drip drip = dripWorker.resolveId(id);
            print(drip);
            print("2nd gen:" + dripWorker.composeId(drip));
            if(id != dripWorker.composeId(drip)) {
                throw new RuntimeException();
            }
        }
    }

    @Test
    public void testRipple() {
        IntStream.range(0, 1000).forEach(i -> {
            String codec  = rippleWorker.nextId();
            print(codec + ":" + rippleWorker.decodeId(codec));
        });
    }


    @Test
    public void testRandomUnique() {
        Set<Long> set = new ConcurrentSkipListSet<>();

        RsaHelper helper = new RsaHelper(1192, 967L, 9999999L);
        print(helper.codec(3371));
        print(helper.decode(3371));

        IntStream.range(1, 4096).forEach(id -> {
            long codec = helper.codec(id);
            assert id == helper.decode(codec);
            print(id + ":" + String.format("%07d", codec));
            boolean add = set.add(codec);
            if (!add) {
                print(id + ":" + codec);
                throw new RuntimeException();
            }
        });
    }


    private static void print(Object obj) {
        System.out.println(obj);
    }
}
