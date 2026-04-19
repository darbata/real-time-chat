import {useEffect, useRef, useState} from "react";
import {Client, type Frame} from "@stomp/stompjs";
import type {ULID} from "ulid";

const token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdWItMDQ5OWZhOTYtODA5Yi00OGZmLTlhZGUtNzk4MGM4ZDljNjk5IiwiaWQiOiIxMjM0NTY3IiwiaWF0IjoxNzc2NTY3NDg1LCJleHAiOjE3NzY1NzEwODV9.hawHdEIaGLhnmBb5XoQyxrMZ0A1jHzHoO1Np3w2hOxVYKdXKYTfrPNtkNpwBzJJHsV-dnMqrkngF9qEs_jJDQh3UbpXq_TD5c-TZOFZ0g00Fdn9-eDMs1kNFEze85JVa7nxacYGD3t6kus7CNEu0ZsFEwFPV9VZ6zWf52NRPhfu8sN10QX3P1Zx_PXs89ARWRhIzH8Q8vtSeb-mHFeq9KQ1SI7rQmp7naI5Go354sw3_R6CD0R93e12r5gvE9gnB2KrjlFXmac1Sh1GdwESzmn5EnnfVmnamQDry-qAkXU8rII8_45TUulCfwxzvZ0AxFTpusGp39YB8MfDfgUNTog"

type Message = {
    content: string,
    createdAt: string,
    from: number,
    id: ULID,
    to: number,
}

type MessageForm = {
    conversationId: number,
    to: number,
    content: string
}

const fetchInbox = async () => {
    const response = await fetch("http://localhost:8080/api/conversations")
    const data = await response.json()
    console.log(data)
    return data
}


function App() {

    const clientRef = useRef<Client | null>(null);
    const [connected, setConnected] = useState(false)
    const [messageForm, setMessageForm] = useState<MessageForm>({
        conversationId: 0,
        to: 1234567,
        content: ""
    });

    const [messages, setMessages] = useState<Message[]>([])

    const callback = (frame: Frame) => {
        const message: Message = JSON.parse(frame.body) as Message;
        console.log(message)
        setMessages(prev => [...prev, message])
    }

    useEffect(() => {

        const client = new Client({
            brokerURL: "ws://localhost:8081/ws",
            connectHeaders: {
                Authorization: token
            },
            onConnect: () => {
                setConnected(true);
                const connection = client.subscribe("/user/queue/chats", callback);
                console.log(connection)
            },
            onDisconnect: () => setConnected(false)
        })
        client.activate();
        clientRef.current = client;



        return () => { client.deactivate(); };
    }, [])

    const handleSend = () => {
        if (!clientRef.current?.connected) return;
        clientRef.current.publish({
            destination: "/app/chat",
            body: JSON.stringify(messageForm)
        });
    }

    return (
        <div>
            <form onSubmit={(e) => {e.preventDefault(); handleSend()}}>
                <input type="number" value={messageForm.conversationId} onChange={(e) => {setMessageForm({...messageForm, conversationId: Number(e.target.value)})}}/>
                <input type="number" value={messageForm.to} onChange={(e) => {setMessageForm({...messageForm,  to: Number(e.target.value)})}}/>
                <input type="text" value={messageForm.content} onChange={(e) => {setMessageForm({...messageForm, content: e.target.value})}}/>
                <button type="submit" disabled={!connected}>Submit</button>
            </form>

            <button onClick={fetchInbox}>Fetch Inbox</button>

            <ul>
                {messages.map((message) => (
                    <li key={message.id}>{message.content}</li>
                ))}
            </ul>
        </div>

    );
}

export default App
