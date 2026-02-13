package com.concept.ip;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static final String BASE_URL = "http://ip-api.com/json/";
    private static final String QUERY_PARAMS = "?fields=" + URLEncoder.encode("countryCode,isp", StandardCharsets.UTF_8);
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public IpInfo getIpInfo(String ipAddress) throws IOException, InterruptedException {

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + ipAddress + QUERY_PARAMS))
                .GET()
                .build();


        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(), IpInfo.class);
    }
}
