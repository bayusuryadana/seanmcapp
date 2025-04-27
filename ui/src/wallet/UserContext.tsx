import { ReactNode, useState, FC, createContext } from "react";

interface Props {
    children: ReactNode
}

export const UserProvider: FC<Props> = ({ children }) => {
    const [userContext, setPassword] = useState<string|null>(null)

    const savePassword = (password: string|null) => {
        setPassword(password)
    }

    return <UserContext.Provider value={{ userContext, savePassword}}>
        {children}
    </UserContext.Provider>
}

export type UserContextType = {
    userContext: string|null;
    savePassword: (password: string|null) => void;
}

export const UserContext = createContext<UserContextType|null>(null);

