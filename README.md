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

### 4. Verify tracing is working

Check the logs - you should see trace context:
```
INFO [book-loan-service,684aae7c6ce4e72d,40dabe5a65cb92d7] - Application started successfully
```

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
| `/actuator/metrics` | Application metrics | http://localhost:8080/actuator/metrics |
| `/actuator/info` | Application info | http://localhost:8080/actuator/info |

## API Documentation

### Swagger UI
The service provides interactive API documentation via Swagger UI:

1. Start the service
2. Open your browser: **http://localhost:8080/swagger-ui.html**
3. Explore and test all endpoints directly from the browser

**Benefits:**
- 🎯 Visual API exploration
- 🧪 Try endpoints without Postman/curl
- 📋 See request/response schemas
- 📝 View example responses
- 🔍 Test with real data

## Configuration

### Application Configuration

Key configuration in `application.properties`:
```properties
# Server
server.port=8080

# Kafka Producer
spring.kafka.bootstrap-servers=localhost:9092
app.kafka.topic=book-loans

# JSON Serialization
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

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

You can also configure via environment variables:
```bash
export OTEL_SERVICE_NAME=book-loan-service
export OTEL_TRACES_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318
export OTEL_METRICS_EXPORTER=none
export OTEL_LOGS_EXPORTER=none
export OTEL_INSTRUMENTATION_LOGBACK_MDC_ENABLED=true

java -javaagent:agent/opentelemetry-javaagent.jar -jar target/book-loan-service-0.0.1-SNAPSHOT.jar
```

## Testing

### 1. Start the service with tracing
```bash
./scripts/run-with-tracing.sh
```

### 2. Get all loans (via curl)
```bash
curl http://localhost:8080/api/loans
```

**Or via Swagger UI:**
1. Open http://localhost:8080/swagger-ui.html
2. Find the `GET /api/loans` endpoint
3. Click "Try it out" → "Execute"

### 3. Publish loans to Kafka
```bash
curl -X POST http://localhost:8080/api/loans/publish
```

**Or via Swagger UI:**
1. Find the `POST /api/loans/publish` endpoint
2. Click "Try it out" → "Execute"

Expected response: `"Published 64 loans to Kafka"`

### 4. View traces in Jaeger

1. Open http://localhost:16686
2. Select service: `book-loan-service`
3. Click "Find Traces"
4. See complete request flow:
    - HTTP request received
    - File loading (CSV + JSON)
    - Kafka message publishing
    - Response returned

### 5. Search logs by trace ID

Check your logs for a trace_id:
```
INFO [book-loan-service,684aae7c6ce4e72d,40dabe5a65cb92d7] - Published 64 loans to Kafka
```

Copy `684aae7c6ce4e72d` and:
- Search in Jaeger UI to see the full trace
- Find all related logs across services
- Debug end-to-end request flow

## Observability

### Distributed Tracing

The OpenTelemetry agent **automatically instruments**:
- ✅ HTTP requests (Spring MVC Controllers)
- ✅ Kafka message production
- ✅ File I/O operations
- ✅ Database calls (if any)
- ✅ External HTTP calls
- ✅ Method execution spans

**View traces in Jaeger:**

1. Open http://localhost:16686
2. Select service: `book-loan-service`
3. Click "Find Traces"
4. Click any trace to see:
    - Request processing time
    - File loading duration
    - Kafka publish latency
    - Service dependencies
    - Error details (if any)

**Complete End-to-End Tracing:**

When a request flows: `HTTP Request` → `book-loan-service` → `Kafka` → `book-loan-consumer`

You'll see:
- Single unified trace across both services
- Message publish → consume timing
- Complete request lifecycle
- Performance bottlenecks identified

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

**Benefits:**
- 🔍 Search logs by trace_id to see all related logs
- 🔗 Click trace_id in Jaeger to jump to relevant logs
- 📊 Correlate logs with distributed traces
- 🐛 Debug issues across services easily
- 📈 Aggregate metrics by trace

**Log Levels:**
- `INFO` - Important business events (requests received, loans loaded/published)
- `DEBUG` - Detailed flow information (file loading, parsing steps)
- `WARN` - Non-critical issues (malformed data entries)
- `ERROR` - Failures and exceptions

**Example logs with context:**
```
INFO [book-loan-service,684aae7c6ce4e72d,40dabe5a65cb92d7] - Received request to retrieve all book loans
INFO [book-loan-service,684aae7c6ce4e72d,40dabe5a65cb92d7] - Loaded 64 valid book loans (CSV: 32, JSON: 32)
WARN [book-loan-service,684aae7c6ce4e72d,40dabe5a65cb92d7] - Found 6 malformed entries (CSV: 3, JSON: 3)
INFO [book-loan-service,684aae7c6ce4e72d,40dabe5a65cb92d7] - Successfully retrieved 64 book loans
```

**Contextual Information:**
- Counts (64 loans loaded)
- Sources (CSV: 32, JSON: 32)
- IDs (loan IDs, member IDs)
- Operations (loading, publishing, processing)

### Trace ID in Logs

**Format:** `[service-name, traceId, spanId, sampled]`

**Correlation:**
1. Copy trace_id from logs: `684aae7c6ce4e72d`
2. Paste in Jaeger search: http://localhost:16686
3. See the complete request flow with timing
4. Jump between logs and traces seamlessly

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
│   │   │   ├── controller/                   # REST controllers with logging
│   │   │   ├── service/                      # Business logic with structured logging
│   │   │   ├── model/                        # Domain models
│   │   │   └── BookLoanServiceApplication.java
│   │   └── resources/
│   │       ├── loans/                        # CSV and JSON data files
│   │       │   ├── libraryLoans.csv
│   │       │   └── libraryLoans.json
│   │       ├── application.properties        # Spring configuration
│   │       └── logback-spring.xml            # Logging configuration (optional)
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

### Producer Configuration
```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
```

### Tracing Kafka Messages

The OpenTelemetry agent automatically:
- Creates spans for Kafka publish operations
- Propagates trace context to Kafka message headers
- Links producer and consumer traces
- Measures message publish latency

**In Jaeger, you'll see:**
- `kafka.send` span showing publish operation
- Time taken to publish each message
- Kafka broker information
- Message size and partition details

## Troubleshooting

### Traces not appearing in Jaeger

1. **Check Jaeger is running:**
   ```bash
   curl http://localhost:16686
   ```

2. **Verify OTLP endpoint:**
   ```bash
   curl http://localhost:4318/v1/traces
   ```

3. **Enable agent debug logging:**
   ```bash
   -Dotel.javaagent.debug=true
   ```

4. **Check service name** in Jaeger UI dropdown matches `book-loan-service`

5. **Verify agent is loaded** - Look for this in startup logs:
   ```
   [otel.javaagent ...] opentelemetry-javaagent - version: x.x.x
   ```

### Logs missing trace_id

- Verify agent parameter: `-Dotel.instrumentation.logback-mdc.enabled=true`
- Check `logback-spring.xml` includes MDC pattern: `%X{trace_id}` and `%X{span_id}`
- Ensure the agent jar is being loaded (check startup logs)
- Try using the environment variable: `OTEL_INSTRUMENTATION_LOGBACK_MDC_ENABLED=true`

### Agent not loading

- Verify agent path is correct: `agent/opentelemetry-javaagent.jar`
- Check file exists: `ls -lh agent/opentelemetry-javaagent.jar`
- Ensure Java version compatibility (agent requires Java 8+, app uses 21)
- Check file permissions: `chmod +r agent/opentelemetry-javaagent.jar`

### Kafka connection issues

- Verify Kafka is running: `docker ps | grep kafka`
- Check bootstrap servers: `localhost:9092`
- Test Kafka connectivity: `docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092`
- Review producer logs for connection errors

### Data loading issues

- Check CSV/JSON files exist in `src/main/resources/loans/`
- Verify file permissions
- Look for WARN logs about malformed entries
- Check file encoding (should be UTF-8)

### Swagger UI not accessible

- Verify service is running on port 8080
- Check `springdoc.swagger-ui.enabled=true` in application.properties
- Clear browser cache
- Try: http://localhost:8080/swagger-ui/index.html (alternative URL)

## Performance Considerations

### Agent Overhead
- **CPU**: Typically <5% overhead
- **Memory**: Minimal impact (<50MB)
- **Latency**: <1ms per operation
- **Network**: OTLP exports add minimal bandwidth

### Sampling Strategy

**Current:** 100% sampling (`sampling.probability=1.0`)

**For production with high traffic:**
```bash
# Sample 10% of requests
-Dotel.traces.sampler=traceidratio
-Dotel.traces.sampler.arg=0.1

# Or use parent-based sampling
-Dotel.traces.sampler=parentbased_traceidratio
-Dotel.traces.sampler.arg=0.1
```

**Recommendations:**
- Development: 100% (see everything)
- Staging: 50% (good coverage)
- Production (low traffic): 30-50%
- Production (high traffic): 5-10%

### Storage Considerations

- **Jaeger storage** grows with trace volume
- Configure retention policies in production
- Consider using Cassandra or Elasticsearch backend
- Monitor disk usage regularly

## Running in Different Environments

### Development (Local)
```bash
# Use localhost endpoints
-Dotel.exporter.otlp.endpoint=http://localhost:4318
```

### Docker Compose
```yaml
services:
  book-loan-service:
    image: book-loan-service:latest
    environment:
      - OTEL_SERVICE_NAME=book-loan-service
      - OTEL_EXPORTER_OTLP_ENDPOINT=http://jaeger:4318
      - OTEL_TRACES_EXPORTER=otlp
      - OTEL_METRICS_EXPORTER=none
      - OTEL_LOGS_EXPORTER=none
      - OTEL_INSTRUMENTATION_LOGBACK_MDC_ENABLED=true
      - JAVA_TOOL_OPTIONS=-javaagent:/app/agent/opentelemetry-javaagent.jar
    depends_on:
      - kafka
      - jaeger
```

### Kubernetes
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: book-loan-service
spec:
  template:
    metadata:
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
    spec:
      containers:
      - name: book-loan-service
        image: book-loan-service:latest
        env:
        - name: OTEL_SERVICE_NAME
          value: book-loan-service
        - name: OTEL_EXPORTER_OTLP_ENDPOINT
          value: http://otel-collector:4318
        - name: OTEL_TRACES_EXPORTER
          value: otlp
        - name: OTEL_METRICS_EXPORTER
          value: none
        - name: OTEL_LOGS_EXPORTER
          value: none
        - name: OTEL_INSTRUMENTATION_LOGBACK_MDC_ENABLED
          value: "true"
        - name: OTEL_RESOURCE_ATTRIBUTES
          value: k8s.namespace=$(NAMESPACE),k8s.pod.name=$(POD_NAME)
        - name: JAVA_TOOL_OPTIONS
          value: "-javaagent:/app/agent/opentelemetry-javaagent.jar"
```

### Production Recommendations

1. **Use OpenTelemetry Collector**
    - Decouples apps from backend
    - Centralized configuration
    - Data processing and filtering
    - Multiple export destinations

2. **Configure sampling**
    - Balance cost vs visibility
    - Use adaptive sampling
    - Sample errors at 100%

3. **Secure endpoints**
   ```bash
   -Dotel.exporter.otlp.headers="Authorization=Bearer ${API_TOKEN}"
   -Dotel.exporter.otlp.certificate=/path/to/cert.pem
   ```

4. **Resource attributes**
   ```bash
   -Dotel.resource.attributes=deployment.environment=production,service.version=1.0.0
   ```

5. **Monitor agent health**
    - Check agent logs
    - Monitor export failures
    - Alert on high overhead

## Related Services

- **Consumer**: [book-loan-consumer](../book-loan-consumer) - Consumes messages from Kafka
- **Jaeger**: http://localhost:16686 - Distributed tracing UI
- **Kafka**: localhost:9092 - Message broker

## Development Tips

### IntelliJ IDEA Run Configuration

Create a run configuration with VM options:
```
-javaagent:agent/opentelemetry-javaagent.jar
-Dotel.service.name=book-loan-service
-Dotel.traces.exporter=otlp
-Dotel.exporter.otlp.endpoint=http://localhost:4318
-Dotel.metrics.exporter=none
-Dotel.logs.exporter=none
-Dotel.instrumentation.logback-mdc.enabled=true
```

### Testing with Swagger

1. Start service: `./scripts/run-with-tracing.sh`
2. Open Swagger: http://localhost:8080/swagger-ui.html
3. Test endpoints interactively
4. View traces in Jaeger after each request
5. Correlate requests with trace_id in logs

