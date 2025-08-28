package com.edisonchavez.challenge.starwars.integrations;

import com.edisonchavez.challenge.config.JwtUtil;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
        "spring.profiles.active=test",
        "spring.cache.type=simple",
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration",
        "starwars.base-url=http://localhost:${wiremock.server.port}",
        "security.jwt.secret=E7sJr3VjKkG4p2mQ9wA1tZc6R8uM5nX0B4fH7kP2sD9yL3eT6qW8rY1uI3oP6aZ",
        "security.jwt.expiration-minutes=60"
})
class FilmsIntegrationTest {

    @Autowired
    TestRestTemplate rest;
    @Autowired
    JwtUtil jwt;

    @Test
    void unauthorizedWithoutToken() {
        ResponseEntity<String> resp = rest.getForEntity("/api/films", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void listOkWithCache() {
        WireMock.stubFor(WireMock.get("/api/films")
                .willReturn(WireMock.aResponse().withHeader("Content-Type","application/json")
                        .withBody("{\"message\":\"ok\",\"result\":[{\"properties\":{\"title\":\"A New Hope\",\"episode_id\":4,\"url\":\"https://www.swapi.tech/api/films/1\"}},{\"properties\":{\"title\":\"The Empire Strikes Back\",\"episode_id\":5,\"url\":\"https://www.swapi.tech/api/films/2\"}}]}")));

        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(jwt.generateToken("luke"));
        HttpEntity<Void> req = new HttpEntity<>(h);

        ResponseEntity<String> r1 = rest.exchange("/api/films?page=0&size=1&search=hope", HttpMethod.GET, req, String.class);
        assertEquals(HttpStatus.OK, r1.getStatusCode());

        ResponseEntity<String> r2 = rest.exchange("/api/films?page=0&size=1&search=hope", HttpMethod.GET, req, String.class);
        assertEquals(HttpStatus.OK, r2.getStatusCode());

        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/api/films")));
    }
}