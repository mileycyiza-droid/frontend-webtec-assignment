import axios from 'axios'
import { useState } from 'react'
import LibraryButton from './LibraryButton'

function Office() {
    const [officeName, setOfficeName] = useState("");
    const [officeNumber, setOfficeNumber] = useState("");

    async function saveOffice() {
        try {
            const response = await axios.post('http://localhost:8082/api/offices/save', {
                name: officeName,
                officeNumber: Number(officeNumber),
            })
            if (response.status == 200) {
                alert("The office is saved successfully")
            } else {
                alert("office not saved")
            }
        } catch (error) {
            alert("office not saved")
        }
    }

    return (
        <div>
            <h1>SAVE OFFICE</h1>
            <label>Office Name:</label>
            <input type='text' onChange={e => setOfficeName(e.target.value)} />
            <label>Office Number:</label>
            <input type='text' onChange={e => setOfficeNumber(e.target.value)} />
            <LibraryButton buttonLabel="SAVE" onClickButton={saveOffice} />
        </div>
    )
}

export default Office