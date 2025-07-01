# 🦉 NEU Finance<br/>*open-source Robo-Advisor & Quant-Factor Research Platform*

[![Code Style](https://img.shields.io/badge/code%20style-Prettier-blue.svg)](https://prettier.io)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

> **NEU Finance** combines a Spring Boot micro-service core, a VBen Admin front-end and a Python research toolkit to deliver an extensible, production-ready robo-advisory stack.



---

## ✨ Key Features
- **Factor-Driven Portfolio Engine** – plug-in research factors, run back-tests, and auto-construct portfolios aligned with user risk profiles.  
- **Modular Front-End** – powered by Vue 3 + Vite and the VBen Admin monorepo template for lightning-fast dev experience and out-of-the-box Dark Mode 🌘. :contentReference[oaicite:0]{index=0}
- **Spring Boot 3 REST API** – with MyBatis, JWT auth and MySQL persistence. :contentReference[oaicite:1]{index=1}
- **Research “Factor” Lab** – Python notebooks & scripts for signal generation, alpha evaluation and walk-forward validation.  
- **Docker-First Deploy** – one-command spin-up for local demos or cloud roll-outs.  
- **Role-Based Access Control, Audit Logs & Theming** – production niceties baked in.

---

## 🏗 Architecture

```mermaid
flowchart LR
  subgraph "Frontend (Vue 3 + Vite)"
    A[Browser] --> B(Widget SPA)
  end
  subgraph "Backend ( Spring Boot 3 )"
    C(API Gateway) --> D(User Service)
    C --> E(Portfolio Service)
    C --> F(Risk Engine)
    F --> G[MySQL]
  end
  subgraph "Research Layer"
    H[Jupyter / Python CLI] --> E
  end
  A <--> C
  H --gRPC--> C
````


---

## 🚀 Quick Start

### 1. Clone & Bootstrap

```bash
git clone https://github.com/yohbii/NEU_finance.git
cd NEU_finance
```

### 2. Spin Up All Services (Dev Mode)

```bash
# requires Docker & pnpm
docker compose -f docker-compose.dev.yml up --build
```

The stack exposes:

* **Frontend** → [http://localhost:5555](http://localhost:5555)
* **Backend API** → [http://localhost:8081/api](http://localhost:8081/api)
* **MySQL** → `localhost:3306` (env vars in `docker/.env`)

### 3. Login

Default credentials: `super`
(See `backend/src/main/resources/data.sql` for seed data.)

---

## 🧰 Tech Stack

| Layer        | Tech                                                |
| ------------ | --------------------------------------------------- |
| Front-End    | **Vue 3**, Vite, VBen Admin, Pinia, Tailwind CSS    |
| Back-End     | **Java 17**, Spring Boot 3.5.x, MyBatis, JWT, MySQL |
| Data & Quant | **Python 3.11**, pandas, NumPy, backtrader          |
| Dev-Ops      | Docker, GitHub Actions, pnpm monorepo workflows     |

---

## 📁 Repo Layout

```
NEU_finance/
├─ backend/      # Spring Boot source & tests
├─ frontend/     # Vue 3 (VBen) monorepo apps & UI lib
├─ factor/       # Python factor research & notebooks
└─ harmony/      # Shared API contracts & SDKs
```

Languages split: \~54 % Vue, 37 % TypeScript, 5 % Java, others. ([github.com][1])

---

## 🛣️ Roadmap

* [ ] **Live Paper-Trading Bridge** (Alpaca / Interactive Brokers)
* [ ] **Multi-factor Optimiser** with Bayesian search
* [ ] **iOS / Android Companion** (Capacitor)
* [ ] **Quant Community Plug-ins** – share-and-play marketplace

---

## 🤝 Contributing

1. Fork & create feature branch (`git checkout -b feat/my-awesome-thing`)
2. Commit your changes with conventional commits
3. Push & open a PR – ensure CI is 💚

Need help? Open an [Issue](../../issues) or ping us on the **Discussions** tab.

---

## 📜 License

This project is licensed under the **MIT License** – see [`LICENSE`](LICENSE) for details.

---

> *Made with ☕, 📈 and far too many late-night back-tests.*

```

---

Enjoy!
::contentReference[oaicite:3]{index=3}
```

[1]: https://github.com/yohbii/NEU_finance "GitHub - yohbii/NEU_finance"
