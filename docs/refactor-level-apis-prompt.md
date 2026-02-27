Refactor the Level APIs to follow these strict rules:

1. Split Public and Admin APIs clearly.

PUBLIC:
- GET /api/levels?page=0&size=10
- GET /api/levels/{levelId}

ADMIN:
- POST   /api/admin/levels
- PUT    /api/admin/levels/{levelId}
- DELETE /api/admin/levels/{levelId}

2. Remove this endpoint completely:
- GET /api/admins/{adminId}/levels

3. Follow STRICT DTO-ONLY policy:
- Controllers must return ResponseEntity<ApiResponse<T>>
- No Entity exposure
- Manual mapping in Service layer
- No ModelMapper

4. All list endpoints must use:
ApiResponse<PaginationResponse<LevelResponse>>

5. Do NOT modify database schema.

6. Keep existing business rules and validation.

Output:
- Updated Controller
- Service changes (if needed)
- DTOs (if needed)
- Short explanation of what was refactored.