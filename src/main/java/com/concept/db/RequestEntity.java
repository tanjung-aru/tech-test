package com.concept.db;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "request")
public class RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String requestId;
    private String uri;
    private LocalDateTime timestamp;
    private int httpStatus;
    private String countryCode;
    private String isp;
    private long elapsedTimeMs;

    public RequestEntity(String requestId, String uri, LocalDateTime timestamp, int httpStatus,
                         String countryCode, String isp, long elapsedTimeMs) {
        this.requestId = requestId;
        this.uri = uri;
        this.timestamp = timestamp;
        this.httpStatus = httpStatus;
        this.countryCode = countryCode;
        this.isp = isp;
        this.elapsedTimeMs = elapsedTimeMs;
    }
}
