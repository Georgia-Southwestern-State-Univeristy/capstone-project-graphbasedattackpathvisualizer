# Graph-Based Attack Path Visualizer

## Project Description

This project models potential cyber attack paths within a small business IT environment using a graph-based approach. Systems and services are represented as nodes, and attacker actions are represented as weighted edges.

The system computes the shortest attack path to high-value assets using Dijkstra's algorithm. It also demonstrates how security mitigations increase attack difficulty by dynamically adjusting edge weights.

This tool is designed to be an educational cybersecurity visualization platform.

### BusinessProfile-Based Dynamic Topology

Before the graph is rendered, the system requires a BusinessProfile configuration. On first load, users must complete a questionnaire that defines optional infrastructure components such as VPN access, File Server usage, SaaS integrations, Public Web App exposure, and Identity Provider usage.

This configuration is persisted in the database and used to dynamically filter the graph structure. Nodes and edges are conditionally removed based on the saved BusinessProfile, and Dijkstra’s algorithm executes on the filtered graph instance. As a result, business structure directly influences the computed attack path.

## Tech Stack

- **Backend Framework:** Spring Boot
- **Language:** Java
- **Build Tool:** Maven
- **Graph Library:** JGraphT
- **Algorithm:** Dijkstra's Shortest Path
- **Database:** PostgreSQL  
- **ORM:** Spring Data JPA / Hibernate
- **Frontend Framework:** Vite
- **Styling:** Tailwind CSS
- **Graph Visualization:** Cytoscape.js
- **Package Manager (Frontend):** npm
- **Runtime Environment:** Node.js (required to run Vite)
- **Version Control:** Git
- **Repository Hosting / Collaboration:** GitHub
- **API Architecture:** RESTful API
- **Data Format:** JSON


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

### 4. First-Time Database Seeding

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

#### Test Endpoints

- `GET /api/health`
    Returns a confirmation message indicating that the API is running
- `GET /api/graph`
    Returns the full attack graph structure (nodes and edges) in JSON format
- `GET /api/path?source={sourceId}&target={targetId}`
    Computes and returns the shortest attack path between the specified source and target nodes, 
    including ordered nodes, ordered edges, and total path cost
- `GET /api/mitigations`
    Returns all available mitigation controls and their IDs
- `GET /api/profile`
    Returns the most recently saved BusinessProfile configuration. If no profile exists, the frontend will display the BusinessProfile questionnaire before rendering the graph.
- `POST /api/profile`
    Saves a new BusinessProfile configuration. This configuration is used to dynamically filter the graph structure before path computation.

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

    `http://localhost:5173`

2. On first load, the system will display a BusinessProfile questionnaire.

3. Select the infrastructure components used by the business, such as:

    - VPN access
    - File Server usage
    - SaaS applications
    - Public web application
    - Identity Provider

4. Submit the questionnaire. The system will save this configuration and dynamically construct the network graph based on the selected infrastructure.

5. The graph visualization will render the business network and available mitigation controls.

6. Click Compute Attack Path to calculate the most likely attack path through the system.

7. Enable or disable mitigation controls using the sidebar. Each mitigation increases the difficulty of certain attack edges.

8. Recompute the attack path to see how the attack path changes when mitigations are applied.


## Contributor Guidelines

- Use feature branches
- Do not push directly to `main` except for minor documentation updates
- Changes should go through a Pull Request before merging
- Commit messages should be clear and descriptive
- At least one teammate should review and approve Pull Requests
- Test your code locally before pushing changes