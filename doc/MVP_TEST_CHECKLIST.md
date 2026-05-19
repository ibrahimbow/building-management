# Building Management MVP Manual Test Checklist

## Purpose

This checklist validates the complete MVP flow of the Building Management platform in a professional enterprise-style environment.

The goal is to verify:

- Gateway routing
- Authentication and authorization
- Role-based access control
- Building ownership
- Tenant membership
- Announcements
- Share & Help
- File uploads
- Chat functionality
- WebSocket realtime messaging
- Soft delete behavior
- Docker integration
- Database migrations
- Service-to-service communication
- Backend stability

---

## 1. Start Full Backend System

### Start Docker Environment

```bash
docker compose up --build
```

### Verify Containers

Expected containers:

```text
bm-gateway-service
bm-auth-service
bm-building-service
bm-announcement-service
bm-share-and-help-service
bm-file-service
bm-chat-service
```

### Verify Gateway Health

Open:

```text
http://localhost:8080/actuator/health
```

Expected result:

```json
{
  "status": "UP"
}
```

---

## 2. Start Frontend

### Start Angular Frontend

```bash
ng serve
```

Open:

```text
http://localhost:4200
```

Verify:

- Frontend starts successfully
- No console startup errors
- API requests go through gateway at `http://localhost:8080`
- No direct calls to internal services

---

## 3. Authentication Flow

### Manager Login

Verify:

- Manager can login successfully
- JWT token is stored
- Manager is redirected to manager dashboard
- Manager-only routes are accessible
- Tenant-only routes are blocked

### Tenant Login

Verify:

- Tenant can login successfully
- JWT token is stored
- Tenant is redirected to tenant dashboard
- Tenant-only routes are accessible
- Manager-only routes are blocked

### Invalid Login

Verify:

- Invalid credentials are rejected
- Clear error message is shown
- No token is stored

---

## 4. Building Management Flow

### Manager Creates Building

Verify:

- Manager can create a building
- Building name is saved
- Address is saved
- Total apartments is saved
- Emergency phone is saved
- Unique building code is generated
- Building appears in manager dashboard

### Manager Ownership Rules

Verify:

- Manager can view only own building
- Manager cannot access another manager building
- Manager cannot create a second building if MVP rule allows only one building
- Unauthorized access returns proper error

---

## 5. Tenant Membership Flow

### Tenant Joins Building

Verify:

- Tenant can join using building code
- Tenant sees current joined building
- Tenant cannot join another building while already active in one
- Duplicate active membership is prevented

### Tenant Leaves Building

Verify:

- Tenant can leave current building
- Membership is soft-left
- Tenant no longer sees building dashboard data
- Tenant can rejoin after leaving

---

## 6. Announcement Flow

### Manager Creates Announcement

Verify:

- Manager can create announcement
- Title is saved
- Content is saved
- Category is saved
- Optional image upload works
- Created date is visible
- Announcement appears in manager dashboard

### Tenant Reads Announcement

Verify:

- Tenant sees announcements for own building
- Tenant does not see announcements from other buildings
- Ordering is correct
- Images load correctly through file service

---

## 7. Share & Help Flow

### Tenant Creates Post

Verify:

- Tenant can create Share & Help post
- Title is saved
- Description is saved
- Optional image upload works
- Display name is visible
- Avatar is visible if available
- Email is not exposed publicly

### Tenant Adds Comment

Verify:

- Tenant can add comment
- Comment appears under the correct post
- Comment author display name is visible
- Comments are ordered correctly

### Ownership Validation

Verify:

- User can update/delete own post if supported
- User cannot delete another user post
- User can delete own comment if supported
- Unauthorized deletion is blocked

---

## 8. File Upload Flow

### Valid Uploads

Verify upload works for:

- JPG
- PNG
- WEBP

Verify upload categories:

```text
PROFILE_AVATAR
ANNOUNCEMENT_IMAGE
SHARE_AND_HELP_IMAGE
CHAT_MESSAGE_IMAGE
```

### Invalid Uploads

Verify:

- Empty file is rejected
- Unsupported file type is rejected
- Invalid content type is rejected
- Oversized file is rejected if size validation exists

### File Retrieval

Verify:

```text
GET /api/files/{type}/{fileName}
```

Expected:

- File is returned successfully
- Content type is correct
- Missing file returns correct error

---

## 9. Chat Flow

### Open Building Chat

Verify:

- Tenant with active building can open chat
- Tenant without active building cannot access chat
- Messages load correctly
- Messages are ordered by creation date

### Send Message

Verify:

- Tenant can send text message
- Message appears in chat
- Sender display name is visible
- Sender avatar is visible if available
- Timestamp is visible
- Email is not exposed publicly

### Upload Chat Image

Verify:

- Tenant can upload one image
- Image URL is saved
- Image appears in message
- Invalid image is rejected

### Emoji Reaction

Verify:

- Tenant can react to message
- Reaction is saved
- Duplicate same emoji reaction is prevented or toggled depending on design
- Reaction count updates correctly
- Current user's reaction state is visible

### Soft Delete Message

Verify:

- User can delete own message
- User cannot delete another user's message
- Message remains in database
- Message is marked as deleted
- Deleted timestamp is stored
- Frontend hides deleted content or displays deleted state

---

## 10. WebSocket Realtime Flow

### Browser Setup

Open two browser sessions:

```text
Browser 1: Tenant A
Browser 2: Tenant B
```

Both tenants must be in the same building.

### Realtime Message Test

When Tenant A sends a message:

- Tenant B receives the message instantly
- No page refresh is required
- Message appears in correct order
- Backend logs show no WebSocket errors

### Realtime Reaction Test

When Tenant B reacts to a message:

- Tenant A sees reaction update instantly
- Reaction count updates correctly

### Realtime Delete Test

When Tenant A deletes own message:

- Tenant B sees delete update instantly
- Message content is hidden or marked deleted

---

## 11. Gateway Validation

Verify all frontend requests go through:

```text
http://localhost:8080
```

Check routes:

```text
/api/auth/**
/api/buildings/**
/api/manager/buildings/**
/api/tenant/buildings/**
/api/manager/announcements/**
/api/tenant/announcements/**
/api/manager/share-and-help/**
/api/tenant/share-and-help/**
/api/files/**
/api/tenant/chat/**
/ws/**
```

Expected:

- Gateway forwards requests correctly
- JWT is validated at gateway
- User headers are forwarded correctly
- Internal services are not publicly exposed

---

## 12. Security Validation

Verify:

- Protected endpoints require JWT
- Role restrictions work
- Manager endpoints reject tenants
- Tenant endpoints reject managers where needed
- Invalid token is rejected
- Missing token is rejected
- Public auth endpoints remain accessible

Expected forwarded headers:

```text
X-User-Id
X-User-Email
X-User-Role
X-User-Display-Name
X-User-Avatar-Url
```

---

## 13. Docker Validation

Verify:

- Only gateway exposes public port `8080:8080`
- Internal services use container-only ports or expose
- PostgreSQL starts correctly
- Services wait for required dependencies
- Environment variables are loaded
- No service crashes after startup

---

## 14. Database Validation

Verify:

- Flyway migrations run successfully
- Tables are created
- Unique constraints work
- Foreign keys work
- Soft delete columns are populated correctly
- No unexpected schema validation errors

---

## 15. Automated Test Validation

Run from backend root:

```bash
mvn clean verify
```

Expected:

- All unit tests pass
- All controller tests pass
- All service tests pass
- All persistence adapter tests pass
- All integration tests pass
- All Testcontainers tests pass

---

## 16. Backend Stability Validation

Verify during full manual flow:

- No backend stack traces
- No failed service-to-service calls
- No CORS errors
- No WebSocket handshake failures
- No database migration failures
- No unexpected 500 errors

---

## 17. Final MVP Pass Conditions

MVP is considered complete when:

- All services start successfully
- Frontend communicates only through gateway
- Authentication works
- Authorization works
- Building flow works
- Tenant join/leave flow works
- Announcement flow works
- Share & Help flow works
- File upload and retrieval work
- Chat works
- WebSocket realtime works
- Soft delete works
- Docker environment is stable
- All automated tests pass
- Manual MVP flow is fully validated

---

## Final Sign-off

```text
MVP Status: PASSED / FAILED
Tester:
Date:
Notes:
```
