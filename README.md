# 📦 Order Notification System

A microservices-based application built with **Spring Boot**, **Apache Kafka**, **MySQL**, and **Docker**. It demonstrates event-driven architecture where an Order Service publishes order events to Kafka, and a Notification Service consumes those events and sends email confirmations to customers.

---

## 🏗️ Architecture

```
┌─────────────────────┐         ┌──────────────────┐         ┌────────────────────────┐
│                     │         │                  │         │                        │
│    Order Service    │ ──────► │  Apache Kafka    │ ──────► │  Notification Service  │
│    (Port 8081)      │ publish │  (order-events)  │ consume │    (Port 8082)         │
│                     │         │                  │         │                        │
└────────┬────────────┘         └──────────────────┘         └───────────┬────────────┘
         │                                                                │
         ▼                                                                ▼
   ┌───────────┐                                                   ┌───────────┐
   │  orderdb  │                                                   │notificati │
   │  (MySQL)  │                                                   │  ondb     │
   └───────────┘                                                   │  (MySQL)  │
                                                                   └───────────┘
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Spring Boot 3.x | Microservice framework |
| Apache Kafka | Event streaming / messaging |
| MySQL 8.0 | Persistent storage |
| Docker & Docker Compose | Containerization |
| Spring Data JPA | Database ORM |
| Lombok | Boilerplate reduction |

---

## 📁 Project Structure

```
order-notification-system/
│
├── order-service/                  # Produces order events to Kafka
│   ├── src/main/java/
│   │   └── com/sahil/order_service/
│   │       ├── controller/         # REST API endpoints
│   │       ├── model/              # OrderEvent model
│   │       └── producer/           # Kafka producer
│   ├── src/main/resources/
│   │   └── application.yml
│   ├── docker-compose.yml          # Kafka + Zookeeper + MySQL
│   └── pom.xml
│
└── notification-service/           # Consumes order events from Kafka
    ├── src/main/java/
    │   └── com/sahil/notification_service/
    │       ├── consumer/           # Kafka consumer
    │       ├── model/              # OrderEvent + NotificationLog
    │       ├── repository/         # JPA repository
    │       └── service/            # Email service
    ├── src/main/resources/
    │   └── application.yml
    └── pom.xml
```

---

## ⚙️ Key Features

- **Event-Driven Architecture** — Services communicate asynchronously via Kafka topics
- **Idempotent Producer** — Prevents duplicate messages on retry using `enable.idempotence=true`
- **Idempotent Consumer** — Tracks processed orders in `notification_log` table to skip duplicates
- **Manual Offset Commit** — Kafka offsets are committed only after successful processing (`MANUAL_IMMEDIATE`)
- **Error Handling Deserializer** — Prevents poison-pill messages from crashing the consumer
- **Batching & Throughput** — Producer configured with `linger.ms`, `batch.size`, and `buffer.memory`

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven
- Docker & Docker Compose
- MySQL client (optional)

### 1. Start Infrastructure (Kafka + Zookeeper + MySQL)

```bash
cd order-service
docker-compose up -d
```

### 2. Create Databases

Connect to MySQL and run:

```sql
CREATE DATABASE orderdb;
CREATE DATABASE notificationdb;
```

Or via Docker:

```bash
docker exec -it mysql mysql -u root -proot -e "CREATE DATABASE orderdb; CREATE DATABASE notificationdb;"
```

### 3. Run Order Service

```bash
cd order-service
mvn spring-boot:run
```

### 4. Run Notification Service

```bash
cd notification-service
mvn spring-boot:run
```

---

## 📬 API Endpoints

### Place a Single Order

```http
POST http://localhost:8081/api/orders
Content-Type: application/json

{
  "customerId": "C001",
  "item": "Laptop",
  "amount": 75000
}
```

**Response:**
```
Order Placed <orderId>
```

### Bulk Test (10 Orders)

```http
POST http://localhost:8081/api/orders/bulk-test
```

**Response:**
```
10 order fired
```

---

## 🔄 Flow

1. Client sends a POST request to the **Order Service**
2. Order Service assigns a UUID, sets status to `Placed`, and publishes the event to Kafka topic `order-events`
3. **Notification Service** consumes the event from Kafka
4. It checks `notification_log` table — if the `orderId` was already processed, it skips (idempotency)
5. If new, it sends an email confirmation and saves a record to `notification_log`
6. Kafka offset is manually committed only after successful processing

---

## 🐳 Docker Compose Services

| Service | Image | Port |
|---|---|---|
| Zookeeper | confluentinc/cp-zookeeper:7.5.0 | 2181 |
| Kafka | confluentinc/cp-kafka:7.5.0 | 9092 |
| MySQL | mysql:8.0 | 3306 |

---

## 👨‍💻 Author

**Sahil** — Entry-level Java/Spring Boot Developer  
GitHub: [@Nishal2309](https://github.com/Nishal2309)
