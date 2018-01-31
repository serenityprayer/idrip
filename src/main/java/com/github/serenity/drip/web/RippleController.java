package com.github.serenity.drip.web;

import com.github.serenity.drip.base.RippleWorker;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Api
@RestController
public class RippleController {

    private RippleWorker worker = new RippleWorker();

    @RequestMapping(value = "/random", method = RequestMethod.GET)
    public String[] random(int batchSize) {
        if (batchSize <= 0) return null;
        String[] ids = new String[batchSize];
        IntStream.range(0, batchSize).forEach(i -> ids[i] = worker.nextId());
        return ids;
    }

    @RequestMapping(value = "/decode", method = RequestMethod.GET)
    public Map<String, String> batchResolveId(String randomIds) {
        String[] ids = randomIds.split(",");
        Map<String, String> map = new HashMap<>();
        Arrays.stream(ids).forEach(id -> map.put(id.trim(), worker.decodeId(id.trim())));
        return map;
    }



}
