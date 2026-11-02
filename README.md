# LinkedIn Job Application Bot Enterprise Web Application

An enterprise Spring Boot web application designed for automated LinkedIn job application management, real-time status monitoring, and scalable async workflow execution. Built with **Java 21**, **Spring Boot 3.4.3**, **Thymeleaf**, **Bootstrap 5**, **WebSockets**, and **OpenAPI Swagger UI**.

---

## 🚀 Key Features

- **LinkedIn Job Application Bot Engine**: Multithreaded asynchronous bot workflow that searches and applies to target job roles on LinkedIn.
- **Realtime Dashboard**: Interactive Web UI built with Thymeleaf, Bootstrap 5, and WebSocket (STOMP/SockJS) push notifications for real-time application updates.
- **Multi-Database Support**: Default H2 in-memory DB with full support and scripts for **Oracle Database**, **PostgreSQL**, and **Microsoft SQL Server**.
- **Automated Reporting**: Export comprehensive job application logs in CSV format.
- **Swagger UI Integration**: Interactive REST API documentation at `/swagger-ui.html`.
- **Cross-Platform OS Scripts**: Complete startup and shutdown scripts for both Windows (`start.bat`, `stop.bat`) and Linux (`start.sh`, `stop.sh`).

---

## 🛠️ Technology Stack & Requirements

- **Java**: Version 21 (OpenJDK 21+)
- **Build Tool**: Apache Maven 3.9+
- **Framework**: Spring Boot 3.4.3
- **ORM / Persistence**: Spring Data JPA / Hibernate 6
- **Database Support**: Oracle, PostgreSQL, MS SQL Server, H2
- **Documentation**: Springdoc OpenAPI (Swagger UI 2.8.5)
- **Frontend**: Thymeleaf, Bootstrap 5, FontAwesome, SockJS, Stomp.js
- **Testing**: JUnit 5, Spring Boot Test, Mockito, AssertJ

---

## 📁 Package Structure

All Java source code resides under `com.cth.sdm`:

```
com.cth.sdm
├── LinkedInJobBotApplication.java
├── config
│   └── WebSocketConfig.java
├── controller
│   ├── BotController.java
│   ├── DashboardViewController.java
│   ├── JobApplicationController.java
│   └── ReportController.java
├── dto
│   ├── BotStatusDto.java
│   └── DashboardStatsDto.java
├── model
│   ├── entity
│   │   ├── BotConfig.java
│   │   ├── BotExecutionLog.java
│   │   └── JobApplication.java
│   └── enums
│       └── ApplicationStatus.java
├── repository
│   ├── BotConfigRepository.java
│   ├── BotExecutionLogRepository.java
│   └── JobApplicationRepository.java
├── service
│   ├── DashboardService.java
│   └── ReportService.java
└── workflow
    └── LinkedInBotService.java
```

---

## 💾 Database Configuration & DDL Scripts

Database initialization scripts are located in `scripts/db/`:

- `scripts/db/oracle_schema.sql` - DDL for Oracle Database
- `scripts/db/postgres_schema.sql` - DDL for PostgreSQL
- `scripts/db/sqlserver_schema.sql` - DDL for MS SQL Server
- `scripts/db/h2_schema.sql` - DDL for H2 Database

### Switching Databases
Run the application with the corresponding Spring profile:
- **H2 (Default)**: `-Dspring.profiles.active=h2`
- **Oracle**: `-Dspring.profiles.active=oracle`
- **PostgreSQL**: `-Dspring.profiles.active=postgres`
- **MS SQL Server**: `-Dspring.profiles.active=sqlserver`

---

## 🖥️ Running the Application

### Linux / Unix
```bash
# Grant execution permissions
chmod +x start.sh stop.sh

# Start application (runs in background on port 8080)
./start.sh

# Stop application
./stop.sh
```

### Docker

Requirements on the deployment host: Docker Engine and the Docker Compose
plugin. Build the image on a machine with internet access, then export it:

```bash
docker compose build
docker save -o linkedin-job-bot-image.tar linkedin-job-bot:1.0.0
```

Transfer `linkedin-job-bot-image.tar` and `compose.yaml` to the deployment
host, then load and start the image:

```bash
docker load -i linkedin-job-bot-image.tar
docker compose up -d
```

Open the dashboard at [http://localhost:8080](http://localhost:8080). The image
contains the application, Java runtime, and Java dependencies. Only the image
build machine downloads base images and Maven dependencies; the deployment host
needs no Java or Maven and downloads no application dependencies. Application
data is stored in the `job-data` Docker volume and survives container
recreation.

To view logs or stop the application:

```bash
docker compose logs -f app
docker compose down
```

`docker compose down -v` also removes the database volume and its data.

### Windows
```cmd
:: Start application in new command window
start.bat

:: Stop application running on port 8080
stop.bat
```

---

## 🌐 Web Endpoints & UI

- **Interactive Dashboard**: [http://localhost:8080](http://localhost:8080)
- **Swagger UI API Docs**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **CSV Report Export**: `GET /api/v1/reports/export/csv`
- **H2 Console (H2 Profile Only)**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

---

## 🧪 JUnit 5 Test Suite

The application includes unit tests for repositories, services, and REST API controllers:

- `JobApplicationRepositoryTest.java`: Tests JPA persistence and query methods.
- `DashboardServiceTest.java`: Tests business metric calculations and status aggregations.
- `JobApplicationControllerTest.java`: Integration tests for Web MVC endpoints and report generation.

To run tests:
```bash
mvn test
```

## 📚 Documentation

- [Technical Documentation](docs/technical-documentation.md) - architecture, stack, data flow, APIs, configuration, and operations.
- [Functional Documentation](docs/functional-documentation.md) - user workflows, screens, job behavior, reporting, and functional boundaries.
