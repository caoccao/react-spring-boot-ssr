package com.caoccao.javet.demo.react.ssr;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestSsrController {
    @LocalServerPort
    protected int port;
    @Resource
    protected TestRestTemplate restTemplate;
    @Resource
    protected SsrController ssrController;

    @Test
    public void testController() {
        assertNotNull(ssrController);
    }

    @Test
    public void testRenderByCjs() {
        assertThat(
                restTemplate.getForObject("http://localhost:" + port + "/render-by-cjs", String.class))
                .contains("React + Spring Boot + Javet");
    }

    @Test
    public void testRenderByEsm() {
        assertThat(
                restTemplate.getForObject("http://localhost:" + port + "/render-by-esm", String.class))
                .contains("React + Spring Boot + Javet");
    }
}
