Context:
- Existing Spring Boot 3.x project
- Java 17+
- MySQL database already connected and running
- Layered architecture:
  controller → service → repository → entity
- Swagger (springdoc-openapi) already configured and working
- Database schema is fully defined in: database-schema.md

Important rules:
- database-schema.md is the SINGLE source of truth
- Use ONLY existing tables and relationships
- Do NOT modify database schema
- Do NOT create new tables
- Map entities exactly as defined
- Follow naming exactly as in the schema

Scope of this task:
- Implement Topic management APIs
- This task is SEPARATE from the test endpoint (/api/test)
- Do NOT modify or reuse the /api/test API

Functional requirements:
- Topic belongs to ONE Level (many Topics → one Level)
- Manage Topics within a specific Level

Required APIs:
1. Create Topic
   - POST /api/levels/{levelId}/topics
2. Get all Topics of a Level
   - GET /api/levels/{levelId}/topics
3. Get Topic by ID
   - GET /api/topics/{topicId}
4. Update Topic
   - PUT /api/topics/{topicId}
5. Delete Topic
   - DELETE /api/topics/{topicId}

Rules for implementation:
- Validate Level existence when creating Topic
- No advanced business logic
- No pagination, no filtering
- Use JPA relationships correctly (@ManyToOne)
- Use transactional service where appropriate
- Use minimal DTOs ONLY if needed to avoid infinite JSON recursion
- Prefer returning Entity directly if safe

Architecture requirements:
- Entity (Topic, Level if needed)
- Repository (TopicRepository, LevelRepository if needed)
- Service interface
- Service implementation
- Controller
- Clear package separation
- Clean, readable code

Swagger requirements:
- Add @Tag for Topic APIs
- Add @Operation for each endpoint
- APIs must appear clearly in Swagger UI
- Provide example request body where applicable

Constraints:
- No authentication / authorization
- No security filters
- No changes to application.yml or properties
- No global exception handler unless required

Output format:
- Entity code (only what is required)
- Repository interfaces
- Service interface
- Service implementation
- Controller code
- Example JSON request/response
- Swagger URLs to test each API
