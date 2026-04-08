# 🔧 ServeTech

A full-stack web application for managing service and technology requests end-to-end, built with a Java Spring Boot backend and a vanilla JavaScript frontend.

---

## ✨ Features

- Submit and manage service/tech requests through a clean web interface
- RESTful API backend for reliable, structured data operations
- Responsive frontend built with vanilla JS, CSS, and HTML
- Modular project structure with clear separation of frontend and backend

---

## 🛠️ Tech Stack

**Frontend**
- JavaScript
- CSS
- HTML

**Backend**
- Java
- Spring Boot
- REST API

---

## 🏗️ Architecture

ServeTech is organized into two dedicated modules:

- **`back-end/`** — Spring Boot application that handles business logic and exposes REST endpoints
- **`front-end/`** — Vanilla JS/CSS/HTML interface that communicates with the backend via API calls

This separation keeps the codebase clean, maintainable, and easy to extend.

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven
- A modern web browser (for the frontend)

### Backend Setup

```bash
cd back-end
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

Open `front-end/index.html` directly in your browser, or serve it using a simple static server:

```bash
cd front-end
npx serve .
```

> Update the API base URL in the frontend JS files to point to your running backend instance.

---

## 📁 Project Structure

```
ServeTech/
├── back-end/               # Spring Boot backend
│   ├── src/main/java/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── model/
│   │   └── repository/
│   └── pom.xml
├── front-end/              # Vanilla JS frontend
│   ├── index.html
│   ├── style.css
│   └── app.js
└── README.md
```

---

## 📡 API Endpoints

| Method | Endpoint               | Description                  |
|--------|------------------------|------------------------------|
| GET    | `/api/requests`        | Get all service requests     |
| POST   | `/api/requests`        | Submit a new request         |
| PUT    | `/api/requests/{id}`   | Update an existing request   |
| DELETE | `/api/requests/{id}`   | Delete a request             |

---

## 📄 License

This project is licensed under the [Apache 2.0 License](LICENSE).

---

## 🙋‍♂️ Author

**Bhagat Singh**
[GitHub](https://github.com/BhagatSingh23)
