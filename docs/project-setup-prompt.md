Context:
- Existing Spring Boot Web API project
- Java 25
- Spring Boot 3.x
- MySQL database
- Controller-based (REST API, not reactive)

Task:
Prepare the project for a layered architecture.

Requirements:
- Create base packages:
  - controller
  - service
  - service.impl
  - repository
  - model (entity)
  - dto
  - config
- Add Spring Data JPA and MySQL dependencies (if needed)
- Create base Entity class (if needed)
- Create JPA Repository interfaces (empty for now)
- Configure MySQL datasource in application.properties
- Enable JPA (Hibernate) configuration
- Add Swagger (OpenAPI) dependency and basic configuration
- Do NOT implement business logic yet
- Do NOT create actual API endpoints yet

Output:
- Project package structure
- application.properties MySQL configuration snippet
- pom.xml dependency snippet
- Swagger access URL
