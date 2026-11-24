
import axios from 'axios'
import { useEffect, useState } from 'react'

function OfficeList() {
    const [offices, setOffices] = useState([])
    const [searchTerm, setSearchTerm] = useState("")

    async function getOffices() {
        try {
            const response = await axios.get(
                'http://localhost:8082/api/offices/all'
            )

            setOffices(response.data)

        } catch (error) {
            alert("Failed to retrieve offices: " + error.message)
        }
    }

    useEffect(() => {
        getOffices()
    }, [])

    async function deleteOffice(id) {
        const confirmDelete = window.confirm(
            "Are you sure you want to delete this office?"
        )

        if (!confirmDelete) {
            return
        }

        try {
            await axios.delete(
                `http://localhost:8082/api/offices/delete/${id}`
            )

            alert("Office deleted successfully")

            getOffices()

        } catch (error) {
            alert("Failed to delete office: " + error.message)
        }
    }

    const filteredOffices = offices.filter(office =>
        office.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        String(office.officeNumber).includes(searchTerm)
    )

    return (
        <div>
            <h1>AVAILABLE OFFICES</h1>

            <input
                type="text"
                placeholder="Search by office name or number"
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
            />

            <table>
                <thead>
                    <tr>
                        <th>Office Number</th>
                        <th>Office Name</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>
                    {filteredOffices.map(office => (
                        <tr key={office.id}>
                            <td>{office.officeNumber}</td>
                            <td>{office.name}</td>
                            <td>
                                <button disabled>
                                    Update
                                </button>

                                <button onClick={() => deleteOffice(office.id)}>
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {filteredOffices.length === 0 && (
                <p>No offices found.</p>
            )}
        </div>
    )
}

export default OfficeList
