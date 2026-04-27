export function LoginForm(

    {
        login,
        setLogin,
        handleSubmit
    } :
    {
        login: string;
        setLogin: (value: string) => void;
        handleSubmit: (e: React.SubmitEvent<HTMLFormElement>) => void;
    }

) {
    return (
        <div className="bg-background-300 text-foreground-100 aspect-[6/7] rounded flex justify-center items-center px-8 py-24">
            <form className="flex flex-col justify-evenly w-full h-full" onSubmit={handleSubmit}>
                <div className="flex flex-col gap-2">

                    <label className="text-xs text-muted font-semibold" htmlFor="login">YOUR USERNAME</label>

                    <div className="flex relative">

                        <span className="absolute left-2 top-1 text-muted select-none">@</span>

                        <input
                            className="pl-7 w-full bg-background-200 rounded h-8"
                            type="text" value={login}
                            onChange={(e) => {setLogin(e.target.value)}}
                            required
                        />
                    </div>

                </div>

                <button
                    className="bg-accent-400 w-full h-10 rounded"
                    type="submit"
                >
                    Continue
                </button>

            </form>
        </div>
    )
}