package com.concept.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestDatabaseService {

    private final RequestRepository requestRepository;

    public RequestEntity saveRequest(String requestId, String uri, LocalDateTime timestamp, int httpStatus,
            String countryCode, String isp, long elapsedTime) {

        final RequestEntity request = new RequestEntity(requestId, uri, timestamp, httpStatus,
                countryCode, isp, elapsedTime);

        return requestRepository.save(request);
    }
}
