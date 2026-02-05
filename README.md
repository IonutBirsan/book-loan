# Book Loan Service (Producer)

A Spring Boot microservice that manages book loan data and publishes it to Kafka for consumption by other services.

## Features

- 📚 Load book loan data from CSV and JSON files
- 🚀 REST API to retrieve and publish book loans
- 📤 Kafka producer - publishes book loan events to `book-loans` topic
- 📊 Distributed tracing with OpenTelemetry and Jaeger
- 📝 Structured logging with trace context (trace_id, span_id)
- 📖 Interactive API documentation with Swagger/OpenAPI
- ❤️ Health checks and metrics via Spring Actuator

## Technologies

- Java 21
- Spring Boot 3.3.3
- Apache Kafka
- OpenTelemetry Java Agent
- Jaeger for distributed tracing
- Swagger/OpenAPI (SpringDoc)
- Jackson for JSON processing
- SLF4J + Logback for structured logging

## Prerequisites

- Java 21+
- Maven 3.6+
- Docker (for Kafka and Jaeger)
- OpenTelemetry Java Agent (included in `agent/` directory)

## Getting Started

### 1. Start Kafka and Jaeger
```bash
docker-compose up -d
```

This starts:
- Zookeeper (port 2181)
- Kafka (port 9092)
- Jaeger UI (http://localhost:16686)
- Jaeger OTLP endpoint (port 4318)

### 2. Build the application
```bash
mvn clean package
```

### 3. Run with OpenTelemetry Agent

**Using the run script (recommended):**
```bash
./scripts/run-with-tracing.sh
```

**Or manually:**
```bash
java -javaagent:agent/opentelemetry-javaagent.jar \
  -Dotel.service.name=book-loan-service \
  -Dotel.traces.exporter=otlp \
  -Dotel.exporter.otlp.endpoint=http://localhost:4318 \
  -Dotel.metrics.exporter=none \
  -Dotel.logs.exporter=none \
  -Dotel.instrumentation.logback-mdc.enabled=true \
  -jar target/book-loan-service-0.0.1-SNAPSHOT.jar
```

**For development (Maven):**
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-javaagent:agent/opentelemetry-javaagent.jar -Dotel.service.name=book-loan-service -Dotel.traces.exporter=otlp -Dotel.exporter.otlp.endpoint=http://localhost:4318 -Dotel.metrics.exporter=none -Dotel.logs.exporter=none -Dotel.instrumentation.logback-mdc.enabled=true"
```

The service will start on **http://localhost:8080**

## API Endpoints

### REST API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/loans` | Get all book loans |
| POST | `/api/loans/publish` | Publish all loans to Kafka |

### Documentation & Monitoring

| Endpoint | Description | URL |
|----------|-------------|-----|
| `/swagger-ui.html` | Interactive API documentation | http://localhost:8080/swagger-ui.html |
| `/api-docs` | OpenAPI specification (JSON) | http://localhost:8080/api-docs |
| `/actuator/health` | Health check | http://localhost:8080/actuator/health |

## Configuration

### Application Configuration

Key configuration in `application.properties`:
```properties
# Server
server.port=8080

# Kafka Producer
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

### OpenTelemetry Agent Configuration

The OpenTelemetry Java Agent is configured via JVM arguments:

| Parameter | Value | Description |
|-----------|-------|-------------|
| `-javaagent` | `agent/opentelemetry-javaagent.jar` | Loads the OTel agent |
| `otel.service.name` | `book-loan-service` | Identifies this service in traces |
| `otel.traces.exporter` | `otlp` | Export traces via OTLP protocol |
| `otel.exporter.otlp.endpoint` | `http://localhost:4318` | Jaeger OTLP endpoint |
| `otel.metrics.exporter` | `none` | Disable metrics export |
| `otel.logs.exporter` | `none` | Disable log export (we only add context) |
| `otel.instrumentation.logback-mdc.enabled` | `true` | Add trace_id/span_id to logs |

**Alternative: Environment Variables**

```bash
export OTEL_SERVICE_NAME=book-loan-service
export OTEL_TRACES_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318
export OTEL_METRICS_EXPORTER=none
export OTEL_LOGS_EXPORTER=none
export OTEL_INSTRUMENTATION_LOGBACK_MDC_ENABLED=true

java -javaagent:agent/opentelemetry-javaagent.jar -jar target/book-loan-service-0.0.1-SNAPSHOT.jar
```

## How It Works

### What Gets Automatically Traced

The OpenTelemetry agent automatically instruments:
- HTTP requests (Spring MVC Controllers)
- Kafka message production
- File I/O operations
- Database calls (if any)
- External HTTP calls

### Structured Logging with Trace Context

Logs automatically include trace correlation:

**Before (without agent):**
```
INFO [book-loan-service,,] - Loaded 64 valid book loans
```

**After (with agent):**
```
INFO [book-loan-service,684aae7c6ce4e72d,40dabe5a65cb92d7,01] - Loaded 64 valid book loans
                          ^^^^^^^^^^^^^^^^  ^^^^^^^^^^^^  ^^
                          trace_id          span_id       sampled
```

## Testing

### 1. Get all loans
```bash
curl http://localhost:8080/api/loans
```

**Or via Swagger UI:**
- Open http://localhost:8080/swagger-ui.html
- Find the `GET /api/loans` endpoint
- Click "Try it out" → "Execute"

### 2. Publish loans to Kafka
```bash
curl -X POST http://localhost:8080/api/loans/publish
```

Expected response: `"Published 64 loans to Kafka"`

### 3. View traces in Jaeger

1. Open http://localhost:16686
2. Select service: `book-loan-service`
3. Click "Find Traces"
4. See complete request flow with timing

## Observability

### Viewing Traces in Jaeger

1. Open http://localhost:16686
2. Select service: `book-loan-service`
3. Click "Find Traces"
4. Click any trace to see:
   - Request processing time
   - File loading duration
   - Kafka publish latency
   - Error details (if any)

### End-to-End Tracing

When a request flows: `HTTP Request` → `book-loan-service` → `Kafka` → `book-loan-consumer`

You'll see:
- Single unified trace across both services
- Message publish → consume timing
- Complete request lifecycle

### Log Correlation

Copy the trace_id from logs:
```
INFO [book-loan-service,684aae7c6ce4e72d,40dabe5a65cb92d7] - Published 64 loans to Kafka
```

Paste `684aae7c6ce4e72d` in Jaeger search to see the full trace.

## Project Structure
```
book-loan-service/
├── agent/
│   ├── opentelemetry-javaagent.jar          # OTel Java agent
│   └── README.md                             # Agent version info
├── scripts/
│   └── run-with-tracing.sh                   # Helper script to run with agent
├── src/
│   ├── main/
│   │   ├── java/com/bvd/java_fundamentals/
│   │   │   ├── controller/                   # REST controllers
│   │   │   ├── service/                      # Business logic
│   │   │   ├── model/                        # Domain models
│   │   │   └── BookLoanServiceApplication.java
│   │   └── resources/
│   │       ├── loans/                        # CSV and JSON data files
│   │       │   ├── libraryLoans.csv
│   │       │   └── libraryLoans.json
│   │       └── application.properties        # Spring configuration
│   └── test/
├── docker-compose.yml                        # Kafka + Jaeger setup
├── pom.xml                                   # Maven dependencies
└── README.md                                 # This file
```

## Data Sources

The service loads book loan data from:
- `loans/libraryLoans.csv` - CSV format (32 loans)
- `loans/libraryLoans.json` - JSON format (32 loans)

**Total:** 64 valid book loans

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

### Topic Configuration
- **Name**: `book-loans`
- **Partitions**: 1
- **Replication Factor**: 1

### Message Format
Messages are published as JSON with the BookLoan structure shown above.

## Related Services

- **Consumer**: [book-loan-consumer](../book-loan-consumer) - Consumes messages from Kafka
- **Jaeger**: http://localhost:16686 - Distributed tracing UI
- **Kafka**: localhost:9092 - Message broker