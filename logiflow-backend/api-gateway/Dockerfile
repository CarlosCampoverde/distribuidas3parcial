# Build stage
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build

# Copy Maven files
COPY pom.xml ./pom.xml
COPY common ./common
COPY api-gateway/pom.xml ./api-gateway/pom.xml

# Copy source code
COPY api-gateway/src ./api-gateway/src

# Move into service module
WORKDIR /build/api-gateway

# Build application
RUN apk add --no-cache maven && \
  mvn -f /build/common/pom.xml -DskipTests install && \
  mvn clean package -DskipTests && \
  mv target/*.jar /build/app.jar

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 appuser && \
    adduser -D -u 1001 -G appuser appuser

# Copy JAR from builder
COPY --from=builder /build/app.jar .

# Change ownership
RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
