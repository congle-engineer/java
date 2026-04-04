# Code Review: Spring Boot 3.x Authentication Application

**Date:** 2026-03-31
**Reviewer:** code-reviewer agent
**Scope:** Security config, services, controller, model, repository, DTO (7 files, ~230 LOC)

---

## Overall Score: 7 / 10

Solid foundation — correct use of BCrypt, Spring Security form login, Bean Validation, and constructor injection throughout. The architecture is clean and conventional. Several security gaps and correctness issues prevent a higher score.

---

## Critical Issues

### 1. TOCTOU Race Condition in Registration (AuthController + UserService)
**Files:** `AuthController.java:34-38`, `UserService.java:28-34`

`usernameExists()` and `registerUser()` are two separate DB calls with no transaction or atomic check. Under concurrent requests, two threads can both pass the `usernameExists` check, then both call `save()`. The unique DB constraint will catch one, but the caller receives a generic 500 error instead of a controlled response.

Fix: Add `@Transactional` to `registerUser()`, or use `INSERT ... ON CONFLICT` / catch `DataIntegrityViolationException` in the controller to return a user-friendly duplicate error.

---

### 2. Username Enumeration via Error Message (CustomUserDetailsService)
**File:** `CustomUserDetailsService.java:23`

```java
throw new UsernameNotFoundException("User not found: " + username);
```

The exception message echoes the submitted username. Spring Security's `DaoAuthenticationProvider` hides this from the HTTP response by default (it maps to a generic "Bad credentials"), but the raw message leaks into application logs and any exception handler that forwards exception details. Remove the username interpolation — use a static message.

---

### 3. Password Minimum Length Too Weak (UserRegistrationDto)
**File:** `UserRegistrationDto.java:18`

```java
@Size(min = 6, max = 100, ...)
```

6-character minimum is well below NIST SP 800-63B guidance (minimum 8, recommended 12+). BCrypt still protects the hash, but weak passwords increase brute-force risk. Raise to minimum 8, ideally 12.

---

### 4. No CSRF Protection Explicitly Configured (SecurityConfig)
**File:** `SecurityConfig.java:44-64`

CSRF is enabled by default in Spring Security for form-based apps — this is correct. However, there is no explicit `.csrf(...)` configuration, which means it silently relies on Spring Boot's auto-configuration default. If a future developer adds `.csrf(csrf -> csrf.disable())` for API convenience, the protection disappears with no warning. Make the CSRF config explicit and add a comment documenting the intent.

---

## High Priority Issues

### 5. Role Stored as Plain String — No Enum Validation (User model + UserService)
**Files:** `User.java:22-23`, `UserService.java:33`

Role is a freeform `String` column with no constraint beyond `length = 20`. Nothing prevents invalid values like `"ADMIN"` (missing `ROLE_` prefix) or `"ROLE_SUPERUSER"`. Use an `@Enumerated(EnumType.STRING)` with a `Role` enum, or at minimum add a `@Pattern` / `@Column(columnDefinition = "VARCHAR(20) CHECK (...)")` constraint.

---

### 6. No `@Transactional(readOnly = true)` on Read Methods (UserService)
**File:** `UserService.java:20-25`

`usernameExists()` and `emailExists()` open full read-write transactions. Annotate them with `@Transactional(readOnly = true)` — this tells the JPA provider to skip dirty-checking and enables connection-pool optimizations.

---

### 7. `loadUserByUsername` Does Not Check Account Status (CustomUserDetailsService)
**File:** `CustomUserDetailsService.java:25-29`

The `User` constructor used sets `enabled=true`, `accountNonExpired=true`, etc. by default. The domain `User` entity has no `enabled` / `locked` / `credentialsExpired` fields, so there is currently no way to disable an account without deleting it. Add `enabled` and `locked` boolean fields to `User` and pass them through `UserDetails`.

---

### 8. Concurrent Test Pollution — No `@Transactional` or Cleanup in Tests (UserServiceTest)
**File:** `UserServiceTest.java`

Integration tests register users directly against the H2 in-memory DB with no rollback per test. If test execution order changes, `existinguser` / `emailtestuser` registrations from earlier tests bleed into later ones, causing `DataIntegrityViolationException`. Add `@Transactional` at the class level (Spring will roll back each test) or `@DirtiesContext`.

---

## Medium Priority Issues

### 9. `User` Constructor Does Not Set Role (User.java)
**File:** `User.java:27-31`

The 3-arg constructor sets `username`, `email`, `password` but not `role`. The field default `"ROLE_USER"` covers most paths, but if someone constructs `new User(u, e, p)` and persists immediately (bypassing `UserService`), the role relies on field-level initialization — which works but is fragile. The constructor should explicitly set role.

### 10. No `@Pattern` Constraint on Username (UserRegistrationDto)
**File:** `UserRegistrationDto.java:9-11`

Only length is validated. A username like `"<script>"` or `"admin'; DROP TABLE users--"` passes validation. While Thymeleaf auto-escapes output and JPA uses parameterized queries (preventing SQL injection), adding `@Pattern(regexp = "^[a-zA-Z0-9_.-]+$")` is defense-in-depth and prevents unexpected characters from causing display or downstream issues.

### 11. `findByEmail` in Repository Is Unused
**File:** `UserRepository.java:9`

`findByEmail(String email)` is declared but never called. `emailExists()` uses `existsByEmail`. Remove dead code (YAGNI).

### 12. Default Success URL Forces Redirect (SecurityConfig)
**File:** `SecurityConfig.java:54`

`.defaultSuccessUrl("/dashboard", true)` — the `true` forces redirect even if the user tried to access a specific protected URL first. This breaks the standard "redirect-after-login" UX. Remove the `true` (or set to `false`) so Spring Security uses the `SavedRequest` when available.

---

## Low Priority Issues

- `User.java` uses plain getters/setters — consider Lombok `@Data` / `@Entity` combo to reduce boilerplate if team adopts it.
- No `@Column(name = ...)` explicit DB column names; relies on hibernate naming strategy. Acceptable but worth being explicit for portability.
- `SecurityConfig.logoutUrl("/logout")` — POST is the secure default; make sure templates submit logout as a form POST, not a GET link (Spring Security 6 enforces this by default, but no template review was possible).
- `spring-boot-starter-parent 3.4.4` — latest stable as of review date. Good.

---

## Positive Observations

- BCrypt via `PasswordEncoder` bean — correct, no raw password storage.
- Constructor injection everywhere — no `@Autowired` field injection.
- `@Valid` + `BindingResult` pattern in controller — proper validation flow.
- Separate DTO from entity — raw User entity never bound directly from request.
- H2 for tests, MySQL for prod — clean test isolation strategy.
- `thymeleaf-extras-springsecurity6` included — correct version alignment with Spring Security 6.
- `spring-security-test` in test scope — correct setup for mock security context.
- Repository uses derived query methods — no raw JPQL/SQL injection surface.

---

## Recommended Actions (Priority Order)

1. Fix TOCTOU: wrap `registerUser` in `@Transactional` and catch `DataIntegrityViolationException` in controller.
2. Remove username from `UsernameNotFoundException` message.
3. Raise password minimum to 8 (or 12) characters.
4. Add `enabled` / `locked` fields to `User` entity and thread through `UserDetails`.
5. Replace freeform role string with enum + `@Enumerated`.
6. Add `@Transactional(readOnly = true)` to read-only service methods.
7. Add `@Transactional` or `@DirtiesContext` to integration tests.
8. Add `@Pattern` username constraint.
9. Remove `findByEmail` unused method.
10. Change `defaultSuccessUrl` second arg to `false`.
11. Make CSRF config explicit with explanatory comment.

---

## Metrics

| Metric | Value |
|---|---|
| Files reviewed | 7 source + 1 test + pom.xml |
| Approx LOC | ~300 |
| Critical issues | 4 |
| High issues | 4 |
| Medium issues | 4 |
| Low issues | 4 |
| Test coverage | Partial (service layer only; controller tests exist but not reviewed) |
| Known CVEs in deps | None (Spring Boot 3.4.4 current) |

---

## Unresolved Questions

1. Are Thymeleaf templates reviewed for XSS — specifically, are any model attributes rendered with `th:utext` (unescaped) instead of `th:text`?
2. Is there a `/dashboard` authorization check beyond "authenticated"? No role-based access currently guards any route.
3. Is session fixation protection configured? Spring Security enables it by default but the config does not explicitly set `.sessionManagement(...)` — worth confirming the default is intentional.
4. Are there plans for account lockout / rate limiting on `/login`? Spring Security alone does not throttle login attempts.
5. How is the MySQL `datasource.password` supplied in production — env var, Vault, or hardcoded in `application.properties`?
