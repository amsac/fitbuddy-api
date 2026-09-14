# Authentication V1

The backend implements register, login, current-user lookup, JWT protection, and workout ownership checks. Frontend code is not present in this repository; the client screens, token storage, and navigation from the requirements still need implementation in the client repository.

## Run

Set `JWT_SECRET` to a Base64-encoded random key containing at least 32 decoded bytes. Generate one with `openssl rand -base64 32`, then set it in your shell, IDE run configuration, or Render environment. Keep the same key across application instances and restarts. Changing it invalidates existing tokens. There is no fallback secret.

Optional environment variables:

- `JWT_EXPIRATION_MS`: defaults to `86400000` (24 hours).
- `CORS_ALLOWED_ORIGINS`: comma-separated browser origins, such as `http://localhost:5173,http://localhost:3000`. Defaults to `*` to preserve existing client support. Bearer headers are supported; cookie credentials are not used.

Database settings remain in `application.properties`. Start with `./mvnw spring-boot:run` after configuring the database and JWT secret.

## API

Only `POST /auth/register` and `POST /auth/login` are public. Browser CORS preflights are handled by Spring Security.

Register with:

```json
{"name":"Akhil","email":"akhil@example.com","password":"StrongPassword123","role":"STUDENT"}
```

`TRAINER` is also allowed. Passwords require at least 8 characters and must fit BCrypt's 72 UTF-8 byte limit. Registration returns HTTP 201, login HTTP 200:

```json
{"accessToken":"...","tokenType":"Bearer","expiresIn":86400,"user":{"id":7,"name":"Akhil","email":"akhil@example.com","role":"STUDENT"}}
```

Login accepts `email` and `password`. Email is trimmed and lowercased. Duplicate registration returns 409; invalid input returns 400; incorrect credentials return a generic 401.

Send `Authorization: Bearer <accessToken>` on subsequent requests. `GET /auth/me` returns the current user without password fields. Missing, malformed, expired, incorrectly signed tokens and tokens for inactive or removed accounts cannot access protected APIs.

Changes to existing requests:

- `POST /templates`: omit `createdBy`. The JWT user becomes the creator. Both roles may create templates. Exercise entries accept numeric `exerciseId`; existing `externalId` lookup remains supported. When both are supplied, `exerciseId` takes precedence.
- `POST /sessions/start`: send only `templateId`; the JWT user owns the session.
- `GET /sessions/me/completed`: replaces `/sessions/users/{userId}/completed` and returns only the caller's history with the existing response shape.
- Session detail, completion, and set logging return 404 for another user's resources.

Clients should store the token centrally, attach it through their API client, and call `/auth/me` to restore identity at startup. On 401, clear authentication and show login. Logout only removes the client token. There are no refresh tokens or password reset endpoints in V1.

## Existing users

No hosted database was modified during implementation. The entity's password column remains nullable during migration so existing workout owners can be retained. Newly registered accounts always receive BCrypt hashes and are active.

Before enabling login for existing users:

1. Back up the database and identify duplicates after `lower(trim(email))`; resolve them while preserving workout ownership.
2. Normalize existing email addresses and ensure name, email, and role are populated.
3. Provision each intended account with its own BCrypt hash through a controlled process, or register new development accounts. Never use a shared default password or store plaintext. Accounts with null passwords cannot log in.
4. Verify `is_active` for intended accounts. Existing false values are deliberately not overwritten.
5. After all rows are provisioned, apply `docs/sql/auth_constraints.sql` and update the entity nullability to match. The SQL is manual and is not run at application startup.

Keep password-reset tokens in a separate future entity when implementing that feature.

## Verification

Run `./mvnw test`. Tests use an isolated H2 database in PostgreSQL compatibility mode and a test-only signing key; they do not use the hosted database. They exercise the HTTP security chain, BCrypt, signed tokens, validation, duplicate email, login, current user, invalid tokens, inactive users, CORS, both roles creating templates, and cross-user ownership checks. PostgreSQL deployment and client integration still require environment-specific verification.

Implementation references: [Spring Security password authentication](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html) and [JJWT documentation](https://github.com/jwtk/jjwt).
