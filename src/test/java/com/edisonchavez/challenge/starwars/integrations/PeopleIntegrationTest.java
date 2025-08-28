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
class PeopleIntegrationTest {

    @Autowired
    TestRestTemplate rest;

    @Autowired
    JwtUtil jwt;

    @Test
    void listPeopleOkWithFilter() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/people"))
                .withQueryParam("page", WireMock.equalTo("1"))
                .withQueryParam("limit", WireMock.equalTo("1"))
                .withQueryParam("name", WireMock.equalTo("lu"))
                .willReturn(WireMock.okJson(
                        "{\"message\":\"ok\",\"result\":[" +
                                "{\"uid\":\"1\",\"properties\":{\"name\":\"Luke\",\"url\":\"https://www.swapi.tech/api/people/1\"}}" +
                                "]}"
                )));

        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(jwt.generateToken("luke"));

        ResponseEntity<String> resp = rest.exchange(
                "/api/people?page=0&size=1&name=lu",
                HttpMethod.GET,
                new HttpEntity<>(h),
                String.class
        );

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void getPersonOk() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/people/1"))
                .willReturn(WireMock.okJson(
                        "{\"message\":\"ok\",\"result\":{\"properties\":{\"name\":\"Luke\",\"url\":\"https://www.swapi.tech/api/people/1\"}}}"
                )));

        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(jwt.generateToken("luke"));

        ResponseEntity<String> resp = rest.exchange(
                "/api/people/1",
                HttpMethod.GET,
                new HttpEntity<>(h),
                String.class
        );
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void listPeopleOk_withoutFilter() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/people"))
                .willReturn(WireMock.okJson(
                        "{\"message\":\"ok\",\"total_records\":82,\"total_pages\":9," +
                                "\"results\":[{\"uid\":\"1\",\"name\":\"Luke\",\"url\":\"https://www.swapi.tech/api/people/1\"}]}"
                )));

        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(jwt.generateToken("luke"));

        ResponseEntity<String> resp = rest.exchange(
                "/api/people?page=0&size=10",
                HttpMethod.GET,
                new HttpEntity<>(h),
                String.class
        );
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
