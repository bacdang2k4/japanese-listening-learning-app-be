# PROMPT: SPRING BOOT 3 LEVEL MANAGEMENT API  
## (STRICT DTO-ONLY + SPECIFIC EXCEPTION HANDLING + RESPONSE WRAPPER)

---

# 1. CONTEXT & TECH STACK

- Framework: Spring Boot 3.x (Java 25)
- Database: PostgreSQL (Already configured & running)
- Architecture: Layered Architecture  
  Controller → Service → Repository → Entity
- Libraries:
  - Spring Data JPA
  - Lombok
  - Jakarta Validation
  - Springdoc-openapi (Swagger)

---

# 2. NON-NEGOTIABLE RULES

## Database Rules
- `database-schema.md` is the SINGLE source of truth.
- DO NOT modify database schema.
- DO NOT create new tables.
- DO NOT rename columns.
- Map entities EXACTLY as defined in schema.

---

## DTO-ONLY POLICY (STRICT)

🚫 The Controller MUST NEVER return Entity objects.

✅ ALL responses MUST use DTOs wrapped inside ApiResponse<T>.

- Request → LevelRequest
- Response → LevelResponse
- Entity classes are strictly for persistence layer only.
- Entity must NEVER be serialized directly to JSON.

If necessary, use manual mapping inside Service layer.

---

# 3. DATA FLOW RULES

Controller:
- Accept DTO
- Return ResponseEntity<ApiResponse<T>>
- Use @Valid for validation

Service:
- Handle business logic
- Validate entity existence
- Perform DTO ↔ Entity mapping
- Throw specific exceptions only (no generic RuntimeException)

Repository:
- Only database access
- No business logic

Entity:
- Pure JPA mapping only
- No API logic
- No DTO conversion logic inside entity

---

# 4. STANDARD RESPONSE WRAPPER

All responses (success & error) MUST follow this structure:

```json
{
  "success": true,
  "message": "Human-readable message",
  "data": {},
  "timestamp": "2026-02-25T15:00:00Z"
}
```

Field rules:

- success → boolean
- message → clear readable description
- data → DTO, List<DTO>, or null
- timestamp → ISO-8601 format (Instant.now())

Raw responses without wrapper are NOT allowed.

---

# 5. SPECIFIC EXCEPTION HANDLING (MANDATORY)

Implement @RestControllerAdvice named GlobalExceptionHandler.

You MUST handle the following:

---

## 1️⃣ Validation Error

Exception: MethodArgumentNotValidException  
HTTP Status: 400

Response message must contain ALL field errors in this format:

"levelName: must not be blank, adminId: must not be null"

Return inside ApiResponse with:
- success = false
- data = null

---

## 2️⃣ Resource Not Found

Custom Exception:
ResourceNotFoundException

Used when:
- Level ID does not exist
- Admin ID does not exist

HTTP Status: 404

Example message:
"Level not found with id: 5"

---

## 3️⃣ Unexpected Error

Catch generic Exception.

HTTP Status: 500

Message:
"Internal server error"

---

# 6. ENTITY REQUIREMENTS

Level:
- @ManyToOne(fetch = FetchType.LAZY)
- JoinColumn(name = "admin_id")
- Must belong to one Admin

Admin:
- Only fields required for mapping
- id
- name

No bidirectional serialization exposure allowed.

---

# 7. DTO SPECIFICATIONS

## LevelRequest
- String levelName
- Long adminId

Validation:
- @NotBlank → levelName
- @NotNull → adminId

---

## LevelResponse
- Long id
- String levelName
- Long adminId
- String adminName
- LocalDateTime createdAt

Must be fully populated in Service layer.

---

# 8. REQUIRED APIS

### 1️⃣ Create Level
POST /api/levels

- Validate admin exists
- Return created LevelResponse

---

### 2️⃣ Get All Levels
GET /api/levels

Return List<LevelResponse>

---

### 3️⃣ Get Level By ID
GET /api/levels/{levelId}

---

### 4️⃣ Update Level
PUT /api/levels/{levelId}

- Validate Level exists
- Validate Admin exists if changed

---

### 5️⃣ Delete Level
DELETE /api/levels/{levelId}

---

### 6️⃣ Get Levels By Admin
GET /api/admins/{adminId}/levels

- Validate Admin exists

---

# 9. SERVICE LAYER REQUIREMENTS

- Annotate with @Service
- Use @Transactional
- Throw ResourceNotFoundException for invalid IDs
- Perform manual mapping:
  - Entity → LevelResponse
  - LevelRequest → Entity

No model mapper libraries.

---

# 10. CONTROLLER REQUIREMENTS

- @RestController
- @RequestMapping("/api")
- @Tag(name = "Level Management")
- @Operation(summary = "...") for every endpoint
- Return ResponseEntity<ApiResponse<T>>

---

# 11. REQUIRED OUTPUT FILES

Provide full source code for:

common/
- ApiResponse.java
- GlobalExceptionHandler.java
- ResourceNotFoundException.java

dto/
- LevelRequest.java
- LevelResponse.java

entity/
- Level.java
- Admin.java

repository/
- LevelRepository.java
- AdminRepository.java

service/
- LevelService.java
- LevelServiceImpl.java

controller/
- LevelController.java

---

# 12. REQUIRED EXAMPLES

Provide:

1️⃣ Example successful Create response  
2️⃣ Example validation error response  
3️⃣ Example resource not found response  

All must follow ApiResponse format.

---

# IMPORTANT

- No authentication
- No security config
- No pagination
- No filtering
- No schema modification
- No returning Entity under any circumstance
- Clean, readable, production-ready code