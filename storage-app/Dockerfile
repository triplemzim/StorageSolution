FROM maven:3.9.8-eclipse-temurin-17

WORKDIR /storage-app
COPY . .
RUN mvn clean install -DskipTests

CMD mvn spring-boot:run