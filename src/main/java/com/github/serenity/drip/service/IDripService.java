package com.github.serenity.drip.service;

import com.github.serenity.drip.base.Drip;

import java.util.Map;

public interface IDripService {

    String newId();

    Drip resolveId(long id);

    String composeId(Drip drip);

    String[] batchNewId(int batchSize);

    Map<String, Drip> batchResolveId(String[] ids);

    // 分库基因埋点(如uid%8分库,则调整预留3bit)
    String newIdWithGene(long geneId);

    String[] batchNewIdWithGene(int batchSize, long geneId);

}
