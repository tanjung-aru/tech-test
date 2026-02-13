package com.concept.ip.rest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

class IpLookupTest {

    private final WireMockServer wireMockServer = new WireMockServer(0);
    private IpLookup ipLookup;

    @BeforeEach
    void setUp() {
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        ipLookup = new IpLookup("http://localhost:" + wireMockServer.port() + "/");
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void assert_that_lookup_service_calls_endpoint() throws IOException, InterruptedException {

        String ipAddress = "1.2.3.4";
        String response = """
                {
                  "countryCode": "US",
                  "isp": "AWS"
                }
                """;
        stubFor(WireMock.get(urlPathEqualTo("/" + ipAddress))
                .withQueryParam("fields", equalTo("countryCode,isp"))
                .willReturn(okJson(response)));

        IpInfo ipInfo = ipLookup.getIpInfo(ipAddress);

        Assertions.assertThat(ipInfo.countryCode()).isEqualTo("US");
        Assertions.assertThat(ipInfo.isp()).isEqualTo("AWS");
    }
}