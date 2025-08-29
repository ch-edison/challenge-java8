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
class VehicleIntegrationTest {

    @Autowired
    TestRestTemplate rest;

    @Autowired
    JwtUtil jwt;

    @Test
    void listVehiclesOkWithFilter() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/vehicles"))
                .withQueryParam("page", WireMock.equalTo("1"))
                .withQueryParam("limit", WireMock.equalTo("1"))
                .withQueryParam("name", WireMock.equalTo("San"))
                .willReturn(WireMock.okJson(
                        "{\"message\":\"ok\",\"result\":[{\"properties\":{\"created\":\"2025-08-28T16:42:09.675Z\",\"edited\":\"2025-08-28T16:42:09.675Z\",\"consumables\":\"2 months\",\"name\":\"Sand Crawler\",\"cargo_capacity\":\"50000\",\"passengers\":\"30\",\"max_atmosphering_speed\":\"30\",\"crew\":\"46\",\"length\":\"36.8 \",\"model\":\"Digger Crawler\",\"cost_in_credits\":\"150000\",\"manufacturer\":\"Corellia Mining Corporation\",\"vehicle_class\":\"wheeled\",\"pilots\":[],\"films\":[\"https://www.swapi.tech/api/films/1\",\"https://www.swapi.tech/api/films/5\"],\"url\":\"https://www.swapi.tech/api/vehicles/4\"},\"_id\":\"5f63a160cf50d100047f97fc\",\"description\":\"A vehicle\",\"uid\":\"4\",\"__v\":2}],\"apiVersion\":\"1.0\",\"timestamp\":\"2025-08-28T22:16:49.064Z\",\"support\":{\"contact\":\"admin@swapi.tech\",\"donate\":\"https://www.paypal.com/donate/?business=2HGAUVTWGR5T2&no_recurring=0&item_name=Support+Swapi+and+keep+the+galaxy%27s+data+free%21+Your+donation+fuels+open-source+innovation+and+helps+us+grow.+Thank+you%21+%F0%9F%9A%80&currency_code=USD\",\"partnerDiscounts\":{\"saberMasters\":{\"link\":\"https://www.swapi.tech/partner-discount/sabermasters-swapi\",\"details\":\"Use this link to automatically get $10 off your purchase!\"},\"heartMath\":{\"link\":\"https://www.heartmath.com/ryanc\",\"details\":\"Looking for some Jedi-like inner peace? Take 10% off your heart-brain coherence tools from the HeartMath Institute!\"}}},\"social\":{\"discord\":\"https://discord.gg/zWvA6GPeNG\",\"reddit\":\"https://www.reddit.com/r/SwapiOfficial/\",\"github\":\"https://github.com/semperry/swapi/blob/main/CONTRIBUTORS.md\"}}"
                )));

        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(jwt.generateToken("admin"));

        ResponseEntity<String> resp = rest.exchange(
                "/api/vehicles?page=0&size=1&name=San",
                HttpMethod.GET,
                new HttpEntity<>(h),
                String.class
        );

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void getVehicleOk() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/vehicles/4"))
                .willReturn(WireMock.okJson(
                        "{\"message\":\"ok\",\"result\":{\"properties\":{\"name\":\"Sand Crawler\",\"cargo_capacity\":\"50000\"}}}"
                )));

        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(jwt.generateToken("admin"));

        ResponseEntity<String> resp = rest.exchange(
                "/api/vehicles/4",
                HttpMethod.GET,
                new HttpEntity<>(h),
                String.class
        );
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void listVehiclesOkWithoutFilter() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/vehicles"))
                .willReturn(WireMock.okJson(
                        "{\"message\":\"ok\",\"total_records\":39,\"total_pages\":4,\"previous\":null,\"next\":\"https://www.swapi.tech/api/vehicles?page=2&limit=10\",\"results\":[{\"uid\":\"4\",\"name\":\"Sand Crawler\",\"url\":\"https://www.swapi.tech/api/vehicles/4\"}]}"
                )));

        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(jwt.generateToken("admin"));

        ResponseEntity<String> resp = rest.exchange(
                "/api/vehicles?page=0&size=10",
                HttpMethod.GET,
                new HttpEntity<>(h),
                String.class
        );
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
