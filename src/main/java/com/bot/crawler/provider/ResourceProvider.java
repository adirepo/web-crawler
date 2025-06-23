package com.bot.crawler.provider;

import java.util.Set;

public interface ResourceProvider {

    /**
     * Finds all the sub-links or child links from the given hyperlinks' page
     * @param hyperlink the hyperlink of the target page
     * @return the unique set of child links of the given hyperlink
     */
    Set<String> findChildLinks(String hyperlink);
}
