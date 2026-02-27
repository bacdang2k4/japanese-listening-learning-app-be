
------------------------------------------------------------------------

## 1. CONTEXT & TECH STACK

-   Spring Boot 3.x (Java 25)
-   PostgreSQL (schema already defined -- DO NOT MODIFY)
-   Architecture: Controller → Service → Repository → Entity
-   Libraries:
    -   Spring Data JPA
    -   Lombok
    -   Jakarta Validation
    -   Springdoc OpenAPI

------------------------------------------------------------------------

## 2. NON-NEGOTIABLE RULES

### Database Rules

-   database-schema(postgresql).md is the SINGLE source of truth
-   DO NOT modify schema
-   DO NOT create new tables
-   DO NOT rename columns
-   Entities MUST map EXACTLY to schema

### STRICT DTO-ONLY POLICY

Controller MUST NEVER return Entity objects

All responses MUST return:

ResponseEntity\<ApiResponse`<T>`{=html}\>

Rules:

-   Request → DTO
-   Response → DTO
-   Mapping must be manual inside Service layer
-   No ModelMapper
-   Entity is persistence-only

------------------------------------------------------------------------

## 3. STANDARD RESPONSE WRAPPER

All responses MUST follow structure:

{ success: true, message: "Human readable message", data: object or list
or null, timestamp: ISO-8601 Instant.now() }

Raw response without wrapper is NOT allowed.

------------------------------------------------------------------------

## 4. PAGINATION RULES (MANDATORY FOR LIST ENDPOINTS)

All list endpoints MUST support pagination unless explicitly stated
otherwise.

### Query Parameters

?page=0&size=10

Rules:

-   page starts from 0
-   default size = 10
-   maximum size = 50
-   if size \> 50 → throw BusinessException

### Pagination Response Structure

List endpoints MUST return custom PaginationResponse`<T>`{=html} instead
of Spring Page.

Structure:

{ content: \[...\], page: 0, size: 10, totalElements: 100, totalPages:
10, last: false }

### Required Pagination DTO

PaginationResponse`<T>`{=html} - List`<T>`{=html} content - int page -
int size - long totalElements - int totalPages - boolean last

Rules:

-   MUST NOT expose Spring Page directly
-   MUST NOT expose Pageable
-   Mapping from Page`<Entity>`{=html} must be done manually in Service
    layer

------------------------------------------------------------------------

## 5. GLOBAL EXCEPTION HANDLING

Create:

@RestControllerAdvice GlobalExceptionHandler

Handle:

1.  Validation Error
    -   MethodArgumentNotValidException
    -   HTTP 400
2.  Resource Not Found
    -   ResourceNotFoundException
    -   HTTP 404
3.  Business Rule Error
    -   BusinessException
    -   HTTP 400
4.  Unexpected Error
    -   HTTP 500

------------------------------------------------------------------------

## 6. FEATURE SCOPE -- TEST FLOW

Lifecycle:

Topic → Test → Start → Submit → Result → History

------------------------------------------------------------------------

## 7. REQUIRED APIS

### 1. Get Tests By Topic (PAGINATED)

GET /api/topics/{topicId}/tests?page=0&size=10

-   Validate Topic exists
-   Return ONLY tests where status = Published
-   Return PaginationResponse`<TestSummaryResponse>`{=html}

------------------------------------------------------------------------

### 2. Start Test

POST /api/tests/{testId}/start

Request:

{ profileId: 1, mode: "Practice" }

-   Validate Test exists
-   Validate Test status = Published
-   Validate Profile exists
-   Create TestResult (In Progress)
-   Return StartTestResponse
-   MUST NOT return isCorrect

------------------------------------------------------------------------

### 3. Submit Test

POST /api/test-results/{resultId}/submit

Request:

{ answers: \[ { questionId: 1, selectedAnswerId: 3 } \] }

-   Validate TestResult exists
-   Validate status = In Progress
-   Save LearnerAnswer
-   Calculate score
-   Update TestResult (Completed)
-   Return SubmitTestResponse

------------------------------------------------------------------------

### 4. Get Test Result Detail

GET /api/test-results/{resultId}

Return:

-   testName
-   score
-   isPassed
-   question results
-   selectedAnswer
-   correctAnswer
-   isCorrect

------------------------------------------------------------------------

### 5. Get Test History By Profile (PAGINATED)

GET /api/profiles/{profileId}/test-results?page=0&size=10

Return:

PaginationResponse`<TestHistoryResponse>`{=html}

------------------------------------------------------------------------

## 8. ENTITY RULES

-   All ManyToOne must use FetchType.LAZY
-   No DTO logic inside Entity
-   No business logic inside Entity
-   No JSON exposure of relationships

------------------------------------------------------------------------

## 9. SERVICE LAYER RULES

Each service must:

-   Use @Service
-   Use @Transactional
-   Validate all foreign keys
-   Throw specific exceptions only
-   Perform manual DTO mapping
-   Contain all business logic

------------------------------------------------------------------------

## 10. CONTROLLER RULES

Each controller must:

-   Use @RestController
-   Use @RequestMapping("/api")
-   Use @Tag(name = "Test Management")
-   Use @Operation(summary = "...")
-   Return ResponseEntity\<ApiResponse`<T>`{=html}\>

------------------------------------------------------------------------

## 11. FORBIDDEN

-   No authentication
-   No schema modification
-   No returning Entity
-   No exposing DB internal fields
-   No auto-mapping libraries
