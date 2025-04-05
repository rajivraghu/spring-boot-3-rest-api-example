package com.bezkoder.spring.restapi.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class MetricsFileWriter {

    private static final Logger logger = LoggerFactory.getLogger(MetricsFileWriter.class);
    private final MeterRegistry meterRegistry;
    private final MetricsFileProperties metricsFileProperties;
    private final DateTimeFormatter timestampFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z").withZone(ZoneId.systemDefault());

    @Autowired
    public MetricsFileWriter(MeterRegistry meterRegistry, MetricsFileProperties metricsFileProperties) {
        this.meterRegistry = meterRegistry;
        this.metricsFileProperties = metricsFileProperties;
    }

    public void writeMetricsToFile() {
        // Use the file path from properties
        String filePath = metricsFileProperties.getPath();
        logger.info("Attempting to write metrics to file: {}", filePath);
        logger.info("Number of meters in registry: {}", meterRegistry.getMeters().size());
        if (!meterRegistry.getMeters().isEmpty()) {
            logger.info("Sample meter names: {}",
                meterRegistry.getMeters().stream().limit(5).map(m -> m.getId().getName()).toList());
        } else {
            logger.warn("MeterRegistry is empty!");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("--------------------------------------------------------------------\n");
            writer.write("Metrics Report - " + timestampFormatter.format(Instant.now()) + "\n");
            writer.write("--------------------------------------------------------------------\n");

            for (Meter meter : meterRegistry.getMeters()) {
                writer.write("Meter Name: " + meter.getId().getName() + "\n");
                if (!meter.getId().getTags().isEmpty()) {
                    writer.write("  Tags: ");
                    for (Tag tag : meter.getId().getTags()) {
                        writer.write(tag.getKey() + "=" + tag.getValue() + ", ");
                    }
                    writer.write("\n");
                }
                for (Measurement measurement : meter.measure()) {
                    writer.write("  Measurement: " + measurement.getStatistic().toString() +
                                 ", Value: " + measurement.getValue() + "\n");
                }
                writer.write("\n");
            }
            writer.write("--------------------------------------------------------------------\n\n");
            logger.info("Metrics written to file SUCCESSFULLY: {}", filePath);
        } catch (IOException e) {
            logger.error("Error writing metrics to file: {}", filePath, e);
        }
    }
}
