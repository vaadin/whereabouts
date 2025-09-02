FROM eclipse-temurin:21-jdk AS build
ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY . $HOME

# Build the application with production profile
# Accept VAADIN_PRO_KEY as build argument for commercial components
ARG VAADIN_PRO_KEY

# If VAADIN_PRO_KEY is provided, use it directly (CI scenario)
# Otherwise, Vaadin will look for the key in ~/.vaadin/proKey (local build)
ENV VAADIN_PRO_KEY=${VAADIN_PRO_KEY}

RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -Pproduction -DskipTests

FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
