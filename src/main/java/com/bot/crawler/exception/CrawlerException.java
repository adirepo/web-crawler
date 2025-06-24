package com.bot.crawler.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Custom exception class for exception handling across application
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrawlerException extends RuntimeException {
    private final String message;
    private final Exception e;
}
