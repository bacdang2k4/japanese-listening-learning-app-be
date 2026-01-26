Context:
- Existing Spring Boot 3.x project
- Java 17+
- MySQL database already connected and running
- Layered architecture (controller, service, repository, model/entity, dto)
- Swagger (springdoc-openapi) already configured and accessible
- Database schema is fully described in the file: database-schema.md

Important rules:
- Use ONLY tables and relationships defined in database-schema.md
- Do NOT modify or redesign the database schema
- Do NOT create new tables
- Map entities exactly to existing tables
- Assume database-schema.md is the single source of truth

Task:
Create a simple read-only REST API endpoint to verify Swagger UI and database connectivity.

API requirements:
- Endpoint: GET /api/test
- Purpose: Swagger testing and DB connection verification
- Fetch sample data from ONE simple table defined in database-schema.md
  (prefer Levels or Topics if available)
- No pagination, no filtering, no business logic
- Return JSON response

Architecture requirements:
- Follow layered architecture strictly:
  - Entity (model)
  - Repository (Spring Data JPA)
  - Service interface
  - Service implementation
  - Controller
- Keep code minimal and clean
- Use standard Spring annotations

Swagger requirements:
- Add @Tag and @Operation annotations
- API must appear and be callable in Swagger UI

Constraints:
- Do NOT add authentication or security
- Do NOT add DTO mapping unless absolutely necessary
- Do NOT modify existing application configuration

Output:
- Entity code used
- Repository interface
- Service interface
- Service implementation
- Controller code
- Swagger URL to test the API
