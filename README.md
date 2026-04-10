# Real Estate Dashboard 🏠

A professional Full-stack Real Estate Management application built with **Spring Boot 3** and **Java 17**. This system features secure JWT authentication and an interactive dashboard to manage property listings. The project is fully containerized with **Docker** and deployed on **Render** cloud.

[![Live Demo](https://img.shields.io/badge/demo-online-brightgreen)](https://realestate-app-zcxd.onrender.com/login.html)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue)](https://www.postgresql.org/)

## 🌟 Key Features
* **Interactive Dashboard:** A centralized interface to view and manage property listings dynamically.
* **JWT Authentication:** Secure user registration and login system using JSON Web Tokens.
* **Property Details:** Dynamic routing to individual property pages with detailed info.
* **Responsive UI:** Fully optimized for mobile and desktop using modern HTML5, CSS3, and Vanilla JavaScript.
* **Automatic Data Seeding:** Database is pre-populated with sample listings and users for testing.

## 🛠 Tech Stack
* **Backend:** Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA.
* **Database:** PostgreSQL (Cloud Instance).
* **Security:** JWT (Stateless Authentication).
* **Frontend:** HTML5, CSS3, JavaScript (Fetch API).
* **DevOps:** Docker, GitHub, Render Cloud.

## 🚀 Local Setup & Installation

### Prerequisites
* JDK 17+
* Maven 3.8+
* PostgreSQL (Local or Docker)

### Installation
1. Clone the repository:
   ```bash
   git clone [https://github.com/Nikolife13/realestate-app.git](https://github.com/Nikolife13/realestate-app.git)

   Set up your database credentials in src/main/resources/application.properties.

Build and run:

Bash
mvn clean package
java -jar target/realestate-app-0.0.1-SNAPSHOT.jar
Access the app at: http://localhost:8080/login.html

🐳 Docker Support
   Run the entire stack using Docker:

   Bash
   docker build -t realestate-app .
   docker run -p 8080:8080 realestate-app
