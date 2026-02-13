package com.concept;

import com.concept.ip.validation.validator.CountryValidator;
import com.concept.ip.validation.validator.IspValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({CountryValidator.class, IspValidator.class})
@SpringBootApplication
public class TechTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechTestApplication.class, args);
    }
}
