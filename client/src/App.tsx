import {useEffect, useRef, useState} from "react";
import {Client, type Frame} from "@stomp/stompjs";
import type {ULID} from "ulid";

const token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0LXVzZXIiLCJpZCI6IjEyMzQ1NjciLCJpYXQiOjE3NzU1Nzg3MDMsImV4cCI6MTc3NTU4MjMwM30.iiMoWsm44KgzgQF0L3sh6JBH1Nni9HoywKku4pGhdXcRKlOLsHY2FJKfLiIHd1JcSHk3uL7uKJ1r9nYU3Z1ubAEdKwsx8NQCKU-6tvzylcQbjlv3nq9huefsZK9-xzuL8_nQ0T4pdbsvEe4--kR9MH2qeeAgRTXVsQ6pQYvLvoWGE3vE_zIuiNSAgsvl_pDBvbaCotMBUvtNiIU-J-nIzd81rDUlXKk8H1OhTlhqksOjuCdzGv4cte4oWGp5OmwyW4TKVBwizcQm0TPh9bIbGq3dtUVBOkhBsoSkwHts5S5W4yD2L5VSZThSNbOd3UEoHVuRMethez_ITF2dy698kQ"

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
            brokerURL: "ws://localhost:8080/ws",
            connectHeaders: {
                Authorization: token
            },
            onConnect: () => {
                setConnected(true);
                client.subscribe("/user/1234567/chats", callback);
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
            <ul>
                {messages.map((message) => (
                    <li key={message.id}>{message.content}</li>
                ))}
            </ul>
        </div>

    );
}

export default App
