📈 Stock Broking Platform

A full-stack backend system built using **Spring Boot** that simulates core functionalities of a stock broking platform. It provides APIs for managing clients, instruments, orders, and watchlists, complete with validation, business rules, and test coverage.

---

🚀 Features

✅ Client Management
- Create, update, fetch, delete clients
- KYC & account status validation
- Search by name or client code

📊 Instrument Management
- Add, update, fetch instruments
- Filter by exchange type
- Search by symbol or company name

📝 Order Management
- Place new orders with validations
- Modify/cancel only PENDING orders
- Get all/pending/client-specific orders
- Status transitions (e.g., to EXECUTED)

📋 Watchlist Management
- Create/update/delete watchlists (max 5 per client)
- Add/remove instruments (max 20 per watchlist)
- Mark one as default (must retain at least one)
- Generate summary (count + total value)

---

🧠 BUSINESS RULES:

Clients
- Must have **ACTIVE** status and **COMPLETED** KYC to place orders
- Cannot exceed 5 watchlists

Orders
- Quantity must be positive and a multiple of instrument's lot size
- Price must be a positive number
- Only **PENDING** orders can be modified or canceled

Watchlists
- Must have at least one default per client
- Cannot exceed 20 instruments
- No duplicates allowed

---

🛠️ Tech Stack

| Layer           | Tools Used                     |
|-----------------|--------------------------------|
| Language        | Java 17                        |
| Framework       | Spring Boot, Spring Web        |
| Persistence     | Spring Data JPA, H2 DB         |
| API Docs        | SpringDoc OpenAPI (Swagger UI) |
| Testing         | JUnit 5, Mockito, MockMvc      |
| Build Tool      | Maven                          |

---

📂 Project Structure
```
StockBrokingPlatform/
│
├── src/
│   ├── main/
│   │   ├── java/com/example/StockBrokingPlatform/
│   │   │   ├── controller/             # REST API endpoints (Clients, Orders, Watchlists, Instruments)
│   │   │   ├── service/                # Business logic and validation (OrderService, ClientService, etc.)
│   │   │   ├── repository/             # JPA Repositories for all entities
│   │   │   ├── model/                  # Domain models/entities (Client, Instrument, Order, WatchList)
│   │   │   ├── DTO/                    # Data Transfer Objects (ClientDTO, OrderDTO, etc.)
│   │   │   ├── mapper/                 # Entity ↔ DTO converters
│   │   │   └── exception/              # Custom exceptions (e.g., ResourceNotFoundException)
│   │   └── resources/
│   │       ├── application.properties # Configuration (H2 DB, JPA settings, Swagger)
│   │       └── data.sql                # Initialize Database
│
├── src/
│   └── test/
│       └── java/com/example/StockBrokingPlatform/
│           ├── service/               # Unit tests 
│           └── controller/            # Integration tests using MockMvc
│
├── pom.xml                            # Maven project file with all dependencies
└── README.md                          # Project overview and documentation
```


---

🔍 API Endpoints

All APIs are prefixed with /api :

Clients

| Method | Endpoint              | Description               |
|--------|-----------------------|---------------------------|
| POST   | `/clients`            | Create client             |
| GET    | `/clients`            | Get all clients           |
| GET    | `/clients/{id}`       | Get client by ID          |
| PUT    | `/clients/{id}`       | Update client             |
| DELETE | `/clients/{id}`       | Delete client             |
| GET    | `/clients/search`     | Search by name/code       |

Instruments

| Method | Endpoint              | Description               |
|--------|-----------------------|---------------------------|
| GET    | `/instruments`        | Paginated instruments     |
| GET    | `/instruments/{id}`   | Get instrument by ID      |
| POST   | `/instruments`        | Add new instrument        |
| PUT    | `/instruments/{id}`   | Update instrument         |
| GET    | `/instruments/search` | Search instruments        |
| GET    | `/instruments/type`   | Filter by exchange type   |

Orders

| Method | Endpoint                   | Description                  |
|--------|----------------------------|------------------------------|
| POST   | `/orders`                  | Place order                  |
| GET    | `/orders`                  | Get all orders               |
| GET    | `/orders/{id}`             | Get order by ID              |
| PUT    | `/orders/{id}/modify`      | Modify PENDING order         |
| DELETE | `/orders/{id}`             | Cancel PENDING order         |
| GET    | `/orders/client/{clientId}`| Get orders for client        |
| GET    | `/orders/pending`          | Get all PENDING orders       |
| PUT    | `/orders/{id}/status`      | Update order status          |

Watchlists

| Method | Endpoint                                      | Description                         |
|--------|-----------------------------------------------|-------------------------------------|
| POST   | `/watchlists`                                 | Create watchlist                    |
| GET    | `/watchlists/client/{clientId}`               | Get client’s watchlists             |
| GET    | `/watchlists/{id}`                            | Get watchlist by ID                 |
| PUT    | `/watchlists/{id}`                            | Update watchlist name               |
| DELETE | `/watchlists/{id}`                            | Delete watchlist                    |
| POST   | `/watchlists/{id}/instruments`                | Add instrument to watchlist         |
| DELETE | `/watchlists/{id}/instruments/{instrumentId}` | Remove instrument from watchlist    |
| GET    | `/watchlists/{id}/summary`                    | Summary: count + total value        |
| GET    | `/watchlists/{id}/with-items`                 | Watchlist with instrument details   |

---

✅ Tests

- ClientServiceTest: Unit tests for service methods
- ClientControllerIntegrationTest: End-to-end tests via MockMvc
- Similar test scaffolds can be applied for Orders and Watchlists

Run all tests using:
```bash
./mvnw test
````

---

📦 Setup & Run

Requirements

* Java 17+
* Maven 3.6+

Run locally

```bash
./mvnw spring-boot:run
```

Swagger UI
http://localhost:8080/swagger-ui.html

---

👩‍💻 Authors:
Trishala Singhavi & Dhruvi Patel

---

This project is licensed under the MIT License - feel free to use, modify, and distribute.
