# 스테이지 admin 디버그 — 어드민 토큰(iss=setofAdmin)만 필터

경로 리라이트(`/api/v1/` → `/api/v1/legacy/`) 적용 후, **레거시 어드민이 발급한 JWT 토큰**으로 요청한 건만 필터.

---

## 1. GET /api/v1/legacy/products/group (SELLER 토큰)

**요청:**
```
GET http://marketplace-legacy-api-stage.connectly.local:8081/api/v1/legacy/products/group
Headers: {
  'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzZXRvZkFkbWluIiwiaWQiOjc4LCJzdWIiOiJqYWNlQGNsYXBzLmtyIiwiYXVkIjoiU0VMTEVSIiwiaWF0IjoxNzc0MjI2Mjg4LCJleHAiOjE3NzQyMzcwODh9.QqxrZ3dHf6Mi2iOa0vN8gzQ2wgUDrte8uYK0frzm0nk',
  'X-Trace-Id': '20260323100404731-1153e8af-afba-41be-8621-b2b60ea8c364'
}
Cookies: {
  'token': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzZXRvZkFkbWluIiwiaWQiOjc4LCJzdWIiOiJqYWNlQGNsYXBzLmtyIiwiYXVkIjoiU0VMTEVSIiwiaWF0IjoxNzc0MjI2Mjg4LCJleHAiOjE3NzQyMzcwODh9.QqxrZ3dHf6Mi2iOa0vN8gzQ2wgUDrte8uYK0frzm0nk'
}
```
JWT payload:
```json
{"iss": "setofAdmin", "id": 78, "sub": "jace@claps.kr", "aud": "SELLER", "iat": 1774226288, "exp": 1774237088}
```

**응답:**
```
Status: 401
Body: {"type":"about:blank","title":"Unauthorized","status":401,"detail":"인증이 필요합니다","instance":"/error","code":"AUTHENTICATION_REQUIRED"}
```

---

## 2. GET /api/v1/legacy/orders (MASTER 토큰)

**요청:**
```
GET http://marketplace-legacy-api-stage.connectly.local:8081/api/v1/legacy/orders
Headers: {
  'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzZXRvZkFkbWluIiwiaWQiOjEsInN1YiI6Indvb3JhbmdAdHJleGkuY28ua3IiLCJhdWQiOiJNQVNURVIiLCJpYXQiOjE3NzQyMjYwMzIsImV4cCI6MTc3NDIzNjgzMn0.makp59Ur6YYJxLCv4QL0FJSdCkFj9VkJd2wvOHhckCM',
  'X-Trace-Id': '...'
}
Cookies: {
  'token': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzZXRvZkFkbWluIiwiaWQiOjEsInN1YiI6Indvb3JhbmdAdHJleGkuY28ua3IiLCJhdWQiOiJNQVNURVIiLCJpYXQiOjE3NzQyMjYwMzIsImV4cCI6MTc3NDIzNjgzMn0.makp59Ur6YYJxLCv4QL0FJSdCkFj9VkJd2wvOHhckCM'
}
```
JWT payload:
```json
{"iss": "setofAdmin", "id": 1, "sub": "woorang@trexi.co.kr", "aud": "MASTER", "iat": 1774226032, "exp": 1774236832}
```

**응답:**
```
Status: 401
Body: {"type":"about:blank","title":"Unauthorized","status":401,"detail":"인증이 필요합니다","instance":"/api/v1/legacy/orders","code":"AUTHENTICATION_REQUIRED"}
```

---

## 3. GET /api/v1/legacy/order/today-dashboard (SELLER 토큰)

**요청:**
```
GET http://marketplace-legacy-api-stage.connectly.local:8081/api/v1/legacy/order/today-dashboard
Headers: {
  'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzZXRvZkFkbWluIiwiaWQiOjc4LCJzdWIiOiJqYWNlQGNsYXBzLmtyIiwiYXVkIjoiU0VMTEVSIiwiaWF0IjoxNzc0MjI2Mjg4LCJleHAiOjE3NzQyMzcwODh9.QqxrZ3dHf6Mi2iOa0vN8gzQ2wgUDrte8uYK0frzm0nk',
  'X-Trace-Id': '...'
}
Cookies: {
  'token': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzZXRvZkFkbWluIiwiaWQiOjc4LCJzdWIiOiJqYWNlQGNsYXBzLmtyIiwiYXVkIjoiU0VMTEVSIiwiaWF0IjoxNzc0MjI2Mjg4LCJleHAiOjE3NzQyMzcwODh9.QqxrZ3dHf6Mi2iOa0vN8gzQ2wgUDrte8uYK0frzm0nk'
}
```
JWT payload:
```json
{"iss": "setofAdmin", "id": 78, "sub": "jace@claps.kr", "aud": "SELLER", "iat": 1774226288, "exp": 1774237088}
```

**응답:** (응답 로그에서 아직 미확인 — 같은 토큰이므로 401 예상)

---

## 요약

| 요청 | 토큰 발급자 | role | 만료 | 응답 |
|------|------------|------|------|------|
| GET /api/v1/legacy/products/group | setofAdmin | SELLER | 12:44 (유효) | **401** |
| GET /api/v1/legacy/orders | setofAdmin | MASTER | 12:27 (유효) | **401** |
| GET /api/v1/legacy/order/today-dashboard | setofAdmin | SELLER | 12:44 (유효) | **401 예상** |

**결론:** 레거시 어드민이 발급한 JWT 토큰(iss=setofAdmin, 만료 전)으로 요청해도 스테이지 서버에서 401 반환. JWT secret이 레거시 어드민과 스테이지 서버 간에 다를 가능성이 높음.
