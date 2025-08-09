 # Newsletter Subscription App
This is a simple newsletter subscription application built with Spring Boot, Kafka, PostgreSQL, Docker and a basic Frontend.

# What this project does
- Allows users to subscribe to a newsletter by providing their email, first name, and last name on a UI.
- Sends subscription events asynchronously to a Kafka topic.
- Stores subscription data in a PostgreSQL database.
- Provides REST API endpoints to get all subscriptions, get a subscription by email, and delete subscriptions by email.

# Technologies used
- Java 17, Spring Boot — Backend REST API and Kafka producer.
- Apache Kafka — Message broker for handling subscription events.
- PostgreSQL — Database to store subscription data.
- Docker & Docker Compose — For easy local setup of Kafka, PostgreSQL, backend, and frontend.
- JUnit & Spring Kafka Test — For unit and integration testing.

# How to run the project
- I have provided a docker compose file, so simply run the below command.
- docker-compose up --build
- This will start Kafka, Zookeeper, PostgreSQL, the backend API, and frontend app.
- Frontend will be on localhost:3000
- Backend will be on localhost:8081

# Swagger — API documentation
- http://localhost:8081/swagger-ui/index.html#/

The backend API runs on port 8081. Here are the main endpoints:

- GET /api/newsletter — Get all subscriptions
- GET /api/newsletter/{email} — Get subscription by email
- DELETE /api/newsletter/{email} — Delete subscription by email
- POST /api/newsletter/subscribe — Add new subscription

# Testing
- There are integration tests for every endpoint, using H2 database
- I also implemented integration tests for Kafka messages as well, making sure we can produce and consume correct messages.

# What it looks like
Take a look at the pictures folder
