package com.concept;

import com.concept.db.RequestEntity;
import com.concept.db.RequestRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

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

    @Autowired
    RequestRepository requestRepository;

    @BeforeAll
    static void beforeAll() {
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @BeforeEach
    void beforeEach() {
        requestRepository.deleteAll();
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

    String configureIpLookupResponse(String responseJson) throws Exception {
        String ipAddress = getMockMvcRemoteIpAddress();
        stubFor(WireMock.get(urlPathEqualTo("/" + ipAddress))
                .withQueryParam("fields", equalTo("countryCode,isp"))
                .willReturn(okJson(responseJson)));
        return ipAddress;
    }

    void assertDatabaseEntry(String countryCode, String isp, HttpStatus httpStatus, String ipAddress) {
        List<RequestEntity> optionallyPersistedEntity = requestRepository.findAll();
        Assertions.assertThat(optionallyPersistedEntity.size()).isEqualTo(1);
        Assertions.assertThat(optionallyPersistedEntity.get(0).getCountryCode()).isEqualTo(countryCode);
        Assertions.assertThat(optionallyPersistedEntity.get(0).getIsp()).isEqualTo(isp);
        Assertions.assertThat(optionallyPersistedEntity.get(0).getHttpStatus()).isEqualTo(httpStatus.value());
        Assertions.assertThat(optionallyPersistedEntity.get(0).getRequestIpAddress()).isEqualTo(ipAddress);
        Assertions.assertThat(optionallyPersistedEntity.get(0).getUri()).isEqualTo("http://localhost/api/upload-entry-file");
    }

    private String getMockMvcRemoteIpAddress() throws Exception {
        return mockMvc.perform(get("/")).andReturn().getRequest().getRemoteAddr();
    }
}
