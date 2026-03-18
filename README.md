![Banner](https://raw.githubusercontent.com/Vishnu-Yadav0/Revshop-notification-service/main/banner.png)

# 🔔 RevShop — Notification Service

The communication engine for the RevShop platform. Sends real-time in-app notifications, email alerts for orders, and inventory warnings to sellers.

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Twilio](https://img.shields.io/badge/Twilio-SMS-red?style=flat-square&logo=twilio)](https://www.twilio.com/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-blue?style=flat-square&logo=docker)](https://www.docker.com/)

---

## Capabilities

- **In-App Notifications:** Real-time updates for buyers and sellers.
- **Order Confirmations:** Automatic email/SMS receipts upon successful checkout.
- **Inventory Alerts:** Notifies sellers immediately when products are low on stock.
- **Marketing & OTP:** Support for promotional notifications and security verification codes.

## Tech Stack

| Component | Technology |
|---|---|
| Runtime | Spring Boot 3 |
| Emails | JavaMailSender (SMTP) |
| SMS / OTP | Twilio |
| Database | MySQL (Notification Logs) |
| Container | Docker |

## Usage

This service is typically called asynchronously or via Feign by other services like **Payment**, **Order**, and **Inventory**.

---

## Ecosystem Links

- 🌐 [Front-end](https://github.com/Vishnu-Yadav0/Revshop-frontend)
- ⚙️ [Gateway](https://github.com/Vishnu-Yadav0/Revshop-api-gateway)
- 👤 [Direct User Access](https://github.com/Vishnu-Yadav0/Revshop-user-service)

