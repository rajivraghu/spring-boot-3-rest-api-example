package com.bezkoder.spring.restapi.service;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "metrics.file")
public class MetricsFileProperties {

    private String path = "metrics.log"; // Default file path
    private Duration reportingInterval = Duration.ofSeconds(30); // Default reporting interval

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Duration getReportingInterval() {
        return reportingInterval;
    }
    public void setReportingInterval(Duration reportingInterval) {
        this.reportingInterval = reportingInterval;
    }
}
