package com.github.serenity.drip.service;

import com.github.serenity.drip.base.Drip;
import com.github.serenity.drip.base.DripWorker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class DripServiceImpl implements IDripService {

    private DripWorker dripWorker;

    public void setDripWorker(DripWorker dripWorker) {
        this.dripWorker = dripWorker;
    }

    @Override
    public String newId() {
        return String.valueOf(dripWorker.nextId());
    }

    @Override
    public Drip resolveId(long id) {
        return dripWorker.resolveId(id);
    }

    @Override
    public String composeId(Drip drip) {
        return String.valueOf(dripWorker.composeId(drip));
    }

    @Override
    public String[] batchNewId(int batchSize) {
        if (batchSize <= 0) return null;
        String[] ids = new String[batchSize];
        IntStream.range(0, batchSize).forEach(i -> ids[i] = newId());
        return ids;
    }

    @Override
    public Map<String, Drip> batchResolveId(String[] ids) {
        Map<String, Drip> map = new HashMap<>();
        Arrays.stream(ids).forEach(id -> map.put(id, dripWorker.resolveId( Long.valueOf(id))));
        return map;
    }

    @Override
    public String newIdWithGene(long geneId) {
        return String.valueOf(dripWorker.nextIdWithGene(geneId));
    }

    @Override
    public String[] batchNewIdWithGene(int batchSize, long geneId) {
        if (batchSize <= 0) return null;
        String[] ids = new String[batchSize];
        IntStream.range(0, batchSize).forEach(i -> ids[i] = newIdWithGene(geneId));
        return ids;
    }
}
