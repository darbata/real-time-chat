import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useUser } from "../UserContext";
import { LoginForm } from "./components/LoginForm";

export default function LoginPage() {
    const [login, setLogin] = useState("");
    const navigate = useNavigate();
    const { username, setUsername } = useUser();

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!login.trim()) return;
        setUsername(login.trim());
    };

    useEffect(() => {
        if (username !== "") navigate("/chat");
    }, [username, navigate]);

    return (
        <main className="w-full h-full flex flex-col justify-center items-center bg-background-400 text-foreground-100 gap-6 px-6">

            <LoginForm
                login={login}
                setLogin={setLogin}
                handleSubmit={handleSubmit}
            />

        </main>
    );
}
