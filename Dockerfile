FROM ghcr.io/graalvm/graalvm-community:25.0.2 AS dependencies
WORKDIR /build
COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/
RUN --mount=type=bind,source=pom.xml,destination=pom.xml \
    --mount=type=cache,destination=/root/.m2  \
    ./mvnw dependency:go-offline -Dmaven.test.skip=true

FROM dependencies AS building
WORKDIR /build
COPY ./src src/
RUN --mount=type=bind,source=pom.xml,destination=pom.xml \
    --mount=type=cache,destination=/root/.m2 \
    ./mvnw native:compile -Pnative -Dmaven.test.skip=true

FROM building AS final
EXPOSE 8080
EXPOSE 8081
ENTRYPOINT ["./target/spends"]
