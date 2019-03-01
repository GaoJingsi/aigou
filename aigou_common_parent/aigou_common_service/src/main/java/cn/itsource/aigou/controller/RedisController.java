package cn.itsource.aigou.controller;

import cn.itsource.aigou.client.RedisClient;
import cn.itsource.aigou.util.RedisUtil;
import org.springframework.web.bind.annotation.*;

//可以不实现接口:实现的目的:只是起一个约束的问题
@RestController
@RequestMapping("/common")
public class RedisController implements RedisClient {

    @RequestMapping(value = "/redis", method = RequestMethod.POST)
    @Override
    public void set(@RequestParam("key") String key, @RequestParam("value") String value) {
        RedisUtil.set(key, value);
    }

    @RequestMapping(value = "/redis/{key}", method = RequestMethod.GET)
    @Override
    public String get(@PathVariable("key") String key) {

        return RedisUtil.get(key);
    }

}

