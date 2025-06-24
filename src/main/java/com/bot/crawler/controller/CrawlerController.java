package com.bot.crawler.controller;

import com.bot.crawler.dto.CrawlResponse;
import com.bot.crawler.exception.ValidationException;
import com.bot.crawler.service.CrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * The main rest controller for request routing
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CrawlerController {

    private final CrawlerService crawlerService;

    @GetMapping("/pages")
    public ResponseEntity<CrawlResponse> crawlUrl(@RequestParam("target") String target,
            @RequestParam(name = "depth", required = false, defaultValue = "5") int depth) {
        if (!StringUtils.hasText(target)) {
            log.error("Target URL can not be blank");
            throw new ValidationException("Target URL can not be blank");
        }

        Set<String> pages = crawlerService.scanTarget(target, depth);
        CrawlResponse response = new CrawlResponse(target, pages);
        return ResponseEntity.ok(response);
    }
}
