package com.bot.crawler;

import com.bot.crawler.dto.CrawlResponse;
import com.bot.crawler.dto.ErrorResponse;
import com.bot.crawler.exception.CrawlerException;
import com.bot.crawler.provider.ResourceProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CrawlerApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private ResourceProvider resourceProvider;

    private static final String TARGET_URL = "http://example.com"; // Test target URL

    @Test
    @DisplayName("Test crawler for a successful scenario")
    public void testCrawlUrlSuccess() {
        /* Given */
        when(resourceProvider.findChildLinks(anyString())).thenReturn(Set.of(TARGET_URL + "/my-page.html"));

        /* When */
        String REQUEST_URL = "http://localhost:" + port + "/pages?target=" + TARGET_URL;
        ResponseEntity<CrawlResponse> response = restTemplate.getForEntity(REQUEST_URL, CrawlResponse.class);

        /* Then */
        assertEquals(200, response.getStatusCode().value(), "Response status should be 200 OK");
        assertNotNull(response.getBody(), "Response body should not be null");
        // Check if the target URL is correctly set in the response
        assertEquals(TARGET_URL, response.getBody().getDomain(), "Target URL should match the request");
        Set<String> pages = response.getBody().getPages();
        // Check that pages are returned (assuming it's a mock test)
        assertEquals(pages.size(), 2, "Pages list should not be empty");
        assertTrue(pages.contains(TARGET_URL + "/my-page.html"));
    }

    @Test
    @DisplayName("Test crawler for a custom depth value")
    public void testCrawlUrlWithCustomDepth() {
        /* Given */
        when(resourceProvider.findChildLinks(eq(TARGET_URL))).thenReturn(Set.of(TARGET_URL + "/my-page.html"));
        when(resourceProvider.findChildLinks(eq(TARGET_URL + "/my-page.html")))
                .thenReturn(Set.of(TARGET_URL + "/my-child-page-1.html"));
        when(resourceProvider.findChildLinks(eq(TARGET_URL + "/my-child-page-1.html")))
                .thenReturn(Set.of(TARGET_URL + "/my-child-page-2.html"));
        when(resourceProvider.findChildLinks(eq(TARGET_URL + "/my-child-page-2.html")))
                .thenReturn(Set.of(TARGET_URL + "/my-child-page-3.html"));

        int customDepth = 2; // Custom depth

        /* When */
        String REQUEST_URL = "http://localhost:" + port + "/pages?target=" + TARGET_URL;
        ResponseEntity<CrawlResponse> response = restTemplate.getForEntity(REQUEST_URL + "&depth=" + customDepth,
                CrawlResponse.class);

        /* Then */
        // Assertions
        assertEquals(200, response.getStatusCode().value(), "Response status should be 200 OK");
        assertNotNull(response.getBody(), "Response body should not be null");

        // Check if the target URL is correctly set in the response
        assertEquals(TARGET_URL, response.getBody().getDomain(), "Target URL should match the request");

        // Check that pages are returned
        Set<String> pages = response.getBody().getPages();
        assertEquals(pages.size(), 3, "Pages list count should match");
        assertTrue(pages.contains(TARGET_URL + "/my-child-page-1.html"));
        assertFalse(pages.contains(TARGET_URL + "/my-child-page-2.html"));
    }

    @Test
    @DisplayName("Test crawler with an empty target")
    public void testCrawlUrlWithEmptyTarget() {
        /* Given */
        String targetUrl = "";

        /* When */
        String REQUEST_URL = "http://localhost:" + port + "/pages?target=" + targetUrl;
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(REQUEST_URL, ErrorResponse.class);

        /* Then */
        // Assertions
        assertEquals(400, response.getStatusCode().value(), "Response status should be 400 Bad Request");
        assertEquals("Bad Request: Target URL can not be blank", response.getBody().getMessage(),
                "Response status should be 400 Bad Request");
    }

    @Test
    @DisplayName("Test crawler to capture external URLs from the given target")
    public void testCrawlUrlWithExternalUrls() {
        /* Given */
        when(resourceProvider.findChildLinks(eq(TARGET_URL))).thenReturn(
                Set.of(TARGET_URL + "/my-page-1.html", TARGET_URL + "/my-child-page-1.html", "https://github.com"));

        /* When */
        String REQUEST_URL = "http://localhost:" + port + "/pages?target=" + TARGET_URL;
        ResponseEntity<CrawlResponse> response = restTemplate.getForEntity(REQUEST_URL, CrawlResponse.class);

        /* Then */
        // Assertions
        assertEquals(200, response.getStatusCode().value(), "Response status should be 200 OK");
        assertNotNull(response.getBody(), "Response body should not be null");

        // Check if the target URL is correctly set in the response
        assertEquals(TARGET_URL, response.getBody().getDomain(), "Target URL should match the request");

        // Check that pages are returned
        Set<String> pages = response.getBody().getPages();
        assertEquals(pages.size(), 4, "Pages list count should match");
        assertTrue(pages.containsAll(Set.of(TARGET_URL, TARGET_URL + "/my-page-1.html",
                TARGET_URL + "/my-child-page-1.html", "https://github.com")));
    }

    @Test
    @DisplayName("Test crawler for exception scenario on target URL")
    public void testCrawlUrlForExceptionOnTargetUrl() {
        /* Given */
        // Throw exception
        when(resourceProvider.findChildLinks(eq(TARGET_URL))).thenThrow(new CrawlerException(
                String.format("Failed to connect to URL: %s", TARGET_URL), new RuntimeException()));

        /* When */
        String REQUEST_URL = "http://localhost:" + port + "/pages?target=" + TARGET_URL;
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(REQUEST_URL, ErrorResponse.class);

        /* Then */
        // Assertions
        assertEquals(500, response.getStatusCode().value(), "Response status should be 200 OK");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals("Internal Server Error: Failed to connect to URL: http://example.com",
                response.getBody().getMessage(), "Exception message should match");
    }

    @Test
    @DisplayName("Test crawler for exception scenario on child URL")
    public void testCrawlUrlForExceptionOnChildUrl() {
        /* Given */
        when(resourceProvider.findChildLinks(eq(TARGET_URL))).thenReturn(Set.of(TARGET_URL + "/my-page.html"));
        when(resourceProvider.findChildLinks(eq(TARGET_URL + "/my-page.html")))
                .thenReturn(Set.of(TARGET_URL + "/my-child-page-1.html"));
        when(resourceProvider.findChildLinks(eq(TARGET_URL + "/my-child-page-1.html"))).thenThrow(new CrawlerException(
                String.format("Failed to connect to URL: %s", TARGET_URL + "/my-child-page-1.html"),
                new RuntimeException()));

        /* When */
        String REQUEST_URL = "http://localhost:" + port + "/pages?target=" + TARGET_URL;
        ResponseEntity<CrawlResponse> response = restTemplate.getForEntity(REQUEST_URL, CrawlResponse.class);

        /* Then */
        // Assertions
        assertEquals(200, response.getStatusCode().value(), "Response status should be 200 OK");
        assertNotNull(response.getBody(), "Response body should not be null");

        // Check if the target URL is correctly set in the response
        assertEquals(TARGET_URL, response.getBody().getDomain(), "Target URL should match the request");

        // Check that pages are returned
        Set<String> pages = response.getBody().getPages();
        assertEquals(pages.size(), 3, "Pages list count should match");
        assertTrue(pages
                .containsAll(Set.of(TARGET_URL, TARGET_URL + "/my-page.html", TARGET_URL + "/my-child-page-1.html")));
    }
}
