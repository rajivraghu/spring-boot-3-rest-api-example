package com.bezkoder.spring.restapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class MetricsFileScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MetricsFileScheduler.class);
    private final MetricsFileWriter metricsFileWriter;

    @Autowired
    public MetricsFileScheduler(MetricsFileWriter metricsFileWriter) {
        this.metricsFileWriter = metricsFileWriter;
    }

    @Scheduled(fixedRateString = "#{T(java.time.Duration).parse('${metrics.file.reporting-interval}').toMillis()}")
    public void scheduleWriteMetrics() {
        logger.debug("Scheduled metrics file write triggered.");
        metricsFileWriter.writeMetricsToFile();
    }
}
