import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter, Route, Routes } from "react-router";
import ChatPage from "./chat/ChatPage";
import LoginPage from "./login/LoginPage";
import { UserProvider } from "./UserContext";
import { ChatStoreProvider } from "./state/ChatStore";

createRoot(document.getElementById("root")!).render(
    <StrictMode>
        <UserProvider>
            <ChatStoreProvider>
                <BrowserRouter>
                    <Routes>
                        <Route path="/" element={<LoginPage />} />
                        <Route path="/chat" element={<ChatPage />} />
                    </Routes>
                </BrowserRouter>
            </ChatStoreProvider>
        </UserProvider>
    </StrictMode>,
);
