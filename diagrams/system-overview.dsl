workspace "Real Time Chat Application" "SIT226 HD Project" {
    !identifiers hierarchical
    model {

        user = person "Users" "Interacts with client to communicate with other chat application users" "User" 
        client = softwareSystem "Web Application"  "Provides interface to interact with chat services" "WebApp"

        rtc = softwareSystem "Chat Service" {
          ingress = container "Ingress" "Fronts microservices, aggregating services into a single address" "Kubernetes Ingress" "Ingress"
          chat-api = container "Chat API" "Manages client WebSocket connections. Responsible for both inbound and outbound WebSocket messages for online clients." "Spring Boot" "API"
          conversations-api = container "Conversations API"  "Main REST API offers CRUD operations on conversations and messages." "Spring Boot" "API"
          dynamodb = container "Key-Value Store" "Source of truth for messages within a conversation" "DynamoDB" "Database"
          dispatcher-api = container "Dispatcher API" "None-public-facing service. Exposes endpoints to GET messages within a conversation. Also is responsible for creating 'Dispatch' events and deciding which participants events are sent to." "Spring Boot" "API"
          postgres = container "Relational Database" "Source of truth for conversation data, such as participants within a conversation" "PostgreSQL" "Database"
          broker = container "Event Broker" "Routes events between services and fans messages out to connected clients." "RabbitMQ" "Broker"
        }

        user -> client "Uses"

        client -> rtc.ingress "HTTP"

        rtc.ingress -> rtc.chat-api "WebSocket messages to"
        rtc.ingress -> rtc.conversations-api "HTTP requests to"

        rtc.conversations-api -> rtc.postgres "Queries conversation data"
        rtc.dispatcher-api -> rtc.dynamodb "Queries messages data"

        rtc.dispatcher-api -> rtc.conversations-api "Fetches conversation messages to decide recipients to fan-out (dispatch) to"
        rtc.conversations-api -> rtc.dispatcher-api "Fetches messages to satisfy relevant HTTP requests"

        rtc.broker -> rtc.dispatcher-api "Listens to incoming chat messages queue"
        
        rtc.broker -> rtc.chat-api "Listens to dispatch (outgoing) chat messages queue"

        rtc.dispatcher-api -> rtc.broker "Publishes dispatch events"
        rtc.conversations-api -> rtc.broker "Publishes conversation events (e.g. new conversation created)"
        rtc.chat-api -> rtc.broker "Publishes incoming chat messages from WebSocket clients"

    }
    views {
        systemContext rtc {
            include *
            include user
            include client
            include rtc
        }
        container rtc {
            include *
            include user
            include client
            include rtc
        }

        styles {
            element "Element" {
                shape RoundedBox
            }
            element "Person" {
                shape Person
            }
            element "Database" {
                shape "Cylinder"
            }
            element "Broker" {
                shape Pipe
            }
            element "Ingress" {
                shape Hexagon
            }
            element "WebApp" {
                shape Window
            }
        }
    }
    configuration {
        scope softwaresystem
    }
}


