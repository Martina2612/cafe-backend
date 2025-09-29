# ---- build stage ----
FROM maven:3.9.1-eclipse-temurin-17 AS build
WORKDIR /workspace

# copiar pom para aprovechar cache de dependencias
COPY pom.xml .
# si usás .mvn/mvnw puedes copiarlos también:
COPY mvnw ./
COPY .mvn .mvn

# copiar el código fuente y compilar (sin tests)
COPY src ./src
RUN mvn -B -DskipTests package

# ---- runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# copiar el jar generado
COPY --from=build /workspace/target/*.jar app.jar

# Declarar las variables (sin valores)
ENV CLOUD_NAME=""
ENV API_KEY=""
ENV API_SECRET=""
ENV DB_URL=""
ENV DB_USERNAME=""
ENV DB_PASSWORD=""
ENV JWT_SECRET=""
ENV PORT=8080

# convención: exponer 8080 (Render sobreescribe con PORT en runtime)
EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
