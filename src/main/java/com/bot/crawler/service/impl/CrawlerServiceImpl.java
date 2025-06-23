package com.bot.crawler.service.impl;

import com.bot.crawler.provider.ResourceProvider;
import com.bot.crawler.service.CrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.TreeSet;

import static com.bot.crawler.util.Validator.hasSameDomain;
import static com.bot.crawler.util.Validator.isNotYetScanned;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {

    private final ResourceProvider resourceProvider;

    @Value("${crawler.external-links-capture.enable}")
    boolean externalLinkCaptureEnabled;

    @Value("${crawler.depth-based-scan.enable}")
    boolean isDepthBasedScanEnabled;

    @Override
    public Set<String> scanTarget(String target, int depth) {
        log.info("Starting scan of target URL {}", target);
        Set<String> scannedLinks = new TreeSet<>();
        this.scanPage(target, scannedLinks, depth);
        log.info("Finished scan with {} crawled URLs for target {}", scannedLinks.size(), target);
        return scannedLinks;
    }

    private void scanPage(String currentLink, Set<String> scannedLinks, int depth) {
        log.debug("Scanning page: {}", currentLink);

        // Capture the current link
        scannedLinks.add(currentLink);

        // If depth based scan is enabled and the depth is reached then return
        if (isDepthBasedScanEnabled && depth <= 0) {
            log.info("The scan has reached requested depth");
            return;
        }

        // Read the page and fetch the hyperlink elements from it
        Set<String> childLinks = resourceProvider.findChildLinks(currentLink);

        for(String childLink : childLinks) {
            log.debug("Current hyperlink: {}, child hyperlink: {}", currentLink, childLink);

            // If the found URL is not yet scanned and is from the same domain, then proceed with scan for child pages.
            // Else, just capture the URL as external URL.
            boolean isNotYetScanned = isNotYetScanned(childLink, scannedLinks);

            if (isNotYetScanned && hasSameDomain(currentLink, childLink)) {
                scanPage(childLink, scannedLinks, depth - 1); // Scan the child pages
            } else if (isNotYetScanned && externalLinkCaptureEnabled) {
                scannedLinks.add(childLink); // Capture the (external) hyperlink but don't scan it
            }
        }
    }
}
