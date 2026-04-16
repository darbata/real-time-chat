import {useEffect, useRef, useState} from "react";
import {Client, type Frame} from "@stomp/stompjs";
import type {ULID} from "ulid";

const token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0LXVzZXIiLCJpZCI6IjEyMzQ1NjciLCJpYXQiOjE3NzU2MTg2NDksImV4cCI6MTc3NTYyMjI0OX0.BlVjoaAo0_ISe5yTHZjZxIE2l-pXaOHDwZZ5k7C6oHo6D2rga9N6fW5hK0bmpqyP0luVLDgIGtI-_NsR2luIMKOVbixJ299olUjxtsq9iCpDjHTCZop0rzhgAPvzqLclbn9oJOgFdcg6Rg8bIH74DHO_D4qcTMlSKjZgpY4jWc3-DsoKw8PdKcVEUyNbwtzTOZRd6d0W6bvnb2ch-pfisziQgZQr8zd3qOkm0IDtRDAUe9idjNDoSn8pdczCA6paTV-arhHwYVRzjPKV8HxeoYdwcqYUhuvCh-fY58KCN_i4K8gpRygyrJQnOsiix7eBiwQ8RoSGVxS76VMisOkdCQ"

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
