# SmartBank — Microservices Fund Transfer System
## Built with AI assistance (For Java 17). See the roadmap for what is not implemented yet.

Spring Boot microservices project: **Account Service** and **Transaction Service** communicating
through **Eureka** service discovery and a **Spring Cloud Gateway**, with a **Resilience4j circuit
breaker**, **idempotent** transfer API, **JPA/Hibernate + MySQL**, **JUnit/Mockito** tests, and
**Docker Compose**.

## Architecture

```
Client -> API Gateway :8080 -> Account Service :8081     -> MySQL (smartbank_accounts)
                            -> Transaction Service :8082 -> MySQL (smartbank_transactions)
                                   |  REST + circuit breaker
                                   +--> Account Service (debit / credit)
All services register with Eureka Server :8761
```

## Tech stack
Java 17, Spring Boot 3.2, Spring Cloud (Eureka, Gateway, LoadBalancer), Resilience4j,
Spring Data JPA / Hibernate, MySQL 8, JUnit 5, Mockito, Docker / Docker Compose, Maven.

## Prerequisites
JDK 17, Maven 3.9+, MySQL 8 running locally (root/root, or edit application.yml), Docker Desktop (optional).

## Run locally (IntelliJ or terminal)
Start in this order, each in its own terminal / run configuration:
```bash
cd eureka-server       && mvn spring-boot:run   # http://localhost:8761
cd account-service     && mvn spring-boot:run
cd transaction-service && mvn spring-boot:run
cd api-gateway         && mvn spring-boot:run
```
Check http://localhost:8761 — ACCOUNT-SERVICE, TRANSACTION-SERVICE and API-GATEWAY should be up.

## Run with Docker Compose
```bash
mvn clean package -DskipTests      # from repo root, builds all four jars
docker compose up --build
```

## Try it
```bash
# create two accounts
curl -X POST localhost:8080/api/accounts -H "Content-Type: application/json" \
  -d '{"accountHolderName":"Alice","accountNumber":"ACC1001","initialDeposit":10000}'
curl -X POST localhost:8080/api/accounts -H "Content-Type: application/json" \
  -d '{"accountHolderName":"Bob","accountNumber":"ACC1002","initialDeposit":500}'

# transfer 2000 from account 1 to account 2
curl -X POST localhost:8080/api/transactions/transfer -H "Content-Type: application/json" \
  -H "Idempotency-Key: key-001" -d '{"fromAccountId":1,"toAccountId":2,"amount":2000}'

# repeat the EXACT same call: same response, money is NOT moved twice (idempotency)
```

## Tests
```bash
mvn test
```

## Design notes
- **Idempotency**: transfers require an `Idempotency-Key` header; a repeated key returns the original result.
- **Circuit breaker**: debit/credit calls to Account Service are wrapped with Resilience4j and a fallback.
- **Optimistic locking**: `@Version` on the Account entity guards concurrent balance updates.
- **Database per service**: Account and Transaction services each own their own MySQL schema; they talk only through REST.

## Roadmap
- Reverse the debit if the credit step fails (known limitation today)
- GET /api/transactions/{id}
- Paginated transaction history
- Gateway rate limiting
- Integration tests with Testcontainers
