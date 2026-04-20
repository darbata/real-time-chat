// UserContext.tsx
import { createContext, useContext, useState, } from 'react';
import type { ReactNode } from "react";

type UserContextValue = {
    username: string;
    setUsername: (name: string) => void;
    clearUsername: () => void;
};

const UserContext = createContext<UserContextValue | undefined>(undefined);

export function UserProvider({ children }: { children: ReactNode }) {
    const [username, setUsername] = useState('');
    const clearUsername = () => setUsername('');

    return (
        <UserContext.Provider value={{ username, setUsername, clearUsername }}>
            {children}
        </UserContext.Provider>
    );
}

// eslint-disable-next-line react-refresh/only-export-components
export function useUser() {
    const ctx = useContext(UserContext);
    if (ctx === undefined) {
        throw new Error('useUser must be used within a UserProvider');
    }
    return ctx;
}