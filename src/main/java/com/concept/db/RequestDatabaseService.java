package com.concept.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestDatabaseService {

    private final RequestRepository requestRepository;

    public RequestEntity saveRequest(String requestId, String uri, LocalDateTime timestamp, int httpStatus, String requestIpAddress, String countryCode, String isp, long elapsedTimeMs) {

        final RequestEntity request = new RequestEntity(requestId, uri, timestamp, httpStatus, requestIpAddress, countryCode, isp, elapsedTimeMs);

        return requestRepository.save(request);
    }
}
