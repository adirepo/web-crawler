package com.bot.crawler.service;

import java.util.Set;

public interface CrawlerService {
    Set<String> scanTarget(String target);
}
