CREATE TABLE messages (
  id bigserial PRIMARY KEY,
  extId UUID NOT NULL,
  user_to_id UUID NOT NULL,
  content TEXT,
  is_read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE outbox_events (
   id bigserial PRIMARY KEY,
   message_key UUID NOT NULL,
   payload JSONB NOT NULL,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


