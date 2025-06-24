package com.bot.crawler.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CrawlResponse {
    private final String domain;
    private final Set<String> pages;
}
