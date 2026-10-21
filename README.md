# Job Tracker Backend

A Spring Boot REST API that helps job seekers organize and track their job
applications. Users can register, sign in, and manage application details,
including company names, job roles, application statuses, and notes.

## Features

- User registration with BCrypt password hashing.
- User sign-in with JWT access tokens.
- Renew access tokens using a database-backed refresh token stored in an HttpOnly cookie.
- Logout with refresh token deletion and cookie clearing.
- Create, view, update, and delete job applications.
- Store company name, job role, application status, and notes for each application.
- Supported application statuses: `APPLIED`, `INTERVIEW`, `OFFER`, and `REJECTED`.
- User-specific access: users can only view and manage their own applications.

## Tech Stack

| Technology / Tool | Purpose |
| ------------------| ------- |
| Java 21 | Programming language |
| Spring Boot 4.0.3 | Application framework |
| Spring Web MVC | REST API development |
| Spring Security | Authentication and endpoint protection |
| Spring Data JPA / Hibernate | Database persistence and entity mapping |
| MySQL | Relational database |
| JJWT | JWT generation and validation |
| BCrypt | Password hashing |
| Maven | Dependency management and build tool |
| Lombok | Reduce boilerplate code |
| dotenv-java | Load configuration from a `.env` file |
| Postman | Manual API testing |
| Git | Version control |
| GitHub | Repository hosting |

## Project Structure

```text
src/main/
├── java/com/ravi/job_tracker_backend/
│   ├── controller/     # REST endpoints
│   ├── dto/            # Request and response objects
│   ├── entity/         # Database entities and application status enum
│   ├── exception/      # Custom exceptions and global exception handling
│   ├── repository/     # Database access through Spring Data JPA
│   ├── security/       # JWT authentication, security, and CORS configuration
│   ├── service/        # Business logic
│   └── JobTrackerBackendApplication.java
└── resources/
    └── application.properties
```

## Getting Started

### Prerequisites

Install the following before running the application:

- JDK 21
- MySQL Server
- Git

The project includes the Maven Wrapper, so a separate Maven installation is optional.

You can use IntelliJ IDEA for development and Postman for manual API testing.

### 1. Clone the Repository

[View the repository on GitHub](https://github.com/ravisankar-b/job-tracker-backend)

```bash
Clone the project:
git clone https://github.com/ravisankar-b/job-tracker-backend.git
cd job-tracker-backend
```

### 2. Set Up the Database

Ensure MySQL Server is installed and running.

Connect to your MySQL Server using MySQL Workbench or a MySQL command-line client with your configured hostname, port, username, and password.

For a local installation, the hostname is usually `localhost` and the port is `3306`.

Create the database:

```sql
CREATE DATABASE job_tracker;
```

Hibernate creates or updates the application tables when the backend starts.

### 3. Configure Environment Variables

Create a `.env` file in the project root, alongside `pom.xml`:

```properties
DB_URL=jdbc:mysql://localhost:3306/job_tracker
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_jwt_signing_secret
```

Replace the placeholders with your MySQL credentials and JWT signing secret. Adjust the hostname and port if your MySQL Server uses different settings.

The database name in `DB_URL` must match the database you created.

Keep `.env` excluded from Git using `.gitignore`.

### 4. Run the Application

Open a terminal in the project root, where `pom.xml` and `.env` are located.

**Windows — PowerShell:**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS / Linux:**

```bash
./mvnw spring-boot:run
```

The API runs at:

```text
http://localhost:8080
```

Alternatively, open the project in IntelliJ IDEA and run the
`JobTrackerBackendApplication` class. Ensure the working directory is
the project root so the application can load `.env`.

## API Endpoints

Base URL: `http://localhost:8080`

### Authentication

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|----------------|
| POST | `/auth/signup` | Register a user | Not required |
| POST | `/auth/signin` | Sign in and receive an access token and refresh cookie | Not required |
| POST | `/auth/refresh` | Receive a new access token | Refresh token cookie |
| POST | `/auth/logout` | Delete the refresh token and clear its cookie | Bearer access token |

### Job Applications

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|----------------|
| POST | `/application/addJob` | Create a job application | Bearer access token |
| GET | `/application/getJob` | List the current user's applications | Bearer access token |
| PUT | `/application/{jid}` | Update an application owned by the current user | Bearer access token |
| DELETE | `/application/{jid}` | Delete an application owned by the current user | Bearer access token |

Replace `{jid}` with the job application's ID, for example `/application/1`.

## API Request Examples

For requests with a JSON body, set this header:

```text
Content-Type: application/json
```

### Register a User

**POST** `/auth/signup`

```json
{
  "username": "ravi",
  "email": "ravi@example.com",
  "password": "example-password"
}
```

### Sign In

**POST** `/auth/signin`

```json
{
  "email": "ravi@example.com",
  "password": "example-password"
}
```

The response contains an access token, username, and email.
The refresh token is sent separately through an HttpOnly cookie.

### Create a Job Application

**POST** `/application/addJob`

Include your access token in the request header:

```text
Authorization: Bearer <access-token>
```

Request body:

```json
{
  "company_name": "Example Company",
  "role": "Java Backend Developer",
  "status": "APPLIED",
  "notes": "Applied through the company careers page."
}
```

Supported status values: `APPLIED`, `INTERVIEW`, `OFFER`, and `REJECTED`.

### Update a Job Application

**PUT** `/application/1`

Include the Bearer access token and send all editable fields:

```json
{
  "company_name": "Example Company",
  "role": "Java Backend Developer",
  "status": "INTERVIEW",
  "notes": "Technical interview scheduled."
}
```

Replace `1` with the application's actual ID.

### List Job Applications

**GET** `/application/getJob`

Include the Bearer access token. No request body is needed.

### Delete a Job Application

**DELETE** `/application/1`

Include the Bearer access token. No request body is needed.

### Refresh the Access Token

**POST** `/auth/refresh`

No request body is needed. Send the `refreshToken` cookie received during sign-in.

### Log Out

**POST** `/auth/logout`

Include the Bearer access token. No request body is needed.

Logout deletes the refresh token from the database and clears its cookie.
The existing access token remains valid until it expires.

## Authentication Flow

1. **Sign up:** Register with a username, email, and password.
   Passwords are hashed using BCrypt before being stored.

2. **Sign in:** Submit your email and password.
   The API returns a JWT access token and sets an HttpOnly refresh token cookie.
   Signing in replaces the user's previous refresh token.

3. **Access protected endpoints:** Include the access token in the
   `Authorization` header using the format `Bearer <access-token>`.

4. **Refresh:** Send the refresh token cookie to `/auth/refresh`.
   The API validates the stored refresh token and returns a new access token.
   The refresh token remains unchanged.

5. **Log out:** Call `/auth/logout` with the access token.
   The API deletes the user's refresh token and clears its cookie.
   The client should discard its access token.
   An already issued access token remains valid until it expires.

### Token Expiration

- Access token: 24 hours.
- Refresh token: 7 days.

These durations are configured through `jwt.expiration` and
`jwt.refresh.expiration` in `application.properties`, in milliseconds.

## Testing

### Manual API Testing

The API has been tested manually using Postman.

To test the main workflow:

1. Register a user through `/auth/signup`.
2. Sign in through `/auth/signin`.
3. Copy the returned access token.
4. In Postman, select **Authorization → Bearer Token** and enter the token
   when calling protected endpoints.
5. Create, list, update, and delete job applications.
6. Call `/auth/refresh` using the refresh token cookie saved during sign-in.
7. Call `/auth/logout` to delete the refresh token and clear its cookie.

### Automated Testing

The project currently includes a Spring Boot context-loading test.
Dedicated unit and integration tests are planned.

## Planned Improvements

- [x] Add health monitoring through Spring Boot Actuator.
- [ ] Add unit and integration tests using JUnit, Mockito, and Spring Boot testing tools.
- [ ] Integrate the backend with a React frontend.
- [ ] Containerize the application using Docker.
- [ ] Deploy the application to AWS.
- [ ] Add AI-powered features.

## Author

**Ravi Sankar Beeram**

- [GitHub](https://github.com/ravisankar-b)
- [LinkedIn](https://www.linkedin.com/in/ravisankar-beeram-7371191ba)
- Email: beeramravisankar@gmail.com