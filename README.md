# Forex Application

This application provides functionality for converting currencies, 
retrieving exchange rates, and managing a history of currency conversions. 
It integrates with an external API (Fixer.io) for real-time exchange rates 
and caches results to improve performance.

## Features

- **Currency Conversion**: Convert an amount from one currency to another using real-time exchange rates.
- **Exchange Rate**: Retrieve the latest exchange rate for a given pair of currencies.
- **Conversion History**: View historical conversion records based on transaction ID or conversion date.
- **Error Handling**: Global exception handling for external API errors, validation errors, and missing parameters.

## Technologies Used

- **Spring Boot**: The application is built using Spring Boot for a production-ready environment.
- **Spring Data JPA**: For interaction with the database to store conversion history.
- **RestTemplate**: For consuming the external currency rates API.
- **Spring Caching (Caffeine)**: For caching exchange rates.
- **Spring Validation**: For input validation (e.g., currency codes, amount values).
- **Springdoc OpenAPI**: For automatic generation of OpenAPI documentation.
- **Docker**: For containerization of the application.
- **AWS EC2**: For hosting the application in the cloud.

## Endpoints

### `GET /api/forex/rate`

Retrieve the current exchange rate between two currencies.

**Parameters:**
- `sourceCurrency`: The source currency code (e.g., USD, EUR).
- `targetCurrency`: The target currency code (e.g., USD, EUR).

**Response:**
```json
{
  "sourceCurrency": "USD",
  "targetCurrency": "EUR",
  "exchangeRate": 0.84
}
```
### `POST /api/forex/convert`
Convert an amount from one currency to another.

**Request Body:**
```json
{
  "amount": 100.0,
  "sourceCurrency": "USD",
  "targetCurrency": "EUR"
}
```
**Response:**
```json
{
  "convertedAmount": 84.0,
  "transactionId": "b897a383-3495-4d56-8c7c-348c3a0d540f"
}
```
### `GET /api/forex/history`
Retrieve conversion history.

**Parameters:**
- `transactionId:` Optional transaction ID to filter by.
- `conversionDate:` Optional conversion date to filter by.
- `page:` The page number for pagination (default: 0).
- `size:` The page size for pagination (default: 10).

**Response:**
```json
[
  {
    "transactionId": "b897a383-3495-4d56-8c7c-348c3a0d540f",
    "sourceCurrency": "USD",
    "targetCurrency": "EUR",
    "amount": 100.0,
    "convertedAmount": 84.0,
    "conversionDate": "2025-01-26"
  }
]
```
## Setup and Installation
### 1. Clone the Repository
`git clone https://github.com/hristoiliewh/forex-application`

### 2. Install Dependencies
- `JDK 17`
- `Maven`

### 3. Run the Application locally
`mvn spring-boot:run`

The application will be available at `http://localhost:8080`

### 4. Docker Setup
To run the application using Docker, follow these steps:
1. Pull the Docker image from Docker Hub:
`docker pull hristoiliewh/forex-application:latest`
2. Run the Docker container:
`docker run -p 8080:8080 hristoiliewh/forex-application:latest`

The application will be available at `http://localhost:8080`

### 5. AWS EC2 Deployment
The application is deployed on an AWS EC2 instance. If you want to deploy it yourself, make sure you:
1. Set up an EC2 instance with Docker installed.
2. Push your Docker image to a container registry (like Docker Hub).
3. Pull and run the Docker image on the EC2 instance.

The application will be available at `http://localhost:8080`

## API Documentation
The API documentation is generated using Springdoc OpenAPI and can be accessed at:
`http://localhost:8080/swagger-ui.html`