import {useState} from "react";
import {useNavigate} from "react-router";
import {useUser} from "../UserContext.tsx";

export default function LoginPage() {
    const [login, setLogin] = useState("");
    const navigate = useNavigate();
    const {username, setUsername} = useUser();

    const handleSubmit = (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setUsername(login);
    }

    if (username != "") navigate("/chat")

    return  (
        <main className="bg-blue-200">
            <form onSubmit={(e) => handleSubmit(e)}>
                <label htmlFor="login">Login</label>
                <input type="text" value={login} onChange={(e) => {setLogin(e.target.value)}}/>
            </form>
        </main>
    )
}