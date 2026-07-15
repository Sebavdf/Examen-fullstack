FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copia el JAR que compilaremos de manera local en tu máquina
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]