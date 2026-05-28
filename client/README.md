# darbata — visual test client

A React client for exercising every public-facing interface of the chat /
dispatcher / conversations services from a browser. Designed for
side-by-side two-browser testing where each window is a different user.

## What it covers

**Conversations REST (`conversations` service)**

- `GET    /api/conversations` — list (sidebar)
- `GET    /api/conversations/{id}/messages` — message history with pagination
- `POST   /api/conversations` — new conversation dialog
- `DELETE /api/conversations/{id}/leave` — header menu → "Leave"
- `PUT    /api/conversations/{id}/messages/{messageId}` — hover a message → pencil icon
- `DELETE /api/conversations/{id}/messages/{messageId}` — hover a message → trash icon

**STOMP over WebSocket (`chat` service)**

- CONNECT with `X-User-Id` native header
- SEND `/app/chat` on submit
- SEND `/app/typing` debounced while typing (≤ once / 2s)
- SEND `/app/read` automatically when a new incoming message lands in the
  selected conversation
- SUBSCRIBE `/user/queue/chats/delivered`, `…/typing`, `…/read`

Every inbound and outbound STOMP frame is captured in the event log panel
(scroll icon in the sidebar) so you can verify the wire format end-to-end.

## Configure

```bash
cp .env.example .env
# edit URLs if needed
```

| Variable                      | Default                       |
| ----------------------------- | ----------------------------- |
| `VITE_CONVERSATIONS_API_URL`  | `http://localhost:8081/api`   |
| `VITE_CHAT_WS_URL`            | `ws://localhost:8080/ws`      |

The Vite dev server is pinned to port **5173** because the chat service's
`WebSocketConfig` hard-codes
`setAllowedOrigins("http://localhost:5173")`. If you need a different
port, update both ends.

## Run

```bash
npm install
npm run dev
```

Open <http://localhost:5173>, log in as one of the seeded users
(`luffy`, `sanji`, `zoro`, `chopper`, `nami`, `robin`, `ussop`) — these
match `init/init-db/init-db.sql`.

## Two-user testing

The username is persisted in `localStorage`, which is shared across tabs
of the same browser. To run two distinct user sessions:

1. **Two different browsers** (Chrome + Firefox), or
2. One regular window + one private/incognito window, or
3. Two Chrome user profiles.

Conversations seeded by `init-db.sql`:

| ID | Participants |
| -- | ------------------------------------ |
| 1  | luffy, sanji |
| 2  | luffy, zoro |
| 3  | luffy, nami, robin, chopper |

So logging in as `luffy` in one window and `sanji` in another gives you
conversation #1 to test the round-trip.

## Architecture quick reference

```
LoginPage  ─┐
            ├─► UserContext (localStorage)
ChatPage   ─┘                    │
            │                     ▼
            │              ChatStoreProvider
            │                ├── conversationsApi  ──► conversations REST
            │                └── useStomp          ──► chat STOMP/WS
            │
            ├── ConversationsSidebar
            │     └── ConversationsList → ConversationItem
            └── ConversationMessages
                  ├── ConversationHeader
                  ├── MessagesView → MessageItem
                  └── SendMessageInput
```

`ChatStore.tsx` is the single source of truth — every component reads
from / writes through it, and the STOMP callbacks update it directly.
