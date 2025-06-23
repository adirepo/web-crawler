package com.bot.crawler.service.impl;

import com.bot.crawler.service.HtmlDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class HtmlDocumentServiceImpl implements HtmlDocumentService {

    @Async
    @Override
    public CompletableFuture<Document> fetchPageAsDocument(String hyperlink) {

        Document document = null;
        try {
            document = Jsoup.connect(hyperlink).get();
        } catch (IOException e) {
            log.error("Failed to connect to URL: {}", hyperlink, e);
        }
        return CompletableFuture.completedFuture(document);
    }

}
