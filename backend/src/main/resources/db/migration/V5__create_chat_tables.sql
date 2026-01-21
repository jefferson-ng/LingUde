-- Chat Session Table
-- Stores conversation sessions between user and AI tutor
CREATE TABLE chat_session (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    learning_language VARCHAR(10),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT fk_chat_session_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_chat_session_user_id ON chat_session(user_id);
CREATE INDEX idx_chat_session_active ON chat_session(user_id, is_active);

-- Chat Message Table
-- Stores individual messages in a conversation
CREATE TABLE chat_message (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT,
    tool_calls JSONB,
    tool_results JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_chat_message_session
        FOREIGN KEY (session_id)
        REFERENCES chat_session(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_role
        CHECK (role IN ('USER', 'MODEL', 'TOOL'))
);

CREATE INDEX idx_chat_message_session ON chat_message(session_id, created_at);

-- Tool Execution Log Table
-- Tracks tool executions for idempotency and debugging
CREATE TABLE tool_execution_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL,
    tool_name VARCHAR(100) NOT NULL,
    parameters JSONB NOT NULL,
    result JSONB,
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    execution_time_ms INTEGER,
    success BOOLEAN NOT NULL,
    error_message TEXT,

    CONSTRAINT fk_tool_execution_session
        FOREIGN KEY (session_id)
        REFERENCES chat_session(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_tool_execution_session ON tool_execution_log(session_id, executed_at);
CREATE INDEX idx_tool_execution_name ON tool_execution_log(tool_name);
