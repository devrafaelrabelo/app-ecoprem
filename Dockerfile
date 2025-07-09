FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copia o .jar gerado pelo Maven/Gradle
COPY target/*.jar app.jar

# Exponha a porta padrão do Spring Boot
EXPOSE 8080

# Comando de execução
ENTRYPOINT ["java", "-jar", "app.jar"]