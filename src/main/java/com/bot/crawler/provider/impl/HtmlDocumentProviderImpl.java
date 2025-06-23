package com.bot.crawler.provider.impl;

import com.bot.crawler.provider.ResourceProvider;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.bot.crawler.constants.Constants.ATTR_HREF;
import static com.bot.crawler.constants.Constants.ELEMENT_HREF;
import static com.bot.crawler.util.Validator.isRootPath;

@Slf4j
@Service
public class HtmlDocumentProviderImpl implements ResourceProvider {

    @Value("${crawler.source-connect-timeout}")
    int timeout; // Connect timeout in seconds

    @Override
    public Set<String> findChildLinks(String hyperlink) {
        log.debug("Current thread {}", Thread.currentThread());
        Document document;
        try {
            document = Jsoup.connect(hyperlink).timeout(timeout * 1000).get();
        } catch (IOException e) {
            log.error("Failed to connect to URL: {}", hyperlink, e);
            return Set.of();
        }

        Set<String> childLinks = new HashSet<>();
        Elements hrefElements = document.select(ELEMENT_HREF);

        for (Element hrefElement : hrefElements) {
            // Skip if there is home page relative URL present on this page. Eg: site logo
            if (isRootPath(hrefElement.attr(ATTR_HREF))) {
                log.debug("Skipping root URL '/'");
                continue;
            }

            // Skip if the URL is invalid or blank
            if (!StringUtils.hasText(hrefElement.attr(ATTR_HREF))) {
                log.info("Blank href attribute for link {}", hrefElement);
                continue;
            }

            // Convert the href attribute of element into an absolute hyperlink
            String childLink = hrefElement.absUrl(ATTR_HREF);
            childLinks.add(childLink);
        }
        return childLinks;
    }
}
