import {useEffect, useState} from "react";
import {useNavigate} from "react-router";
import {useUser} from "../UserContext.tsx";
import {LoginForm} from "./components/LoginForm.tsx";

export default function LoginPage() {

    const [login, setLogin] = useState("");
    const navigate = useNavigate();
    const {username, setUsername} = useUser();

    const handleSubmit = (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setUsername(login);
    }

    // if user identified go to chat page
    useEffect(() => {
        if (username != "") navigate("/chat")
    }, [username, navigate])

    return  (
        <main className="w-full h-full flex justify-center items-center bg-background-400 text-foreground-100">
            <LoginForm login={login} setLogin={setLogin} handleSubmit={handleSubmit} />
        </main>
    )
}