package com.bot.crawler.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import static com.bot.crawler.constants.Constants.ROOT_PATH;

@Slf4j
@UtilityClass
public class Validator {

    /**
     * Check whether the given hyperlink has already been scanned
     *
     * @param hyperLink
     *            the hyperlink to be checked
     * @param scannedLinks
     *            the set of already scanned hyperlinks
     *
     * @return true if the link is already scanned, else written false
     */
    public static boolean isNotYetScanned(String hyperLink, Set<String> scannedLinks) {
        return !scannedLinks.contains(hyperLink);
    }

    /**
     * Checks whether the given link is part of the same domain
     *
     * @param parentLink
     *            the reference link to be checked against
     * @param childLink
     *            the link to be checked
     *
     * @return true if the childLink belongs to same domain, else returns false
     */
    public static boolean hasSameDomain(String parentLink, String childLink) {
        String domain;
        try {
            URL url = new URL(parentLink);
            domain = url.getHost();
        } catch (MalformedURLException e) {
            log.error("The URL '{}' is invalid", parentLink);
            return false;
        }
        return childLink.contains(domain);
    }

    /**
     * Checks whether the given href attribute is a root path i.e. '/'
     *
     * @param href
     *            the attribute to be checked
     *
     * @return true if it is root path, else returns false
     */
    public static boolean isRootPath(String href) {
        return href.trim().equals(ROOT_PATH);
    }
}
