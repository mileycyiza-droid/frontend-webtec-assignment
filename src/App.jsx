
import { useState } from 'react'
import './App.css'
import LibraryFooter from './components/LibraryFooter'
import LibraryHeader from './components/LibraryHeader'
import Office from './components/Office'
import OfficeList from './components/OfficeList'

function App() {
    const [activePage, setActivePage] = useState("home")

    return (
        <div>
            <LibraryHeader />

            <nav>
                <button onClick={() => setActivePage("home")}>
                    Home
                </button>

                <button onClick={() => setActivePage("offices")}>
                    Available Offices
                </button>
            </nav>

            <main>
                {activePage === "home" && (
                    <Office />
                )}

                {activePage === "offices" && (
                    <OfficeList />
                )}
            </main>

            <LibraryFooter />
        </div>
    )
}

export default App
