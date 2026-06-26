FROM eclipse-temurin:21-jdk AS deps
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q

FROM deps AS dev
COPY src/ src/
CMD ["./mvnw", "spring-boot:run"]

FROM deps AS build
COPY src/ src/
RUN ./mvnw package -DskipTests -q

FROM eclipse-temurin:21-jre AS prod
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
