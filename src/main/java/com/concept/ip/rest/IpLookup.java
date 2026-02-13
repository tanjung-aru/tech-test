package com.concept.ip.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class IpLookup {

    private static final String QUERY_PARAMS = "?fields=" + URLEncoder.encode("countryCode,isp", StandardCharsets.UTF_8);
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String urlBase;

    public IpLookup(@Value("${iplookup.url:http://ip-api.com/json/}") String urlBase) {
        this.urlBase = urlBase;
    }

    public IpInfo getIpInfo(String ipAddress) throws IOException, InterruptedException {

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + ipAddress + QUERY_PARAMS))
                .GET()
                .build();

        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(), IpInfo.class);
    }
}
