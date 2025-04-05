package com.bezkoder.spring.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.bezkoder.spring.restapi.service.MetricsFileProperties;

@SpringBootApplication
//@EnableScheduling // Enable @Scheduled tasks
@EnableConfigurationProperties(MetricsFileProperties.class) // Registers MetricsFileProperties for injection (used in MetricsFileWriter)
public class SpringBoot3RestApiExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBoot3RestApiExampleApplication.class, args);
    }
}
