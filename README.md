# Reporting Service

Data analytics and reporting microservice for the Styra Platform.

## Overview

The Reporting Service is responsible for:
- Report generation and scheduling
- Data aggregation and analytics
- Dashboard data preparation
- Custom report builder
- Report templates and layouts
- Export to multiple formats (PDF, Excel, CSV)
- Real-time analytics
- Data visualization
- Report distribution and sharing

## Technology Stack

- **Framework**: Quarkus 3.24.0
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: PostgreSQL with Liquibase migrations
- **Cache**: Redis
- **Messaging**: Apache Camel for event-driven architecture
- **API**: RESTful with OpenAPI documentation
- **Security**: JWT-based authentication with RBAC
- **Container**: Docker (via Jib)

## Prerequisites

- Java 17 or higher
- Maven 3.8.x or higher
- Docker (optional, for containerization)
- PostgreSQL 14+ (for development)
- Redis 6+ (for caching)

## Getting Started

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd reporting-service
   ```

2. **Configure the application**
   
   Update `src/main/resources/application.properties`:
   ```properties
   # Database configuration
   quarkus.datasource.db-kind=postgresql
   quarkus.datasource.username=your_username
   quarkus.datasource.password=your_password
   quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/reporting_db
   
   # Redis configuration
   quarkus.redis.hosts=redis://localhost:6379
   ```

3. **Run the application in development mode**
   ```bash
   ./mvnw compile quarkus:dev
   ```

   The service will be available at `http://localhost:8084`

### Building the Application

**JAR packaging (fast-jar)**
```bash
./mvnw clean package
```

**Native executable**
```bash
./mvnw clean package -Pnative
```

**Docker image**
```bash
./mvnw clean package -Pcontainer
```

## API Documentation

Once the application is running, the API documentation is available at:
- Swagger UI: `http://localhost:8084/q/swagger-ui`
- OpenAPI spec: `http://localhost:8084/q/openapi`

### Key Endpoints

#### Report Generation
- `GET /api/reports` - List reports
- `GET /api/reports/{id}` - Get report
- `POST /api/reports/generate` - Generate report
- `GET /api/reports/{id}/export` - Export report
- `POST /api/reports/schedule` - Schedule report

#### Analytics
- `GET /api/analytics/dashboard` - Dashboard data
- `GET /api/analytics/metrics` - Key metrics
- `POST /api/analytics/query` - Custom query
- `GET /api/analytics/trends` - Trend analysis

#### Templates
- `GET /api/templates/reports` - Report templates
- `POST /api/templates/reports` - Create template
- `POST /api/reports/custom` - Custom report

## Database Schema

The service uses Liquibase for database migrations. Key tables include:

- `reports` - Generated reports
- `report_templates` - Report templates
- `schedules` - Report schedules
- `analytics_cache` - Cached analytics
- `dashboard_configs` - Dashboard settings
- `report_distributions` - Distribution lists

## Configuration

### Application Configuration

```properties
# Service configuration
quarkus.http.port=8084
quarkus.application.name=reporting-service

# Database configuration
quarkus.datasource.db-kind=postgresql
quarkus.hibernate-orm.database.generation=none
quarkus.liquibase.migrate-at-start=true

# Cache configuration
quarkus.cache.type=redis
quarkus.redis.hosts=redis://localhost:6379

# Security
quarkus.oidc.auth-server-url=${AUTH_SERVICE_URL:http://localhost:8081}
quarkus.oidc.client-id=reporting-service
```

## Testing

**Run unit tests**
```bash
./mvnw test
```

**Run integration tests**
```bash
./mvnw verify
```

**Run with test coverage**
```bash
./mvnw test jacoco:report
```

Coverage reports are available at `target/jacoco-report/index.html`

## Monitoring

- Health check: `http://localhost:8084/q/health`
- Metrics: `http://localhost:8084/q/metrics`
- Prometheus metrics: `http://localhost:8084/q/metrics/prometheus`

## Deployment

### Docker Deployment

```bash
# Build the Docker image
./mvnw clean package -Pcontainer

# Run the container
docker run -p 8084:8084 \
  -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://postgres:5432/reporting_db \
  -e QUARKUS_DATASOURCE_USERNAME=dbuser \
  -e QUARKUS_DATASOURCE_PASSWORD=dbpass \
  myorg/reporting-service:1.0.0-SNAPSHOT
```

### Kubernetes Deployment

```bash
# Generate Kubernetes resources
./mvnw clean package -Dquarkus.kubernetes.deploy=true

# Apply the generated resources
kubectl apply -f target/kubernetes/kubernetes.yml
```

## Troubleshooting

### Common Issues

1. **Database connection errors**
   - Verify PostgreSQL is running and accessible
   - Check database credentials in application.properties
   - Ensure the database exists

2. **Redis connection errors**
   - Verify Redis is running
   - Check Redis connection string
   - Ensure Redis is accessible from the application

3. **Service integration issues**
   - Verify dependent services are running
   - Check service URLs in configuration
   - Review network connectivity between services

## Contributing

Please refer to the project's contribution guidelines before submitting pull requests.

## License

This service is part of the Styra Platform and follows the project's licensing terms.