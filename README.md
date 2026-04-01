# Graph-Based Attack Path Visualizer

## Project Description

This project models potential cyber attack paths within a small business IT environment using a graph-based approach. Systems and services are represented as nodes, and attacker actions are represented as weighted edges.

The system computes the shortest attack path to high-value assets using Dijkstra's algorithm. It also demonstrates how security mitigations increase attack difficulty by dynamically adjusting edge weights.

The application includes user authentication and user-specific infrastructure modeling. Each authenticated user maintains their own BusinessProfile configuration, ensuring that graph generation, mitigation effects, and attack path computation are isolated per user.

This tool is designed to be an educational cybersecurity visualization platform.

### BusinessProfile-Based Dynamic Topology (User-Specific)

Each authenticated user maintains their own BusinessProfile, which defines the structure of their simulated business infrastructure.

On first use, users complete a questionnaire that specifies optional components such as VPN access, File Server usage, SaaS integrations, Public Web App exposure, and Identity Provider usage.

This configuration is stored in the database and is uniquely associated with the authenticated user.

For every request, the backend dynamically reconstructs the graph based on the user's BusinessProfile. Nodes and edges are conditionally included or excluded, and Dijkstra’s algorithm executes on the filtered graph instance.

As a result, each user interacts with an isolated attack surface tailored to their selected infrastructure.

## Authentication & Security

The system uses Spring Security with session-based authentication.

- Users register and log in using email and password
- Passwords are securely stored using BCrypt hashing
- Upon successful login, a session (JSESSIONID) is created
- All protected API endpoints require authentication
- The authenticated user is retrieved from the SecurityContext for each request

This enables:

- User-specific BusinessProfile storage
- Isolated graph generation per user
- Secure access to backend endpoints

## Tech Stack

- **Backend Framework:** Spring Boot
- **Language:** Java
- **Build Tool:** Maven
- **Graph Library:** JGraphT
- **Algorithm:** Dijkstra's Shortest Path
- **Database:** PostgreSQL  
- **ORM:** Spring Data JPA / Hibernate
- **AI Integration:** OpenAI API
- **Frontend Framework:** Vite
- **Styling:** Tailwind CSS
- **Graph Visualization:** Cytoscape.js
- **Package Manager (Frontend):** npm
- **Runtime Environment:** Node.js (required to run Vite)
- **Version Control:** Git
- **Repository Hosting / Collaboration:** GitHub
- **API Architecture:** RESTful API
- **Data Format:** JSON
- **Security:** Spring Security (Session-Based Authentication)
- **Password Hashing:** BCrypt


## Installation & Setup

### Database Setup (PostgreSQL Required)

This application requires **PostgreSQL** to run. The backend will not start without a configured database.

---

### 1. Install PostgreSQL

Download and install PostgreSQL:

https://www.postgresql.org/download/

During installation:

- Remember your PostgreSQL username and password
- Ensure the default port is `5432`

### 2. Create the Required Database

After installation, create a database named:

`attackgraph`

### 3. Configure application.properties

Open:

`visualizer/src/main/resources/application.properties`

Ensure the following properties are configured correctly:

`spring.datasource.url=jdbc:postgresql://localhost:5432/attackgraph`

`spring.datasource.username=YOUR_USERNAME`

`spring.datasource.password=YOUR_PASSWORD`

`spring.datasource.driver-class-name=org.postgresql.Driver`

`spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect`

`spring.jpa.hibernate.ddl-auto=update`

`spring.jpa.show-sql=true`

`spring.jpa.properties.hibernate.format_sql=true`

Replace YOUR_USERNAME and YOUR_PASSWORD with your PostgreSQL credentials

### 4. OpenAI Configuration

Create or update:

`application-local.properties`

Add:

`openai.api.key=YOUR_API_KEY`

This file should NOT be committed to version control.

### 5. First-Time Database Seeding

If running the project for the first time or after dropping the database:

Temporarily change:

`spring.jpa.hibernate.ddl-auto=create`

Run the backend once to allow Hibernate and DataInitializer to create and seed tables

After successful startup, change it back to:

`spring.jpa.hibernate.ddl-auto=update`

---

### Backend Setup

1. Navigate to the backend directory:

    `cd visualizer`

2. Run the Spring Boot application:

    `mvn spring-boot:run`

3. Backend runs at:

    http://localhost:8080

#### Authentication Endpoints

- `POST /api/auth/register`  
    Registers a new user account

- `POST /api/auth/login`  
    Authenticates a user and creates a session

- `POST /api/auth/logout`  
    Logs out the current user and invalidates the session

- `GET /api/auth/me`  
    Returns the currently authenticated user

---

#### Application Endpoints (Authenticated)

- `GET /api/health`  
    Returns a confirmation message indicating that the API is running

- `GET /api/profile`  
    Returns the authenticated user's BusinessProfile configuration

- `POST /api/profile`  
    Creates or updates the authenticated user's BusinessProfile

- `GET /api/graph`  
    Returns the attack graph filtered by the authenticated user's BusinessProfile

- `GET /api/path?source={sourceId}&target={targetId}&mitigations={ids}`  
    Computes and returns the shortest attack path for the authenticated user, optionally applying selected mitigations

- `GET /api/mitigations`  
    Returns all available mitigation controls for the authenticated user's graph

- `POST /api/ai/attack-summary`  
    Generates an AI-based summary of the computed attack path, including risk analysis, weakest points, and mitigation recommendations

---

### Frontend Setup

1. Navigate to the frontend directory:

    `cd Website`

2. Install dependencies:

    `npm install`

3. Start development server:

    `npm run dev`

4. Frontend runs at:

    http://localhost:5173

## User Instructions

1. Open the application in your browser:

    http://localhost:5173

2. Create an account or log in using your credentials.

3. After logging in, the system will check for an existing BusinessProfile.

4. If no profile exists, complete the BusinessProfile questionnaire by selecting infrastructure components such as:

    - VPN access
    - File Server usage
    - SaaS applications
    - Public web application
    - Identity Provider

5. Submit the questionnaire. The system will save this configuration and dynamically construct the network graph based on your selections.

6. The graph visualization will render your personalized business network and available mitigation controls.

7. Click "Compute Path" to calculate the most likely attack path.

8. Enable or disable mitigation controls using the sidebar. Each mitigation increases the difficulty of specific attack steps.

9. Recompute the attack path to observe how security controls impact attack feasibility.

10. View the AI-generated attack path summary to understand key risks, weak points, and recommended mitigations based on your configuration.

## AI Attack Path Summary

After computing an attack path, the system can generate an AI-powered summary that:

- Explains the attack path step-by-step
- Identifies the weakest points in the system
- Highlights overall risk level
- Suggests relevant mitigations

This feature is designed to improve usability for non-technical users by translating graph-based results into clear, actionable insights.

## Contributor Guidelines

- Use feature branches
- Do not push directly to `main` except for minor documentation updates
- Changes should go through a Pull Request before merging
- Commit messages should be clear and descriptive
- At least one teammate should review and approve Pull Requests
- Test your code locally before pushing changes