# Intelligent Stock Forecasting & Secure Multi-Warehouse System

## Overview

This project is an **Intelligent Stock Forecasting and Secure Multi-Warehouse Management System** built for a distribution company managing multiple warehouses.

The system solves two main problems:

* **Stock forecasting**: predict shortages and recommend replenishment quantities using AI.
* **Security**: protect sensitive data with strict role-based access control.

The application is developed with **Spring Boot**, secured with **JWT**, and fully **containerized using Docker**.

---

## Main Features

### AI & Forecasting

* Predict when a product will run out of stock
* Forecast demand for the next 30 days
* Generate recommendations such as:

    * *Order 200 units*
    * *Stock sufficient*
    * *Alert: abnormal sales*

AI is powered by **local LLMs via Ollama**.

---

### Security & Roles

#### ADMIN

* Access to all warehouses
* View sensitive data (purchase prices, margins)
* Manage products, warehouses, and users
* View all sales history and forecasts

#### GESTIONNAIRE (Warehouse Manager)

* Access to **only one assigned warehouse**
* Update stock for their warehouse
* View sales history for their warehouse
* Cannot manage users
* Cannot see purchase prices or margins

---

## Technologies Used

* Java 17+ / Spring Boot
* REST API
* Spring Data JPA
* Spring Security + JWT
* PostgreSQL
* Ollama (LLM / AI)
* Docker & Docker Compose
* JUnit & Mockito
* GitHub Actions (CI)

---

## Prerequisites

You need only:

* Docker
* Docker Compose
* Git

⚠️ No local database or Java installation required.

---

## AI & LLM Setup

### Default LLM (Recommended)

The project uses **TinyLlama** by default for fast and lightweight inference.

```bash
ollama pull tinyllama:latest
```

### Using a Different LLM (Optional)

You can use **any Ollama-compatible model**, for example:

```bash
ollama pull llama3
ollama pull mistral
ollama pull phi
```

As long as the model is available in Ollama, the application can use it.

---

## Running the Project with Docker

### 1️⃣ Clone the repository

```bash
git clone <repository-url>
cd <project-folder>
```

---

### 2️⃣ Build the application

```bash
mvn clean package -DskipTests
```

---

### 3️⃣ Start the full stack

```bash
docker compose up --build
```

This will start:

* Spring Boot backend
* PostgreSQL database
* Ollama AI service

---

### 4️⃣ Access the services

| Service     | URL                                              |
| ----------- | ------------------------------------------------ |
| Backend API | [http://localhost:8080](http://localhost:8080)   |
| Ollama API  | [http://localhost:11434](http://localhost:11434) |
| PostgreSQL  | localhost:5432                                   |

---

## CI/CD

The project includes a **GitHub Actions CI pipeline**:

* Automatically builds and tests the application
* Validates Docker build
* Runs on every push and pull request


---

## Notes

* Database connection uses Docker service names
* AI communication is done through the Ollama container
* Data is persisted using Docker volumes
