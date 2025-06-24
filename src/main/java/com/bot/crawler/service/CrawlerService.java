package com.bot.crawler.service;

import java.util.Set;

/**
 * The service interface for implementing functional logic
 */
public interface CrawlerService {

    /**
     * Scans (crawls) all the hyperlinks for the given target URL
     *
     * @param target
     *            the target or main URL to start scan from
     * @param depth
     *            the optional depth value till which the target should be scanned
     *
     * @return the set of all the traced hyperlinks (URLs)
     */
    Set<String> scanTarget(String target, int depth);
}
