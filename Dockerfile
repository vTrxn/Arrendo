# ==============================================================================
# DOCKERFILE MULTI-STAGE: ARRENDO BACKEND (SPRING BOOT 3 + JAVA 21)
# Diseñado para Render.com, Railway.app y despliegues en contenedores Docker
# ==============================================================================

# Etapa 1: Compilación de la aplicación con Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Descarga de dependencias en capa de caché
COPY backend/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

# Compilación del código fuente
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen ligera de ejecución en producción
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear usuario sin privilegios por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copiar el Fat JAR compilado
COPY --from=builder /app/target/arrendo-backend-eclipse-1.0.0.jar app.jar

# Puerto de escucha (Render inyecta PORT dinámicamente)
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
