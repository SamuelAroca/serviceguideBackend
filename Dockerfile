FROM maven:3.9-amazoncorretto-20

COPY pom.xml /app/pom.xml
RUN mvn -B -f /app/pom.xml dependency:resolve

COPY src /app/src
RUN mvn -B -f /app/pom.xml package

EXPOSE 5001

CMD ["java", "-jar", "/app/target/servceguideBackend.jar"]