import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import {BrowserRouter, Route, Routes} from 'react-router'
import ChatPage from "./chat/ChatPage.tsx";
import LoginPage from "./login/LoginPage.tsx";
import {UserProvider} from "./UserContext.tsx";

createRoot(document.getElementById('root')!).render(
  <StrictMode>
      <UserProvider>
          <BrowserRouter>
              <Routes>
                  <Route path={"/"} element={<LoginPage />} />
                  <Route path={"/chat"} element={<ChatPage />} />
              </Routes>
          </BrowserRouter>
      </UserProvider>
  </StrictMode>,
)
