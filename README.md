# Myfin — Personal Finance Tracker

> A practical demonstration of Domain-Driven Design (DDD) and Hexagonal Architecture using a simple Personal Finance use case.

Myfin is a lightweight personal finance application that reads bank transaction statements, categorizes expenses, and generates monthly and yearly expense summaries.

The application is intentionally simple.

This project is a practical demonstration of architectural principles that I have applied in Enterprise Systems and Integrations, made publicly available through a small and easy-to-understand codebase.

---

## Why Myfin?

Over the years, I have worked with **Domain-Driven Design, Hexagonal Architecture, and Enterprise Integration patterns** in larger systems.

However, architectural concepts are often easier to understand when demonstrated through a small, concrete application.

Myfin was created with that objective:

- Demonstrate **Domain-Driven Design** in a small domain
- Apply **Hexagonal Architecture / Ports & Adapters**
- Keep business logic independent of infrastructure
- Demonstrate dependency direction
- Make external technologies replaceable
- Explore the practical trade-offs of architectural abstraction
- Provide a codebase that others can read, experiment with, and discuss

The application uses bank statements as an example input, but the architectural principles are applicable to much larger enterprise systems.

---

## Architecture

Myfin follows a Hexagonal Architecture approach where the **domain and application logic remain at the centre**, while infrastructure and external technologies are pushed towards the edges.

```text
                         ┌─────────────────────┐
                         │    Bank Statement   │
                         │       (Excel)       │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │       Adapter       │
                         │  Excel / Persistence│
                         └──────────┬──────────┘
                                    │
                                    ▼
                              ┌───────────┐
                              │   Port    │
                              │           │
                              │ Repository│
                              └─────┬─────┘
                                    │
                                    ▼
                 ┌─────────────────────────────────┐
                 │                                 │
                 │          APPLICATION            │
                 │                                 │
                 │   Expense Processing             │
                 │   Categorisation                 │
                 │   Expense Metrics                │
                 │                                 │
                 │              DOMAIN              │
                 │                                 │
                 │   Expense                        │
                 │   Expense Metric                 │
                 │                                 │
                 └─────────────────────────────────┘
```
## Features
 * Bucketises the expenses into multiple Categories
 * Calculates Year-wise and month-wise expenses
