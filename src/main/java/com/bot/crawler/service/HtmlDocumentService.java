package com.bot.crawler.service;

import org.jsoup.nodes.Document;

import java.util.concurrent.CompletableFuture;

public interface HtmlDocumentService {

    /**
     * Fetches the web page as 'jsoup document' for the provided link
     * @param hyperlink the hyperlink of the document to be fetched
     * @return the jsoup document
     */
    CompletableFuture<Document> fetchPageAsDocument(String hyperlink);
}
