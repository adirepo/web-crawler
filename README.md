# Web Crawler

## Overview
Web Crawler is a Spring Boot application designed to crawl web pages from a given domain URL. It processes HTTP GET requests to scan all child URLs within the specified
domain and can optionally include external links based on configuration.

## Features
- **Domain Crawling**: Accepts a domain URL via HTTP GET request (e.g., `/pages?target=<domain-name>`) and scans all child URLs within the domain.
- **External Link Scanning**: Configurable option to enable or disable crawling of external links outside the provided domain.
- **Built with Maven**: Dependency management and build process.
- **Spring Boot**: Production-ready web application framework.

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
Send an HTTP GET request to the `/pages with query param as target=<your-target-url>