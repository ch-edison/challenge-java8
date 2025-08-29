package com.edisonchavez.challenge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "security.jwt.secret=E7sJr3VjKkG4p2mQ9wA1tZc6R8uM5nX0B4fH7kP2sD9yL3eT6qW8rY1uI3oP6aZ",
                "security.jwt.expiration-minutes=60",
                "spring.cache.type=simple",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration",
                "spring.redis.url=",
                "spring.data.redis.url="
        }
)
class ChallengeApplicationTests {

    @Test
    void contextLoads() {
    }

}
