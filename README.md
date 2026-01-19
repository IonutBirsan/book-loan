# Book Loan Service (Producer)

A Spring Boot microservice that manages book loan data and publishes it to Kafka for consumption by other services.

## Features

- 📚 Load book loan data from CSV and JSON files
- 🚀 REST API to retrieve and publish book loans
- 📤 Kafka producer - publishes book loan events to `book-loans` topic
- 📊 Distributed tracing with OpenTelemetry and Jaeger
- 📝 Structured logging with trace context
- 📖 Interactive API documentation with Swagger/OpenAPI
- ❤️ Health checks and metrics via Spring Actuator

## Technologies

- Java 21
- Spring Boot 3.3.3
- Apache Kafka
- OpenTelemetry + Jaeger
- Swagger/OpenAPI (SpringDoc)
- Jackson for JSON processing
- SLF4J for structured logging

## Prerequisites

- Java 21+
- Maven 3.6+
- Docker (for Kafka and Jaeger)

## Getting Started

### 1. Start Kafka and Jaeger
```bash
docker-compose up -d
```

This starts:
- Zookeeper (port 2181)
- Kafka (port 9092)
- Jaeger UI (http://localhost:16686)

### 2. Run the application
```bash
mvn spring-boot:run
```

The service will start on **http://localhost:8080**

## API Endpoints

### REST API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/loans` | Get all book loans |
| POST | `/api/loans/publish` | Publish all loans to Kafka |

### Documentation & Monitoring

| Endpoint | Description |
|----------|-------------|
| `/swagger-ui.html` | Interactive API documentation (Swagger UI) |
| `/api-docs` | OpenAPI specification (JSON) |
| `/actuator/health` | Health check |
| `/actuator/metrics` | Application metrics |
| `/actuator/info` | Application info |

## API Documentation

### Swagger UI
The service provides interactive API documentation via Swagger UI:

1. Start the service
2. Open your browser: **http://localhost:8080/swagger-ui.html**
3. Explore and test all endpoints directly from the browser

**Benefits for training:**
- Visual API exploration
- Try endpoints without Postman
- See request/response schemas
- View example responses

## Testing

### Get all loans
```bash
curl http://localhost:8080/api/loans
```

### Publish loans to Kafka
```bash
curl -X POST http://localhost:8080/api/loans/publish
```

Expected response: `"Published 64 loans to Kafka"`

## Configuration

Key configuration in `application.properties`:
```properties
# Server
server.port=8080

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
app.kafka.topic=book-loans

# Tracing
spring.application.name=book-loan-service
management.tracing.sampling.probability=1.0
management.otlp.tracing.endpoint=http://localhost:4318/v1/traces

# Swagger/OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

## Observability

### Structured Logging
The service uses structured logging with different log levels:

**Log Levels:**
- `INFO` - Important business events (requests received, loans loaded/published)
- `DEBUG` - Detailed flow information (file loading, parsing steps)
- `WARN` - Non-critical issues (malformed data entries)
- `ERROR` - Failures and exceptions

**Example logs:**
```
INFO [book-loan-service,684aae7c6ce4e72d,40dabe5a65cb92d7] - Received request to retrieve all book loans
INFO [book-loan-service,684aae7c6ce4e72d,40dabe5a65cb92d7] - Loaded 64 valid book loans (CSV: 32, JSON: 32)
WARN [book-loan-service,684aae7c6ce4e72d,40dabe5a65cb92d7] - Found 6 malformed entries (CSV: 3, JSON: 3)
INFO [book-loan-service,684aae7c6ce4e72d,40dabe5a65cb92d7] - Successfully retrieved 64 book loans
```

**Benefits:**
- Contextual information (counts, sources, IDs)
- Trace IDs for distributed tracing correlation
- Easy to search and filter in log aggregators
- Clear operational visibility

### Distributed Tracing with Jaeger

**View traces:**
1. Open http://localhost:16686
2. Select service: `book-loan-service`
3. Click "Find Traces"
4. View detailed request traces with timing information

**Trace ID in logs:**
Every log line includes trace context:
```
INFO [book-loan-service,64d3f2a1b5c8e9f0,a7b2c3d4e5f6g7h8] ...
```
Format: `[service-name, traceId, spanId]`

**Correlation:**
- Copy trace ID from logs
- Search for it in Jaeger UI
- See the complete request flow with timing

## Project Structure
```
book-loan-service/
├── src/
│   ├── main/
│   │   ├── java/com/bvd/java_fundamentals/
│   │   │   ├── controller/      # REST controllers with logging
│   │   │   ├── service/         # Business logic with structured logging
│   │   │   ├── model/           # Domain models
│   │   │   └── BookLoanServiceApplication.java
│   │   └── resources/
│   │       ├── loans/           # CSV and JSON data files
│   │       └── application.properties
├── docker-compose.yml
└── pom.xml
```

## Data Sources

The service loads book loan data from:
- `loans/libraryLoans.csv` - CSV format
- `loans/libraryLoans.json` - JSON format

Sample BookLoan:
```json
{
  "loanId": "L-1001",
  "memberId": "M-001",
  "loanDate": "2024-06-01",
  "bookTitle": "1984",
  "genre": "Dystopian",
  "author": "George Orwell",
  "daysLoaned": 14
}
```

## Kafka Integration

### Topic
- **Name**: `book-loans`
- **Partitions**: 1
- **Replication Factor**: 1

### Message Format
Messages are published as JSON with the BookLoan structure shown above.
