package com.learn.learnredis;

import com.learn.learnredis.entity.User;
import com.learn.learnredis.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class LearnRedisApplicationTests {
    @Resource
    private RedisUtil redisUtil;

    @Test
    void contextLoads() {
        User user = new User();
        user.setAge(18);
        user.setName("shaoshi");
        user.setSex(1);
        user.setNickname("bigshaoshi");
        user.setUsername("891616942");
        user.setPassword("1314@Wang");
        redisUtil.set("hello",user);
        Object hello = redisUtil.get("hello");
        System.out.println("hello");
    }

}
