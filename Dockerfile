
# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar configurações do Maven
COPY pom.xml .

# Baixar dependências
RUN mvn -q -DskipTests dependency:go-offline

# Copiar código-fonte e compilar
COPY src src

RUN mvn -DskipTests clean package

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Porta utilizada pelo Render
ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
