package com.bot.crawler.exception;

import lombok.Value;

@Value
public class ValidationException extends RuntimeException {
    String message;
}
