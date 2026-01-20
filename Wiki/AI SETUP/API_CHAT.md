# AI Chat API Documentation

REST API documentation for the AI-powered language learning chat feature.

## Base URL

```
http://localhost:8080/api/chat
```

## Authentication

All chat endpoints require a valid JWT access token in the `Authorization` header:

```
Authorization: Bearer <access_token>
```

Get an access token via the `/api/auth/login` endpoint.

---

## Endpoints

### 1. Send Message

Send a message to the AI language tutor and receive a response.

**Endpoint:** `POST /api/chat/message`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <access_token>
```

**Request Body:**

```json
{
  "message": "Hello! Can you help me practice ordering food in Spanish?",
  "sessionId": "550e8400-e29b-41d4-a716-446655440000"  // Optional
}
```

**Parameters:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `message` | String | Yes | User's message (max 2000 chars) |
| `sessionId` | String (UUID) | No | Session ID to continue conversation. If omitted, uses or creates active session |

**Response:** `200 OK`

```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "response": "¡Hola! Let's practice ordering food together! I'll be the waiter at a Spanish restaurant.\n\nWaiter: '¡Buenas tardes! ¿Qué desea tomar?'\n\nNow, try to order a coffee and a sandwich in Spanish!",
  "timestamp": "2024-01-08T10:30:45.123",
  "currentXp": 150,
  "currentStreak": 3
}
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `sessionId` | String (UUID) | Session ID for this conversation |
| `response` | String | AI tutor's response |
| `timestamp` | DateTime | Response timestamp |
| `currentXp` | Integer | User's current XP (may increase if exercise completed) |
| `currentStreak` | Integer | User's current daily streak |

**Example cURL:**

```bash
curl -X POST http://localhost:8080/api/chat/message \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "message": "¿Me gustaría un café y un bocadillo, por favor?"
  }'
```

**Error Responses:**

- `400 Bad Request` - Invalid message (blank or too long)
- `401 Unauthorized` - Invalid or missing JWT token
- `500 Internal Server Error` - AI service unavailable

---

### 2. Get Chat History

Retrieve the full conversation history for a session.

**Endpoint:** `GET /api/chat/history/{sessionId}`

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `sessionId` | UUID | The session ID to retrieve |

**Response:** `200 OK`

```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "learningLanguage": "ES",
  "createdAt": "2024-01-08T10:00:00",
  "messages": [
    {
      "role": "USER",
      "content": "Hello! Can you help me learn Spanish?",
      "timestamp": "2024-01-08T10:00:15"
    },
    {
      "role": "MODEL",
      "content": "¡Hola! Of course I can help you learn Spanish! Let me check your level first...",
      "timestamp": "2024-01-08T10:00:17"
    },
    {
      "role": "USER",
      "content": "Great! I want to learn about ordering food.",
      "timestamp": "2024-01-08T10:01:00"
    }
  ]
}
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `sessionId` | String (UUID) | Session identifier |
| `learningLanguage` | String | Language being learned (ES, FR, DE, IT, EN) |
| `createdAt` | DateTime | Session creation time |
| `messages` | Array | List of messages in chronological order |
| `messages[].role` | String | Message sender: USER, MODEL, or TOOL |
| `messages[].content` | String | Message text |
| `messages[].timestamp` | DateTime | Message timestamp |

**Example cURL:**

```bash
curl -X GET http://localhost:8080/api/chat/history/550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Error Responses:**

- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Session belongs to another user
- `404 Not Found` - Session does not exist

---

### 3. Get User Sessions

List all chat sessions for the authenticated user.

**Endpoint:** `GET /api/chat/sessions`

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response:** `200 OK`

```json
[
  {
    "sessionId": "550e8400-e29b-41d4-a716-446655440000",
    "learningLanguage": "ES",
    "createdAt": "2024-01-08T10:00:00",
    "updatedAt": "2024-01-08T10:30:00",
    "isActive": true,
    "messageCount": 15
  },
  {
    "sessionId": "660e9511-f30c-52e5-b827-557766551111",
    "learningLanguage": "ES",
    "createdAt": "2024-01-07T14:00:00",
    "updatedAt": "2024-01-07T14:45:00",
    "isActive": false,
    "messageCount": 22
  }
]
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `sessionId` | String (UUID) | Session identifier |
| `learningLanguage` | String | Language being learned |
| `createdAt` | DateTime | Session creation time |
| `updatedAt` | DateTime | Last message time |
| `isActive` | Boolean | Whether session is currently active |
| `messageCount` | Integer | Total messages in session |

**Example cURL:**

```bash
curl -X GET http://localhost:8080/api/chat/sessions \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Error Responses:**

- `401 Unauthorized` - Invalid or missing JWT token

---

### 4. Close Session

Mark a chat session as inactive. Future messages will create a new session.

**Endpoint:** `POST /api/chat/sessions/{sessionId}/close`

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `sessionId` | UUID | The session ID to close |

**Response:** `200 OK`

```json
{}
```

**Example cURL:**

```bash
curl -X POST http://localhost:8080/api/chat/sessions/550e8400-e29b-41d4-a716-446655440000/close \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Error Responses:**

- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Session belongs to another user
- `404 Not Found` - Session does not exist

---

## AI Tool Handlers

The AI tutor can call these tools during conversations to enhance the learning experience.

### Available Tools

| Tool | Description | When AI Uses It |
|------|-------------|-----------------|
| `getUserProfile` | Get user's learning language, current level, target level | At conversation start |
| `getLevelProgress` | Get user's XP and level progress | When discussing progress |
| `getStreak` | Get user's daily practice streak | When discussing habits |
| `addXp` | Award XP for completed exercises | When student completes an exercise successfully |

### XP Award Rules

The AI is instructed to only award XP when students complete exercises it creates:

| Exercise Type | XP Range | Example |
|---------------|----------|---------|
| Simple vocabulary/translation | 5-10 XP | "How do you say 'hello' in Spanish?" |
| Sentence construction | 10-15 XP | "Build a sentence: I want to eat pizza" |
| Conversation practice | 15-20 XP | Role-playing restaurant ordering |
| Complex grammar | 20-25 XP | Conjugating irregular verbs correctly |

**XP is NOT awarded for:**
- Just chatting or asking questions
- Requesting help or explanations
- Partial or incorrect answers
- The same exercise multiple times

---

## Conversation Flow

### 1. First Message

When a user sends their first message:

1. System checks for active session
2. If none exists, creates new session
3. AI calls `getUserProfile` to personalize tutoring
4. AI responds in the user's learning language

### 2. Ongoing Conversation

During conversation:

1. User sends message
2. AI processes in context of full conversation history
3. AI may call tools (check progress, award XP, etc.)
4. AI responds with teaching content
5. Session updated timestamp reflects last activity

### 3. Exercise Completion

When AI creates an exercise and student completes it:

1. Student responds correctly
2. AI validates the answer
3. AI calls `addXp(amount, reason)` to reward progress
4. Response includes updated `currentXp`
5. User sees XP increase in real-time

---

## System Prompt Overview

The AI tutor follows these guidelines (from `SystemPromptService`):

### Teaching Approach
- Always speaks in the target language (unless student asks for English)
- Creates interactive exercises during conversation
- Provides immediate, constructive feedback
- Adjusts difficulty based on student's CEFR level

### Personality
- Enthusiastic and supportive
- Uses real-world scenarios
- Celebrates successes
- Encourages through mistakes

### Tool Usage
- Checks profile once per new conversation
- Awards XP only for completed exercises
- Tracks progress when relevant
- References streaks for motivation

---

## Rate Limiting

Currently no rate limiting is enforced on the backend. Consider implementing:

- **Per-user limits:** 50 messages per hour
- **Per-session limits:** 100 messages per session
- **API quota monitoring:** Track Gemini API usage

---

## Testing Examples

### Complete Workflow Test

```bash
# 1. Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}' \
  | jq -r '.accessToken')

# 2. Send first message
curl -X POST http://localhost:8080/api/chat/message \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message":"Hello! I want to learn Spanish."}'

# 3. Continue conversation
curl -X POST http://localhost:8080/api/chat/message \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message":"Can you teach me how to introduce myself?"}'

# 4. Get all sessions
curl -X GET http://localhost:8080/api/chat/sessions \
  -H "Authorization: Bearer $TOKEN"
```

---

## Error Handling

### Common Error Codes

| Status | Error | Cause | Solution |
|--------|-------|-------|----------|
| 400 | Bad Request | Invalid message format | Check request body validation |
| 401 | Unauthorized | Missing/invalid JWT | Re-authenticate via `/api/auth/login` |
| 403 | Forbidden | Accessing another user's session | Use correct session ID |
| 404 | Not Found | Session doesn't exist | Check session ID or create new session |
| 500 | Internal Server Error | AI service unavailable | Check Gemini API key and quotas |

### Example Error Response

```json
{
  "timestamp": "2024-01-08T10:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Message cannot be blank",
  "path": "/api/chat/message"
}
```

---

## Best Practices

### For Frontend Integration

1. **Store session ID:** Persist `sessionId` from first response
2. **Handle XP updates:** Show XP increases with animations
3. **Display typing indicators:** Inform user AI is "thinking"
4. **Implement retry logic:** Handle temporary API failures
5. **Sanitize user input:** Clean input before sending

### For Backend Developers

1. **Monitor tool calls:** Watch for XP abuse patterns
2. **Log conversations:** Keep audit trail for debugging
3. **Set request timeouts:** Don't let connections hang
4. **Implement caching:** Cache user profiles for sessions
5. **Test failure scenarios:** Ensure graceful degradation

---

## Future Enhancements

Planned features:

- **Voice input/output:** Text-to-speech and speech-to-text
- **Exercise generation tool:** AI creates structured exercises on-demand
- **Multi-language support:** Expand beyond Spanish
- **Difficulty adaptation:** Dynamic level adjustment
- **Progress analytics:** Detailed learning insights

---

## Support

For issues or questions:

- **Setup issues:** See [AI_SETUP.md](./AI_SETUP.md)
- **API errors:** Check application logs
- **Feature requests:** Submit GitHub issue

---

## Changelog

### v1.0.0 (2024-01)
- Initial chat API release
- 4 tool handlers (profile, progress, streak, XP)
- Session management
- Multi-turn conversation support
- Spanish language support
