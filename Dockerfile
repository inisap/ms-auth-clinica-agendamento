# Dockerfile corrigido com uso direto do Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia somente os arquivos de configuração primeiro para aproveitar o cache do Docker
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN mvn dependency:go-offline

# Copia o restante do código
COPY src ./src

# Compila o projeto
RUN mvn clean package -DskipTests

# Segunda etapa: imagem mais leve apenas com o JAR
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]
