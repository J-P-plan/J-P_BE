# 1. 빌드 스테이지
FROM gradle:8.5.0-jdk21 AS build
COPY . /home/gradle/app
WORKDIR /home/gradle/app
RUN ./gradlew build --no-daemon

# 2. 실행 스테이지
FROM eclipse-temurin:21-jdk
COPY --from=build /home/gradle/app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
