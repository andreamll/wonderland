# 🐇 Wonderland Immigration Portal

> *Down the Docker rabbit hole: a containerized full-stack application with Spring Boot, MySQL, Nginx, and Docker Compose.*

## 🎩 Welcome to Wonderland

By decree of the Honorable Minister of Interior, Mr. Mad Hatter, Wonderland has finally decided to regulate its borders.

Travelers are no longer encouraged to enter the country through unexpected rabbit holes. Instead, they must submit a proper immigration application through the **Wonderland Immigration Portal**.

Applicants provide their personal information, select a visa category, and submit supporting documents. The Ministry then assigns an application ID and issues a decision — with only moderate interference from the Queen of Hearts.

The application began as a cloud computing and cybersecurity project at Douglas College and was originally deployed entirely on Google Cloud Platform. It is now being **modernized as a portfolio project**, with a focus on containerization, cloud infrastructure, automation, CI/CD, and Infrastructure as Code.

---

## 🏰 The Original Wonderland

The original application used a multi-layer architecture deployed on **Google Cloud Platform**:

* Static HTML/CSS/JavaScript frontend hosted by Apache on a Compute Engine VM
* Spring Boot REST API hosted on a separate Compute Engine VM
* MySQL database hosted in Cloud SQL
* Uploaded documents stored in Google Cloud Storage
* GCP service-account identity used by the backend to access Cloud Storage

The separation allowed structured immigration data to live in a relational database while passports, explanation letters, and other supporting documents were stored as objects in a Cloud Storage bucket.

It worked.

The Cheshire Cat approved.

But it depended heavily on its original cloud environment.

---

## 🐳 Down the Docker Rabbit Hole

The first modernization phase replaces the VM-dependent local architecture with containers.

The current development environment consists of three services connected through two purpose-specific Docker networks:

```text
                         Browser
                            │
                            │ localhost:8081
                            ▼
                 ┌─────────────────────┐
                 │      Frontend       │
                 │        Nginx        │
                 │   HTML / CSS / JS   │
                 └─────────────────────┘
                            │
                   frontend-backend
                            │
                            │ reverse proxy
                            │ backend:8080
                            ▼
                 ┌─────────────────────┐
                 │       Backend       │
                 │     Spring Boot     │
                 │       Java 21       │
                 └─────────────────────┘
                            │
                   backend-database
                            │
                            │ JDBC
                            │ db:3306
                            ▼
                 ┌─────────────────────┐
                 │        MySQL        │
                 │         8.4         │
                 └─────────────────────┘
                            │
                            ▼
                       mysql-data
                      named volume
```

Docker Compose orchestrates the environment, while separate networks limit direct communication between application layers.

---

## 🫖 What's in the Tea Party?

### Frontend

The frontend is a static web application built with:

* HTML
* CSS
* JavaScript
* Nginx

Nginx serves the static resources from its container, with container port `80` published locally as `8081`.

It also acts as a **reverse proxy** for backend API requests. The browser therefore communicates with a single application origin instead of requiring direct knowledge of the backend address.

### Backend

The backend is a REST API built with:

* Java 21
* Spring Boot
* Maven
* Spring Data JPA
* Hibernate

The application is packaged as an executable JAR and runs inside a Java runtime container.

The backend belongs to both application networks, allowing it to communicate with the frontend layer and the database while keeping those two layers isolated from each other.

### Database

Application data is stored in:

* MySQL 8.4

The database runs in its own container and uses a Docker named volume to preserve data independently of the container lifecycle.

Its MySQL port is not published to the Docker host because direct host access is not required by the application. Database communication occurs only through the `backend-database` Docker network.

---

## 🐱 Docker Networks & Service Discovery

Wonderland uses two Docker bridge networks:

```text
frontend-backend
backend-database
```

Their membership is deliberately separated:

```text
frontend-backend
├── frontend
└── backend

backend-database
├── backend
└── db
```

The backend participates in both networks because it acts as the application boundary between the frontend and the database.

The frontend and database do not share a network and therefore cannot communicate directly.

This applies **network segmentation and least-privilege connectivity**: each service receives only the network access required for its role.

Docker's internal DNS provides service discovery within these networks. Instead of depending on hardcoded container IP addresses, services communicate using Compose service names.

For example, the backend reaches MySQL using:

```text
db:3306
```

and Nginx can reach the backend using:

```text
backend:8080
```

Container IP addresses may change when containers are recreated, while service names remain stable.

Inside a container, `localhost` always refers to that container itself — not another container.

---

## 🪞 Through the Looking Glass — Nginx Reverse Proxy

The original frontend called the backend directly using a hardcoded development URL:

```javascript
fetch("http://localhost:8080/v1/api/applications")
```

This worked locally because the JavaScript executes in the user's browser, where `localhost` refers to the Docker host, and Docker published host port `8080` to the backend container.

However, this coupled frontend application code to a specific local infrastructure configuration.

The frontend now uses a relative API path:

```javascript
fetch("/v1/api/applications")
```

From the browser's perspective, the request is sent back to the same origin that served the frontend:

```text
Browser
   │
   │ /v1/api/applications
   ▼
Nginx
   │
   │ proxy_pass
   ▼
backend:8080
   │
   ▼
Spring Boot
```

Unlike the browser, Nginx runs inside the `frontend` container and participates in the `frontend-backend` Docker network. It can therefore resolve the `backend` service name using Docker's internal DNS.

This removes the backend host and port from the frontend application code and creates a cleaner separation between **application logic and infrastructure configuration**.

It also keeps frontend and API requests under the same browser origin, simplifying cross-origin communication.

The same relative API path can later remain unchanged when Wonderland moves from the local Docker environment to AWS.

---

## ❤️ Is the Database Alive?

A running container does not necessarily mean that the service inside it is ready.

MySQL may still be initializing even after its container has started.

For that reason, the database uses a healthcheck:

```yaml
healthcheck:
  test: ["CMD-SHELL", "mysqladmin ping -h localhost -u${DB_USER} -p${DB_PASSWORD}"]
  interval: 10s
  timeout: 5s
  retries: 5
  start_period: 20s
```

The backend then waits for MySQL to become healthy:

```yaml
depends_on:
  db:
    condition: service_healthy
```

In other words:

```text
"Start the container!"
        ↓
      running

"Are you actually ready?"
        ↓
    healthcheck

"Yes."
        ↓
      healthy

"Release the Spring Boot application!"
        ↓
      backend
```

Even the Queen must wait for MySQL initialization.

The frontend also declares a startup dependency on the backend so that Nginx can resolve its upstream service when starting.

A backend healthcheck is planned as part of the next container build and runtime improvements.

---

## 🔐 Secrets Are Not Welcome at This Tea Party

Database credentials are no longer hardcoded in the Spring configuration or Docker Compose file.

Local configuration is provided through:

```text
.env
```

which is excluded from Git.

The repository contains:

```text
.env.example
```

to document the required variables without exposing real credentials.

Spring Boot also consumes configuration through environment variables, for example:

```properties
spring.datasource.password=${DB_PASSWORD}
```

This keeps runtime configuration outside the application artifact.

---

## 💾 Persistent Memories of Wonderland

Containers are ephemeral.

Immigration records should not be.

The MySQL service therefore uses a named volume:

```yaml
volumes:
  - mysql-data:/var/lib/mysql
```

This separates the database lifecycle from the container lifecycle, allowing the database container to be recreated without automatically destroying its data.

---

## ☁️ A Note About the Old Kingdom — GCP

The original Wonderland backend uploaded supporting documents to:

```text
wonderland-bucket
```

in Google Cloud Storage.

When the backend ran on a Google Compute Engine VM, the application could obtain Google Application Default Credentials from the cloud environment.

After moving the backend into a local Docker container, that implicit identity was no longer available.

Rather than embedding cloud credentials inside the image, the GCP upload operation is currently disabled in the local containerized version.

This integration is planned to be replaced with **Amazon S3** during the AWS modernization phase.

No service-account keys are baked into the Docker image.

The Mad Hatter considers this progress.

---

## 🧪 Running Wonderland Locally

Create your local environment configuration:

```bash
cp .env.example .env
```

Provide appropriate local values, then build the Spring Boot artifact:

```bash
cd backend
./mvnw clean package
cd ..
```

Build and start the containers:

```bash
docker compose up --build
```

Check their status:

```bash
docker compose ps
```

The expected environment contains:

```text
frontend    Up
backend     Up
db          Up (healthy)
```

Then visit:

```text
http://localhost:8081
```

The browser communicates with the application through the frontend entry point. API requests are forwarded internally by Nginx to the Spring Boot backend.

Welcome to Wonderland.

Please have your passport ready.

---

## 🗺️ Modernization Roadmap

Wonderland is still under construction.

The planned journey includes:

* [x] Containerize the Spring Boot backend
* [x] Containerize MySQL
* [x] Add persistent database storage
* [x] Containerize the frontend with Nginx
* [x] Orchestrate services with Docker Compose
* [x] Externalize local configuration and credentials
* [x] Add MySQL healthcheck
* [x] Add health-based backend startup dependency
* [x] Separate frontend/backend and backend/database Docker networks
* [x] Remove the hardcoded frontend backend URL
* [x] Add Nginx reverse proxy for API requests
* [ ] Improve container build strategy
* [ ] Add backend healthcheck and improve service readiness handling
* [ ] Add GitHub Actions CI/CD
* [ ] Deploy the application to AWS
* [ ] Replace Google Cloud Storage with Amazon S3
* [ ] Provision infrastructure with Terraform
* [ ] Review production secrets management and security controls

---

## 🛡️ Security Journey

The original application was deliberately developed as a prototype and was later evaluated for production security concerns.

The current containerization work has already introduced:

* Network segmentation between application layers
* Removal of unnecessary host exposure for MySQL
* Externalized local credentials
* No cloud credentials embedded in container images
* Service readiness checks for MySQL

Areas still identified for improvement include:

* HTTPS/TLS
* Private cloud networking
* Least-privilege IAM
* Authentication and authorization
* Server-side input validation
* File upload validation and malware scanning
* Rate limiting
* Server hardening
* Production-grade secrets management
* Reduced public exposure of backend services

These concerns will be revisited as the application moves toward its AWS architecture.

---

## 🐇 Why Wonderland?

Wonderland started as an academic cloud application, but its second life has a different purpose.

The project is being used as a hands-on environment for exploring:

**Docker → GitHub Actions → AWS → Terraform → Infrastructure as Code and automation**

Rather than building disconnected tutorial projects, each technology is being applied to the same evolving application.

The goal is not simply to make Wonderland run.

The goal is to understand **why it runs, how its components communicate, where it can fail, and how its infrastructure can be made reproducible, secure, and automated.**

---

## 👑 Final Decree

> Visitors may arrive from any kingdom, but production traffic through unidentified rabbit holes is strictly prohibited.

Built with Java, Spring Boot, MySQL, Nginx, Docker, curiosity, and occasional guidance from the Cheshire Cat. 🐱

