package com.concept.ip;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IpInfo(@JsonProperty("countryCode") String countryCode, @JsonProperty("isp") String isp) {}
