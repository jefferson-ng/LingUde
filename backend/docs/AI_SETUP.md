# AI Chat Setup Guide

This guide explains how to set up and configure the AI-powered language learning chat feature for the SEP backend.

## Prerequisites

- Java 21 or higher
- PostgreSQL database
- Google Gemini API key
- Gradle 8.x

## Getting Your Gemini API Key

1. Visit [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Sign in with your Google account
3. Click **"Create API Key"**
4. Copy the generated API key
5. Keep it secure - never commit it to version control!

## Environment Configuration

### 1. Create Environment File

Create a `.env` file in the `backend/` directory (or configure your deployment environment):

```bash
# Copy from the example
cp .env.example .env
```

### 2. Configure Environment Variables

Edit `.env` and add your Gemini API key:

```bash
# Google Gemini AI Configuration
GOOGLE_GEMINI_API_KEY=your-actual-api-key-here
GOOGLE_GEMINI_MODEL=gemini-2.0-flash-exp
GOOGLE_GEMINI_TEMPERATURE=0.7
AI_CHAT_ENABLED=true
```

**Important Environment Variables:**

| Variable | Description | Default |
|----------|-------------|---------|
| `GOOGLE_GEMINI_API_KEY` | Your Gemini API key (required) | - |
| `GOOGLE_GEMINI_MODEL` | Gemini model to use | `gemini-2.0-flash-exp` |
| `GOOGLE_GEMINI_TEMPERATURE` | Creativity level (0.0-1.0) | `0.7` |
| `AI_CHAT_ENABLED` | Enable/disable chat feature | `true` |

## Database Setup

The chat feature requires additional database tables. These are automatically created via Flyway migration.

### Migration Files

- `V2__create_chat_tables.sql` - Creates tables for:
  - `chat_session` - Conversation sessions
  - `chat_message` - Individual messages
  - `tool_execution_log` - AI tool call audit logs

### Running Migrations

Migrations run automatically on application startup. To run manually:

```bash
./gradlew flywayMigrate
```

## Build & Run

### 1. Build the Application

```bash
cd backend
./gradlew build
```

### 2. Run the Application

```bash
./gradlew bootRun
```

Or run with environment variables:

```bash
GOOGLE_GEMINI_API_KEY=your-key ./gradlew bootRun
```

### 3. Verify Setup

Check the logs on startup - you should see:

```
INFO  c.s.s.a.tool.AiToolService - Registered 4 AI tools: [addXp, getUserProfile, getLevelProgress, getStreak]
INFO  c.s.s.a.provider.GeminiAiProvider - Gemini AI provider initialized
```

## Testing the Chat API

### Get a JWT Token

First, authenticate to get a JWT token:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

Copy the `accessToken` from the response.

### Send a Chat Message

```bash
curl -X POST http://localhost:8080/api/chat/message \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Hello! Can you help me learn Spanish?"
  }'
```

### Expected Response

```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "response": "¡Hola! Of course I can help you learn Spanish! I see you're at A1 level...",
  "timestamp": "2024-01-08T10:30:00",
  "currentXp": 150,
  "currentStreak": 3
}
```

## Configuration Options

### Model Selection

You can use different Gemini models:

- `gemini-2.0-flash-exp` - Fast, recommended for chat
- `gemini-1.5-pro` - More capable, slower
- `gemini-1.5-flash` - Balanced option

### Temperature Setting

Controls AI creativity (0.0 = deterministic, 1.0 = creative):

- **0.3-0.5** - Precise grammar explanations
- **0.7** (default) - Balanced tutoring
- **0.8-1.0** - More varied, creative responses

## Troubleshooting

### API Key Issues

**Error:** `AI service unavailable after 3 attempts`

**Solutions:**
1. Verify your API key is correct in `.env`
2. Check [API key quotas](https://aistudio.google.com/app/apikey)
3. Ensure API key has not expired
4. Check network connectivity to Google APIs

### Database Connection Issues

**Error:** `Flyway migration failed`

**Solutions:**
1. Verify PostgreSQL is running
2. Check database credentials in `application.properties`
3. Ensure database exists: `CREATE DATABASE sep;`
4. Check database user has CREATE TABLE permissions

### Build Issues

**Error:** `The import com.google cannot be resolved`

**Solutions:**
1. Run `./gradlew clean build --refresh-dependencies`
2. Check `build.gradle` contains: `implementation 'com.google.genai:google-genai:1.29.0'`
3. Wait for IDE to finish indexing (warnings may appear temporarily)

### Chat Not Working

**Checklist:**
- [ ] API key is set in environment
- [ ] `AI_CHAT_ENABLED=true`
- [ ] Database migrations completed successfully
- [ ] User has a `UserLearning` record (created automatically on first login)
- [ ] JWT token is valid and not expired
- [ ] Endpoint security is configured (see `SecurityConfig.java`)

## Production Deployment

### Security Recommendations

1. **Never commit API keys** - Use environment variables or secrets management
2. **Use HTTPS** - Encrypt all API traffic
3. **Enable rate limiting** - Prevent API quota abuse
4. **Monitor costs** - Track Gemini API usage
5. **Implement request validation** - Sanitize user input

### Environment Variables (Production)

```bash
# Use secrets manager (AWS Secrets Manager, Azure Key Vault, etc.)
GOOGLE_GEMINI_API_KEY=${secrets.gemini_api_key}
GOOGLE_GEMINI_MODEL=gemini-2.0-flash-exp
GOOGLE_GEMINI_TEMPERATURE=0.7

# Database (use managed PostgreSQL)
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/sep_prod
SPRING_DATASOURCE_USERNAME=${secrets.db_username}
SPRING_DATASOURCE_PASSWORD=${secrets.db_password}

# JWT (use strong secrets)
JWT_SECRET=${secrets.jwt_secret_256_bit}
```

### Monitoring

Monitor these metrics in production:

- **API call latency** - Track response times
- **Tool call frequency** - Monitor XP abuse patterns
- **Error rates** - Watch for Gemini API failures
- **User engagement** - Messages per session, session duration

## Cost Estimation

### Gemini API Pricing (as of Jan 2024)

- **gemini-2.0-flash-exp** - Free tier available
- **gemini-1.5-flash** - $0.075 per 1M input tokens
- **gemini-1.5-pro** - $1.25 per 1M input tokens

### Typical Usage

- Average conversation: 50-100 messages
- Average tokens per message: 500-1000
- Estimated cost per conversation: $0.01-0.05 (for paid models)

**Free tier limits:**
- 15 requests per minute
- 1 million tokens per minute
- 1,500 requests per day

## Support & Resources

- [Google Gemini Documentation](https://ai.google.dev/docs)
- [Gemini API Quickstart](https://ai.google.dev/tutorials/get_started_web)
- [API Reference](https://ai.google.dev/api/rest)
- [SEP API Documentation](./API_CHAT.md)

## Version History

- **v1.0.0** (2024-01) - Initial AI chat implementation
  - 4 tool handlers (XP, streak, level, profile)
  - Multi-turn conversation support
  - Session management
  - Spanish language support (extensible to other languages)
