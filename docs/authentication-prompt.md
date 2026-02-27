You are a Senior Backend Engineer.

Your task is to IMPLEMENT FULL AUTHENTICATION & AUTHORIZATION
for an existing Spring Boot 3.x (Java 25) backend project.

=======================================================================
1. CONTEXT & TECH STACK
=======================================================================
- Spring Boot 3.x
- Java 25
- PostgreSQL
- Architecture: Controller → Service → Repository → Entity
- Libraries already used:
  - Spring Data JPA
  - Lombok
  - Jakarta Validation
  - Springdoc OpenAPI

=======================================================================
2. DATABASE RULES (ABSOLUTE — NON NEGOTIABLE)
=======================================================================
- database-schema(postgresql).md is the SINGLE source of truth
- DO NOT modify schema
- DO NOT add tables
- DO NOT rename columns
- DO NOT change enum values
- Entities MUST map EXACTLY to existing tables:
  - Admin
  - Learner
  - Profile

=======================================================================
3. AUTHENTICATION & AUTHORIZATION REQUIREMENTS
=======================================================================

### Authentication Type
- JWT-based authentication
- Stateless
- Access token only (NO refresh token)

### Roles
- ADMIN
- LEARNER

### Role Mapping
- Admin table → ROLE_ADMIN
- Learner table → ROLE_LEARNER

=======================================================================
4. REQUIRED DEPENDENCIES (MUST ADD)
=======================================================================
Add necessary dependencies ONLY:

- spring-boot-starter-security
- spring-boot-starter-oauth2-resource-server
- jjwt-api
- jjwt-impl
- jjwt-jackson

Use latest stable versions compatible with Spring Boot 3.x.

=======================================================================
5. SECURITY CONFIGURATION
=======================================================================
Create SecurityConfig:

- Disable CSRF
- Stateless session
- JWT authentication filter
- Password encoder: BCryptPasswordEncoder

Authorization rules:
- /api/auth/** → permitAll
- /api/admin/auth/** → permitAll
- /swagger-ui/** → permitAll
- /v3/api-docs/** → permitAll
- /api/admin/** → ROLE_ADMIN
- /api/** → authenticated

=======================================================================
6. JWT RULES
=======================================================================
JWT payload MUST contain:
- subject (username)
- role
- userId

Token expiration:
- 24 hours

JWT utility must:
- generate token
- validate token
- extract username & role

=======================================================================
7. AUTH FEATURES TO IMPLEMENT
=======================================================================

### 1. Learner Registration
POST /api/auth/register

Request DTO:
{
  username: string,
  password: string,
  email: string
}

Rules:
- username unique
- email unique
- password encrypted
- After Learner creation → auto-create Profile
- Return DTO WITHOUT password

-----------------------------------------------------------------------

### 2. Learner Login
POST /api/auth/login

Request DTO:
{
  username: string,
  password: string
}

Return:
{
  accessToken,
  tokenType: "Bearer",
  learnerId,
  profileId,
  username,
  role
}

-----------------------------------------------------------------------

### 3. Admin Login
POST /api/admin/auth/login

Request DTO:
{
  username: string,
  password: string
}

Return:
{
  accessToken,
  tokenType: "Bearer",
  adminId,
  username,
  role
}

=======================================================================
8. DTO RULES (STRICT)
=======================================================================
- Controllers MUST NEVER return Entity
- Controllers MUST return:
  ResponseEntity<ApiResponse<T>>
- Mapping MUST be manual in Service layer
- NO ModelMapper
- Entity = persistence-only

=======================================================================
9. STANDARD RESPONSE WRAPPER (MANDATORY)
=======================================================================
{
  success: true,
  message: "Human readable message",
  data: object or null,
  timestamp: ISO-8601 Instant.now()
}

=======================================================================
10. GLOBAL EXCEPTION HANDLING
=======================================================================
Create GlobalExceptionHandler:

Handle:
1. Validation errors → HTTP 400
2. ResourceNotFoundException → HTTP 404
3. BusinessException → HTTP 400
4. AuthenticationException → HTTP 401
5. AccessDeniedException → HTTP 403
6. UnexpectedException → HTTP 500

=======================================================================
11. SWAGGER / OPENAPI
=======================================================================
Configure Swagger to:
- Support JWT Authorization
- Add "Authorize" button
- Use Bearer token
- Apply security scheme globally
- Exclude auth endpoints from security requirement

=======================================================================
12. SERVICE LAYER RULES
=======================================================================
- @Service
- @Transactional
- All validation logic in Service
- Throw specific exceptions only
- No security logic in Controller

=======================================================================
13. FORBIDDEN
=======================================================================
- No schema modification
- No returning Entity
- No exposing password
- No auto-mapping libraries
- No session-based auth
- No refresh token

=======================================================================
14. OUTPUT EXPECTATION
=======================================================================
Generate:
- SecurityConfig
- JwtUtil / JwtService
- JwtAuthenticationFilter
- Controllers
- Services
- Repositories
- DTOs
- Exception classes
- Swagger config

Code must compile, follow clean architecture, and integrate
seamlessly with existing Test Flow APIs.