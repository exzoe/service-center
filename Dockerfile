FROM eclipse-temurin:21-jdk AS build
WORKDIR /build

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew
COPY src ./src
RUN ./gradlew --no-daemon test installDist

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV LANG=C.UTF-8
COPY --from=build /build/build/install/service-center/ ./
ENTRYPOINT ["/app/bin/service-center"]
