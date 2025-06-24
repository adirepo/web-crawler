package com.bot.crawler.service.impl;

import com.bot.crawler.config.CrawlerConfig;
import com.bot.crawler.exception.CrawlerException;
import com.bot.crawler.provider.ResourceProvider;
import com.bot.crawler.service.CrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.bot.crawler.constants.Constants.ZERO;
import static com.bot.crawler.util.Validator.hasSameDomain;
import static com.bot.crawler.util.Validator.isNotYetScanned;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {

    private final ResourceProvider resourceProvider;

    private final CrawlerConfig config;

    @Override
    public Set<String> scanTarget(String target, int depth) {
        log.info("Starting scan of target URL {}", target);
        Set<String> scannedLinks = ConcurrentHashMap.newKeySet();
        this.scanPage(target, scannedLinks, depth);
        log.info("Finished scan with {} crawled URLs for target {}", scannedLinks.size(), target);
        return scannedLinks;
    }

    private void scanPage(String currentLink, Set<String> scannedLinks, int depth) {
        log.debug("Scanning page: {}", currentLink);

        // Capture the current link
        scannedLinks.add(currentLink);

        // If depth based scan is enabled and the depth is reached then return
        if (config.getDepthBasedScan().isEnable() && depth <= ZERO) {
            log.info("The scan has reached requested depth");
            return;
        }

        // Read the page and fetch the hyperlink elements from it
        Set<String> childLinks = resourceProvider.findChildLinks(currentLink);
        if (childLinks == null || childLinks.size() <= ZERO) {
            return;
        }

        // Making use of multithreaded execution to improve scalability of crawling
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(childLinks.size());
        List<Future<Void>> futures = new ArrayList<>();

        // If rate limiting is enabled then use the configured 'delay', else default the delay to 0 seconds
        int delaySeconds = config.getRateLimit().isEnable() ? config.getRateLimit().getDelay() : ZERO;

        for (String childLink : childLinks) {
            log.debug("Current hyperlink: {}, child hyperlink: {}", currentLink, childLink);

            // If the found URL is not yet scanned and is from the same domain, then proceed with scan for child pages.
            // Else, just capture the URL as an external URL.
            boolean isNotYetScanned = isNotYetScanned(childLink, scannedLinks);

            if (isNotYetScanned && hasSameDomain(currentLink, childLink)) {
                futures.add(executor.schedule(() -> {
                    try {
                        scanPage(childLink, scannedLinks, depth - 1); // Scan the child pages
                    } catch (CrawlerException e) {
                        log.error("Failed to scan link {}, will skip processing this link", childLink, e);
                    }
                    return null; // Returning 'null' for future since method does not return any value
                }, delaySeconds, TimeUnit.SECONDS));
            } else if (isNotYetScanned && config.getExternalLinksCapture().isEnable()) {
                scannedLinks.add(childLink); // Capture the (external) hyperlink but don't scan it
            }
        }

        // Once all scans are submitted to thread pool, wait for their completion before closing
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Unable to complete the execution: ", e);
            }
        }

        // Close the thread pool
        executor.shutdown();
    }
}
