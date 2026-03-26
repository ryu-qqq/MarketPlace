# ========================================
# MarketPlace Legacy API Dockerfile
# ========================================
# 레거시(세토프) 호환 REST API 전용 서버
# Port: 8081
# ========================================

FROM eclipse-temurin:21-jre-alpine

# Install wget for health checks
RUN apk add --no-cache wget

# Create non-root user for security
RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

# Copy the JAR file
COPY bootstrap/bootstrap-legacy-api/build/libs/Marketplace-legacy-api.jar app.jar

# Set ownership
RUN chown -R app:app /app

# Switch to non-root user
USER app

# JVM options for container environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
