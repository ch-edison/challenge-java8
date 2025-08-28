package com.edisonchavez.challenge.starwars.service;

import com.edisonchavez.challenge.config.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
        "security.jwt.secret=E7sJr3VjKkG4p2mQ9wA1tZc6R8uM5nX0B4fH7kP2sD9yL3eT6qW8rY1uI3oP6aZ",
        "security.jwt.expiration-minutes=60"
})
class JwtUtilTest {

    @Autowired
    JwtUtil jwt;

    @Test
    void generate_and_validate_subject_ok() {
        String token = jwt.generateToken("user");
        String sub = jwt.validateAndGetSubject(token);
        assertEquals("user", sub);
    }
}