# ---- build stage ----
FROM maven:3.9-amazoncorretto-20 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -f pom.xml dependency:resolve
COPY src ./src
RUN mvn -B -f pom.xml package

# ---- runtime stage ----
FROM eclipse-temurin:20-jre
WORKDIR /app
RUN groupadd -r app && useradd -r -g app app
COPY --from=build /app/target/serviceguideBackend.jar app.jar
USER app
EXPOSE 5001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
