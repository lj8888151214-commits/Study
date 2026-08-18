# 1단계: Java 25 환경에 Maven 설치 후 프로젝트 빌드
FROM openjdk:25-ea-jdk-slim AS build
WORKDIR /app

# Maven 설치
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

COPY . .
RUN mvn clean package -DskipTests

# 2단계: Java 25 실행 환경
FROM openjdk:25-ea-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
