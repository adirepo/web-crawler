package com.bot.crawler.service;

import java.util.Set;

public interface CrawlerService {

    /**
     * Scans (crawls) all the hyperlinks for the given target URL
     * @param target the target or main URL to start scan from
     * @return the set of all the traced hyperlinks (URLs)
     */
    Set<String> scanTarget(String target);
}
