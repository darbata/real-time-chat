type Props = {
    login: string;
    setLogin: (value: string) => void;
    handleSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
};

export function LoginForm({ login, setLogin, handleSubmit }: Props) {
    return (
        <div className="bg-background-300 text-foreground-100 rounded-lg flex justify-center items-center px-8 py-10 w-full max-w-sm">
            <form
                className="flex flex-col gap-6 w-full"
                onSubmit={handleSubmit}
            >
                <div className="flex flex-col gap-2">
                    <label
                        className="text-xs text-muted font-semibold uppercase tracking-wide"
                        htmlFor="login"
                    >
                        Your username
                    </label>

                    <div className="flex relative">
                        <span className="absolute left-3 top-1/2 -translate-y-1/2 text-muted select-none">
                            @
                        </span>
                        <input
                            id="login"
                            className="pl-8 w-full bg-background-200 rounded h-10 text-sm focus:outline-none focus:ring-1 focus:ring-accent-400"
                            type="text"
                            value={login}
                            onChange={(e) => setLogin(e.target.value)}
                            autoComplete="off"
                            required
                        />
                    </div>
                </div>

                <button
                    className="bg-accent-400 hover:bg-accent-300 transition w-full h-10 rounded text-sm font-semibold"
                    type="submit"
                >
                    Continue
                </button>
            </form>
        </div>
    );
}
