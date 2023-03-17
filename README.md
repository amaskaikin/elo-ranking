# Elo ranking

Basic match management between two players supporting Elo ranking service.

## Calculation details:

Used math described in [Wikipedia](https://en.wikipedia.org/wiki/Elo_rating_system) according to FIDE approach.  
Default values:

- Initial rank: 1000
- K-Factors: 40, 20, 10
- Games threshold: 30

## Technology stack

- Java 17
- Spring Boot 3
- Spring Security
- OpenAPI Swagger v3
- OAuth2 Resource server configured with `Auth0` IDP
- **EXPERIMENTAL**: GraalVM Native image support

Deployed as `Docker` container running in `AWS EC2` instance behind `Nginx` reverse proxy

## Run container

```
docker compose pull
docker compose up -d
```
