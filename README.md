# Building-management
A building management platform that allows property managers and residents to interact within a shared building environment.
Managers can publish official announcements, while residents can share information, ask for help, and offer assistance through community posts.
The system is designed using a modern microservices architecture to demonstrate enterprise backend and DevOps skills.

# Tech Stack
* Backend
* Java 17 / Spring Boot
* Spring Security (JWT)
* Spring Data JPA / Hibernate
* SQL (PostgreSQL)
* Apache Kafka (event-driven communication)
* Frontend
* Angular
* DevOps & Infra
* Docker & Docker Compose
* GitHub Actions (CI)
* Jenkins (CD)
* Prometheus & Grafana (monitoring)
* Kubernetes & Terraform (infrastructure as code)
* Testing
* JUnit 5
* Mockito
* Integration tests with Testcontainers

# Microservices
* bm-gateway-service – API Gateway (routing, security)
* bm-auth-service – Authentication & authorization
* bm-building-service – Buildings & user membership
* bm-announcement-service – Official building announcements
* bm-community-service – Community “Share & Help” posts
* bm-notification-service – Kafka-based notifications
* bm-file-service – File & image uploads
* bm-frontend-angular – Angular frontend
* bm-infra – Docker, Kubernetes, Terraform configs

# Features
* Secure JWT-based authentication
* Role-based access (Manager / Tenant)
* Building management with unique codes
* Announcements per building
* Community posts (Help / Share / Info)
* Event-driven notifications using Kafka
* Fully containerized & observable system

# Goal
- This project is built as a portfolio-grade reference system, demonstrating real-world backend architecture, clean code, testing strategies, and DevOps workflows.
