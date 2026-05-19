# ══════════════════════════════════════════════════════════════════
# Dockerfile — Multi-Stage Build
# Proyecto: Guerrero-post2-u12  |  Spring Boot 3 + JDK 21
# ══════════════════════════════════════════════════════════════════

# ── Stage 1: Build ───────────────────────────────────────────────
# Usa la imagen Maven oficial con JDK 21 para compilar el proyecto.
# El resultado es un JAR ejecutable en target/
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar primero el pom.xml para aprovechar el caché de capas Docker.
# Solo se re-descargan dependencias si el pom.xml cambia.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar (sin ejecutar pruebas en build)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Runtime ─────────────────────────────────────────────
# Imagen mínima JRE 21 — sin herramientas de compilación.
# Esto reduce el tamaño final de la imagen significativamente.
FROM eclipse-temurin:21-jre-alpine AS runtime

# Etiquetas de metadata OCI
LABEL org.opencontainers.image.title="mi-spring-app" \
      org.opencontainers.image.description="API REST Spring Boot - Guerrero Post2 U12" \
      org.opencontainers.image.authors="Guerrero" \
      org.opencontainers.image.source="https://github.com/guerrero/Guerrero-post2-u12"

# Crear usuario no-root para seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copiar únicamente el JAR generado en el stage de build
COPY --from=builder /app/target/*.jar app.jar

# Cambiar al usuario no-root
USER appuser

# Puerto expuesto por Spring Boot
EXPOSE 8080

# Variables de entorno con valores por defecto
ENV SPRING_PROFILES_ACTIVE=dev \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Comando de inicio con opciones JVM configurables
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
