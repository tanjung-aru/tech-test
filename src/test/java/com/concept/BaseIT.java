package com.concept;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseIT {

    private static final WireMockServer wireMockServer = new WireMockServer(0);

    @Autowired
    MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() {
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureTempDirectory(DynamicPropertyRegistry registry) throws IOException {
        String tempDir = Files.createTempDirectory("EntryFileIT").toString();
        registry.add("files.upload.directory", () -> tempDir);
        registry.add("files.download.directory", () -> tempDir);
        registry.add("iplookup.url", () -> "http://localhost:" + wireMockServer.port() + "/");
    }

    void configureIpLookupResponse(String responseJson) throws Exception {
        String ipAddress = getMockMvcRemoteIpAddress();
        stubFor(WireMock.get(urlPathEqualTo("/" + ipAddress))
                .withQueryParam("fields", equalTo("countryCode,isp"))
                .willReturn(okJson(responseJson)));
    }

    private String getMockMvcRemoteIpAddress() throws Exception {
        return mockMvc.perform(get("/")).andReturn().getRequest().getRemoteAddr();
    }
}
