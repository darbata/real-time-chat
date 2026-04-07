import {useEffect, useRef, useState} from "react";
import {Client} from "@stomp/stompjs";

const token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0LXVzZXIiLCJpZCI6IjEyMzQ1NjciLCJpYXQiOjE3NzU1NzQ1MTYsImV4cCI6MTc3NTU3ODExNn0.HBMCF2z8AW_H8rKB3SNZOvaQX4xaoei2diu0E2r3xsytRWLmjIvNfdNjTvDf65xlqlKcpv1yLHbZzOqKHHDM-osbwuJzHMJudhkv9DyFtq5QrBsqKqpWyRz50fOk3LsC-kU7qrfYFYqA5H9pa18qj8xzmr0VgY6lYdeF-JkhwM2TyZmrhbTJ4-yFZ58xKzaNSlZ-CWhDStkAhivhRxSeHna1Q3ZhOA8BvF-4gGIlBX9aGJFBvPD1MDERc1l3XBeadF8CMZxhkf_WGbp2sShFExM_FFp85XV_j98kTiX9FEmh8zOz9LE5aqC9j07TpELBscyKHhKjLyvYpp7jYiyGBg"

type Message = {
    conversationId: number,
    to: number,
    content: string
}

const callback = (message: any) => {
    console.log("received: ", message.body)
}

function App() {

    const clientRef = useRef<Client | null>(null);
    const [connected, setConnected] = useState(false)
    const [message, setMessage] = useState<Message>({
        conversationId: 0,
        to: 1234567,
        content: ""
    });

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
            body: JSON.stringify(message)
        });
    }

    return (
        <form onSubmit={(e) => {e.preventDefault(); handleSend()}}>
            <input type="number" value={message.conversationId} onChange={(e) => {setMessage({...message, conversationId: Number(e.target.value)})}}/>
            <input type="number" value={message.to} onChange={(e) => {setMessage({...message,  to: Number(e.target.value)})}}/>
            <input type="text" value={message.content} onChange={(e) => {setMessage({...message, content: e.target.value})}}/>
            <button type="submit" disabled={!connected}>Submit</button>
        </form>
    );
}

export default App
