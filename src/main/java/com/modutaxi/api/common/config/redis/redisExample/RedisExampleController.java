package com.modutaxi.api.common.config.redis.redisExample;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
public class RedisExampleController {
    private final RedisExampleRepository redisExampleRepository;
    @GetMapping("/save/{key}/{value}")
    public String save(@PathVariable("key") String key, @PathVariable("value") String value) {
        RedisExampleDomain redisExampleDomain = new RedisExampleDomain();
        redisExampleDomain.setKey(key);
        redisExampleDomain.setValue(value);
        String res = redisExampleRepository.save(redisExampleDomain);
        return res;
    }

    @GetMapping("/get/{key}")
    public RedisExampleDomain get(@PathVariable("key") String key) {
        RedisExampleDomain res = redisExampleRepository.findById(key);
        return res;
    }
}
