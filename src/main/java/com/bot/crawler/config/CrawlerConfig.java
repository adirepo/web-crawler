package com.bot.crawler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration class to map the application configuration to object
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "crawler")
public class CrawlerConfig {

    private ExternalLinksCapture externalLinksCapture;
    private DepthBasedScan depthBasedScan;
    private RateLimit rateLimit;

    @Data
    public static class ExternalLinksCapture {
        private boolean enable;
    }

    @Data
    public static class DepthBasedScan {
        private boolean enable;
    }

    @Data
    public static class RateLimit {
        private boolean enable;
        private int delay;
    }
}
