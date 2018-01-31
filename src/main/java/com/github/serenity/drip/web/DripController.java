package com.github.serenity.drip.web;

import com.github.serenity.drip.base.Drip;
import com.github.serenity.drip.service.IDripService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 返回统一为String,以避免Java序列化json时long型数值不准确
 * 问题描述:
 * java中long能表示的范围比js中number大,也就意味着部分数值在js中存不下(变成不准确的值).
 * https://stackoverflow.com/questions/17320706/javascript-long-integer
 *
 * 其他方案:
 * 使用注解@JsonSerialize(using=ToStringSerializer.class)
 * https://www.cnblogs.com/Fly-Bob/p/7218006.html
 */
@Api
@RestController
public class DripController {

    @Resource(name = "dripService")
    private IDripService dripService;

    @RequestMapping(value = "/newId", method = RequestMethod.GET)
    public String newId() {
        return dripService.newId();
    }

    @RequestMapping(value = "/resolveId", method = RequestMethod.GET)
    public Drip resolveId(long id) {
        return dripService.resolveId(id);
    }

    @RequestMapping(value = "/batchNewId", method = RequestMethod.GET)
    public String[] batchNewId(int batchSize) {
        return dripService.batchNewId(batchSize);
    }

    @RequestMapping(value = "/batchResolveId", method = RequestMethod.GET)
    public Map<String, Drip> batchResolveId(String[] ids) {
        return dripService.batchResolveId(ids);
    }

    @RequestMapping(value = "/newIdWithGene", method = RequestMethod.GET)
    public String newIdWithGene(long geneId) {
        return dripService.newIdWithGene(geneId);
    }

    @RequestMapping(value = "/batchNewIdWithGene", method = RequestMethod.GET)
    public String[] batchNewIdWithGene(int batchSize, long geneId) {
        return dripService.batchNewIdWithGene(batchSize, geneId);
    }

    @RequestMapping(value = "/composeId", method = RequestMethod.POST)
    public String composeId(@RequestBody Drip drip) {
        return dripService.composeId(drip);
    }
}
