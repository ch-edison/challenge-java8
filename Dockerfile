FROM maven:3.8.8-eclipse-temurin-8 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:8-jre
WORKDIR /app

ENV JAVA_OPTS=""

ENV STARWARS_BASE_URL="https://www.swapi.tech"
ENV SECURITY_JWT_SECRET=""

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar --starwars.base-url=${STARWARS_BASE_URL} --security.jwt.secret=${SECURITY_JWT_SECRET}"]
