import { createContext, useContext, useEffect, useState } from "react";
import type { ReactNode } from "react";

type UserContextValue = {
    username: string;
    setUsername: (name: string) => void;
    clearUsername: () => void;
};

const UserContext = createContext<UserContextValue | undefined>(undefined);

const STORAGE_KEY = "client.username";

export function UserProvider({ children }: { children: ReactNode }) {
    const [username, setUsernameState] = useState(() => {
        if (typeof window === "undefined") return "";
        return window.localStorage.getItem(STORAGE_KEY) ?? "";
    });

    useEffect(() => {
        if (username) window.localStorage.setItem(STORAGE_KEY, username);
        else window.localStorage.removeItem(STORAGE_KEY);
    }, [username]);

    const setUsername = (name: string) => setUsernameState(name);
    const clearUsername = () => setUsernameState("");

    return (
        <UserContext.Provider value={{ username, setUsername, clearUsername }}>
            {children}
        </UserContext.Provider>
    );
}

export function useUser() {
    const ctx = useContext(UserContext);
    if (ctx === undefined) {
        throw new Error("useUser must be used within a UserProvider");
    }
    return ctx;
}
