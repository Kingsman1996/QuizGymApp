# ===== Build stage =====
FROM gradle:8.5.0-jdk17 AS builder
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle bootJar --no-daemon

# ===== Run stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
ENV PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]
