package com.bot.crawler.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ValidationException extends RuntimeException {
    String message;
}
