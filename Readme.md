# Enterprise Expense Management System - Backend

This repository contains the backend implementation of the **Enterprise Expense Management System** — a secure, scalable RESTful API service for managing user expenses with full CRUD functionality, pagination, sorting, filtering, and file attachment support.

---
Deployment link : https://codeshapeitsolution-ems-backend.onrender.com
---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

---

## Project Overview

This backend service supports authenticated users to manage their expenses with the following core capabilities:

- User authentication and authorization (JWT & OAuth2)
- CRUD operations on expenses
- Pagination, sorting, and filtering of expenses
- Upload and download of invoice attachments (PDF, PNG, JPG)
- Summaries and reports of expenses (monthly, yearly, by category)
- Search functionality by keywords

The backend is developed using **Spring Boot** and follows clean architecture and security best practices.

---

## Features

- **Secure authentication:** JWT and OAuth2 (Google Sign-In)
- **Expense Management:** Create, Read, Update, Delete expenses
- **File Uploads:** Attach invoices to expenses
- **Pagination & Sorting:** Efficient data retrieval with pageable APIs
- **Filtering:** Filter expenses by date and category
- **Reporting:** Expense summaries for insights
- **RESTful API:** Well-structured endpoints with JSON responses

---

## Technologies Used

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA (Hibernate)
- MySQL / H2 (for development)
- Maven
- JWT for authentication
- OAuth2 (Google) integration
- Lombok for boilerplate reduction

---

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.8+
- MySQL database (or embedded H2 for testing)

### Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/enterprise-expense-backend.git
   cd enterprise-expense-backend
