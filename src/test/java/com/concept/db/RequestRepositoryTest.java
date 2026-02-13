package com.concept.db;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Test
    void assert_that_request_is_successfully_persisted_to_database() {
        String requestID = "requestID";
        String uri = "uri";
        LocalDateTime timestamp = LocalDateTime.of(2026, 1, 1, 0, 0);
        int httpStatus = 200;
        String requestIpAddress = "1.2.3.4";
        String countryCode = "US";
        String isp = "AWS";
        long elapsedTimeMs = 10;
        RequestEntity request = new RequestEntity(requestID, uri, timestamp, httpStatus, requestIpAddress, countryCode, isp, elapsedTimeMs);

        requestRepository.save(request);

        Optional<RequestEntity> optionallyPersistedEntity = requestRepository.findById(request.getId());
        Assertions.assertThat(optionallyPersistedEntity).isPresent();
        RequestEntity persistedEntity = optionallyPersistedEntity.get();
        Assertions.assertThat(persistedEntity.getRequestId()).isEqualTo(requestID);
        Assertions.assertThat(persistedEntity.getUri()).isEqualTo(uri);
        Assertions.assertThat(persistedEntity.getTimestamp()).isEqualTo(timestamp);
        Assertions.assertThat(persistedEntity.getHttpStatus()).isEqualTo(httpStatus);
        Assertions.assertThat(persistedEntity.getRequestIpAddress()).isEqualTo(requestIpAddress);
        Assertions.assertThat(persistedEntity.getCountryCode()).isEqualTo(countryCode);
        Assertions.assertThat(persistedEntity.getIsp()).isEqualTo(isp);
        Assertions.assertThat(persistedEntity.getElapsedTimeMs()).isEqualTo(elapsedTimeMs);
    }
}