import {useEffect, useRef, useState} from "react";
import {Client, type Frame} from "@stomp/stompjs";
import type {ULID} from "ulid";

const token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdWItMDQ5OWZhOTYtODA5Yi00OGZmLTlhZGUtNzk4MGM4ZDljNjk5IiwiaWQiOiIxMjM0NTY3IiwiaWF0IjoxNzc2NjUwOTExLCJleHAiOjE3NzY2NTQ1MTF9.jUHPXhOuNVxfTlWWLtRsE7HnD05kszqsFQurVpySD1Ake7DVG0KEsC41yoQC4_8rjhJvftBfYd7GQV6Eioia-XGsGtIdqu_wr8ncM5mbrzYd_kjJJs-5YJMiygM6XpOckDQsT0kVLpaSAycKYpXIl6a5P2WvS6imwnaRgQ9zkFFHF6pVe0--A-mNXsDJYZ_WRG4b9uVzCePrNv0M3v8FdHwtZW1gfJrVWJn5RRy15Tvs_4zRSaBWvs5A-7YZFbJ84K2NmEt7-oKYwBpmKq85c2NOGv5ZhBfggHuwAUYceIFGkchrk6xnKmu7MbemVaI2be6CvwmBZwUqkEFGzMD_RA"

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
