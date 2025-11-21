
# SAST Demo Microservice

This is a sample Kotlin microservice designed to test SAST (Static Application Security Testing) tools. It intentionally includes code patterns that SAST tools should flag, such as:

- Hardcoded secrets
- Insecure SQL queries
- Insecure HTTP usage

## Structure
- `controller/` - REST API endpoints
- `service/` - Business logic
- `model/` - Data models

## Build & Run

```sh
./gradlew build
./gradlew bootRun
```

## Endpoints
- `GET /api/users/{id}` - Get user by ID (id is String)
- `GET /api/users/apikey` - Get hardcoded API key
- `GET /api/users/external` - Call external HTTP endpoint

## For SAST Testing Only
This code is intentionally insecure and should not be used in production.
