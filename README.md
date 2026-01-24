# Spends service for Off Road Soft Turtles And Rabbits

# How to use:

## JVM mode:

```shell
./mvnw clean spring-boot:run
```

## Docker:

### Build docker image:

```shell
./mvnw clean spring-boot:build-image -Dspring-boot.build-image.imageName=spends:latest -Pnative
```

## Run docker image:

```shell
docker run --rm -p 8000:8000 -p 8001:8001 spends:latest
```

## Native:

## Build native image:

```shell
./mvnw clean native:compile -Pnative
```

## Run native image:

```shell
./target/spends
```

## Endpoints:

- [Actuator](http://localhost:8001/manage)
- [Swagger-ui](http://localhost:8000/swagger-ui/)
