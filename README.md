# Web Crawler

## Overview
Web Crawler is a Spring Boot application designed to crawl web pages from a given domain URL. It processes HTTP GET requests to scan all child URLs within the specified
domain and can optionally include external links based on configuration and some other technical features.

## Features
- **Domain Crawling**: Accepts a domain URL via HTTP GET request (e.g., `/pages?target=<domain-url>`) and scans all child URLs within the domain.
- **External Link Scanning**: Configurable option to enable or disable crawling of external links outside the provided domain.
- **Depth based scan**: Configurable option to restrict the scan till the given depth of a website, to scan larger websites in small portions. An optional request parameter
`depth=<integer-value>` can be passed with the request.
- **Rate limiting**: Configurable option to slow down or rate limit the scan to avoid getting black-listed by site owners.


## Prerequisites
- Java 17 or higher
- Maven 3.6.0 or higher
- Internet connection for crawling web pages

## Getting Started

### Clone the Repository
```bash
git clone https://github.com/adirepo/web-crawler.git
cd web-crawler
```

### Build the Application
Use Maven to build the project:
```bash
mvn clean install
```

### Run the Application
Run the Spring Boot application using the following command:
```bash
java -jar target/web-crawler-1.0.0.jar
```

The application will start on `http://localhost:8080` by default.

## Usage

### Crawl a Domain
Send an HTTP GET request to the `/pages` with query param as `target=<domain-url>` to be scanned. If the `depthBasedScan` is enabled, then an optional request parameter `depth=<integer-value>`
can also be passed to limit the scan depth. If not passed the default depth is set to 5.