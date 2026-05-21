# Insurance Management Platform

A full-stack property insurance management platform built with Spring Boot, React and PostgreSQL. The application supports policy lifecycle management, configurable premium calculation, AI-based segmentation for clients and buildings, PDF policy document generation and email delivery when a policy is activated.

## Overview

The platform is designed for managing property insurance operations through two main roles:

- Administrators manage brokers, currencies, fee configurations, risk factor configurations and reporting data.
- Brokers manage clients, buildings and insurance policies from draft creation to activation or cancellation.

The backend exposes secured REST APIs and contains the core insurance business logic, while the frontend provides a role-based interface for daily operational workflows. The system also includes a Python-based AI module that uses K-Prototypes clustering to segment mixed numerical and categorical insurance data.

## Main Features

- Role-based authentication with JWT
- Administrator and broker workflows
- Broker, client and building management
- Policy creation, activation and cancellation
- Configurable premium calculation
- Risk factor and fee configuration
- Currency management
- Premium breakdown displayed per policy
- AI segmentation for buildings and clients
- Reports and filtering
- Global backend error handling connected to the frontend
- PDF policy document generation
- Email delivery with attached policy PDF after activation

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Thymeleaf
- OpenHTMLtoPDF
- JavaMail / SMTP

### Frontend

- React
- Vite
- JavaScript
- Tailwind CSS
- Axios
- React Router
- Lucide React

### AI Module

- Python
- K-Prototypes clustering
- Mixed numerical and categorical data segmentation

## Project Structure

```text
insurance-service/
├── frontend/                 # React frontend
├── ml/                       # Python AI segmentation module
├── scripts/                  # Database/demo helper scripts
├── src/main/java/            # Spring Boot backend source code
├── src/main/resources/       # Application config, SQL data and templates
├── pom.xml                   # Backend dependencies and build config
└── README.md
```

## Prerequisites

Before running the project locally, make sure the following are installed:

- Java 21
- Node.js and npm
- PostgreSQL
- Python 3
- Maven or the included Maven wrapper

## Database Setup

Create a PostgreSQL database named:

```text
insurance_db
```

The default local configuration expects:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/insurance_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

These values can be changed in:

```text
src/main/resources/application.properties
```

## Running the Backend

From the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, run the main Spring Boot application directly from IntelliJ IDEA.

The backend starts by default on:

```text
http://localhost:8080
```

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

## Running the Frontend

From the frontend folder:

```powershell
cd frontend
npm install
npm run dev
```

The frontend starts by default on:

```text
http://localhost:5173
```

## AI Segmentation

The application includes an AI segmentation module for grouping clients and buildings into meaningful clusters. It uses K-Prototypes, which is suitable for datasets that contain both numerical and categorical fields.

The module can be used to support:

- Building risk segmentation
- Client segmentation
- Portfolio analysis
- Reporting and pricing insights

The backend calls the Python script configured in:

```properties
ai.python.command=py
ai.python.script-path=ml/kprototypes_segmentation.py
ai.clustering.max-iterations=50
```

## Email and PDF Policy Delivery

When a policy is activated, the backend generates a PDF policy document and sends it to the client's email address.

The PDF contains:

- Policy number and status
- Coverage period
- Client details
- Broker details
- Building details
- Insured value
- Premium calculation
- Applied adjustments
- Risk factors

SMTP configuration is stored in:

```text
src/main/resources/application.properties
```

For Gmail SMTP, the recommended configuration is:

```properties
insurance.email.enabled=true
insurance.email.from=your_email@gmail.com

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=${GMAIL_APP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

Do not commit real email passwords or app passwords to the repository. Use an environment variable such as `GMAIL_APP_PASSWORD` for local development.

## Demo Data

The `scripts` folder contains helper SQL scripts that can be used to populate the database with demo data for clients, buildings, policies and AI-related scenarios.

## Notes

- The application is intended for educational and portfolio purposes.
- The insurance calculations are configurable and designed to demonstrate real-world policy administration concepts.
- The AI clustering output is meant to support analysis and decision-making, not to replace manual underwriting or business validation.

