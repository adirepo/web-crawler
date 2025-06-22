package com.bot.crawler.service.impl;

import com.bot.crawler.service.CrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

import static com.bot.crawler.constants.Constants.ATTR_HREF;
import static com.bot.crawler.constants.Constants.ELEMENT_HREF;
import static com.bot.crawler.constants.Constants.ROOT_PATH;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {


    private final RestTemplate restTemplate;

    @Value("${crawler.external-links-capture.enable}")
    boolean externalLinkCaptureEnabled;

    @Override
    public Set<String> scanTarget(String target) {
        log.debug("Starting scan of target URL {}", target);
        Set<String> scannedLinks = new TreeSet<>();
        this.scanPage(target, scannedLinks);
        return scannedLinks;
    }

    private void scanPage(String currentLink, Set<String> scannedLinks) {
        log.debug("Scanning page: {}", currentLink);

        // Parse the page and fetch the hyperlink elements from it
        Document document;
        try {
            document = Jsoup.connect(currentLink).get();
        } catch (IOException e) {
            log.error("Failed to connect to URL: {}", currentLink, e);
            return;
        }

        // Capture the current link
        scannedLinks.add(currentLink);

        Elements hrefElements = document.select(ELEMENT_HREF);

        for (Element hrefElement : hrefElements) {
            // Skip if there is home page relative URL present on this page. Eg: site logo
            if (isRootPath(hrefElement.attr(ATTR_HREF))) {
                log.debug("Skipping root URL /");
                continue;
            }

            // Return if the URL is invalid or blank
            if (!StringUtils.hasText(hrefElement.attr(ATTR_HREF))) {
                log.debug("Blank href attribute for link {}", hrefElement);
                return;
            }

            // Convert the element hrefElement into an absolute hyperlink
            String subLink = hrefElement.absUrl(ATTR_HREF);
            log.debug("Current hyperlink: {}, Sub hyperlink: {}", currentLink, subLink);

            // If the found URL is not yet scanned and is from the same domain, then proceed with scan for subpages. Else, just capture the hrefElement.
            if (isNotYetScanned(subLink, scannedLinks) && isSameDomain(currentLink, subLink)) {
                scanPage(subLink, scannedLinks); // Scan the subpages
            } else if (isNotYetScanned(subLink, scannedLinks) && externalLinkCaptureEnabled) {
                scannedLinks.add(subLink); // Capture the external hyperlink but don't scan it
            }
        }
    }

/*    private String appendPath(String rootLink, String href) {
        href = href.replaceAll("/", "");
        return rootLink + "/" + href;
    }

    private boolean isURLAbsolute(String url) {
        boolean isAbsolute = false;
        try {
            URI uri = new URI(url);
            if (uri.isAbsolute()) {
                isAbsolute = true;
            }
        } catch (URISyntaxException e) {
            log.error("Invalid page path {}", url, e);
        }
        log.debug("The URL {} is absolute: {}", url ,isAbsolute);
        return isAbsolute;
    }*/

    private boolean isNotYetScanned(String subLink, Set<String> scannedLinks) {
        return !scannedLinks.contains(subLink);
    }

    private boolean isSameDomain(String parentLink, String subLink) {
        String domain;
        try {
            URL url = new URL(parentLink);
            domain = url.getHost();
        } catch (MalformedURLException e) {
            log.error("The URL '{}' is invalid", parentLink);
            return false;
        }
        return subLink.contains(domain);
    }

    private boolean isRootPath(String href) {
        return href.trim().equals(ROOT_PATH);
    }
}
