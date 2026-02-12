# Graph-Based Attack Path Visualizer

## Project Description

This project models potential cyber attack paths within a small business IT environment using a graph-based approach. Systems and services are represented as nodes, and attacker actions are represented as weighted edges.

The system computes the shortest attack path to high-value assets using Dijkstra's algorithm. It also demonstrates how security mitigations increase attack difficulty by dynamically adjusting edge weights.

This tool is designed to be an educational cybersecurity visualization platform.


## Tech Stack

- **Backend Framework:** Spring Boot
- **Language:** Java
- **Build Tool:** Maven
- **Graph Library:** JGraphT
- **Algorithm:** Dijkstra's Shortest Path
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


### Frontend Setup

1. Navigate to the frontend directory:

    `cd Website`

2. Install dependencies:

    `npm install`

3. Start development server:

    `npm run dev`

4. Frontend runs at:

    http://localhost:5173


## Contributor Guidelines

- Use feature branches
- Do not push directly to `main`
- Changes should go through a Pull Request before merging
- Commit messages should be clear and descriptive
- At least one teammate should review and approve Pull Requests
- Test your code locally before pushing changes