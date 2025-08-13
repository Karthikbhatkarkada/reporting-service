# Dockerfile for reporting-service using proper jboss user
# Based on Red Hat best practices

# Stage 1: Build parent POM and styra-common
FROM registry.access.redhat.com/ubi9/openjdk-21:latest AS parent-build

# Use the default jboss user
USER jboss
WORKDIR /home/jboss/build

# Copy parent POM
COPY --chown=jboss:root parent/pom.xml /home/jboss/build/parent/pom.xml

# Install parent POM
RUN cd parent && mvn install -N

# Copy styra-common
COPY --chown=jboss:root styra-common/pom.xml /home/jboss/build/styra-common/pom.xml
COPY --chown=jboss:root styra-common/src /home/jboss/build/styra-common/src

# Build styra-common
RUN cd styra-common && mvn clean install -DskipTests

# Stage 2: Build the service
FROM registry.access.redhat.com/ubi9/openjdk-21:latest AS service-build

USER jboss
WORKDIR /home/jboss/build

# Copy Maven repository from parent build
COPY --from=parent-build --chown=jboss:root /home/jboss/.m2 /home/jboss/.m2

# Copy parent and styra-common
COPY --from=parent-build --chown=jboss:root /home/jboss/build/parent /home/jboss/build/parent
COPY --from=parent-build --chown=jboss:root /home/jboss/build/styra-common /home/jboss/build/styra-common

# Copy service files
COPY --chown=jboss:root reporting-service /home/jboss/build/reporting-service

# Build the service
WORKDIR /home/jboss/build/reporting-service
RUN mvn clean package -DskipTests

# Stage 3: Runtime image
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:latest

# Use default user 185
USER 185

WORKDIR /deployments

# Copy the built artifacts
COPY --from=service-build --chown=185:root /home/jboss/build/reporting-service/target/quarkus-app/lib/ /deployments/lib/
COPY --from=service-build --chown=185:root /home/jboss/build/reporting-service/target/quarkus-app/*.jar /deployments/
COPY --from=service-build --chown=185:root /home/jboss/build/reporting-service/target/quarkus-app/app/ /deployments/app/
COPY --from=service-build --chown=185:root /home/jboss/build/reporting-service/target/quarkus-app/quarkus/ /deployments/quarkus/

# Environment variables
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

# Expose port
EXPOSE 8087

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8087/q/health || exit 1

# Run the application
ENTRYPOINT [ "java", "-jar", "/deployments/quarkus-run.jar" ]
