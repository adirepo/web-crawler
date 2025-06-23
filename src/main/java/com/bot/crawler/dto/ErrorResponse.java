package com.bot.crawler.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ErrorResponse {
    String message;
    LocalDateTime timestamp;
}
