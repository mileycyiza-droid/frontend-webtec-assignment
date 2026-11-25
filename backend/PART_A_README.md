# Clinic JPA & JPQL Quiz - Part A

Implemented from the supplied quiz requirements.

## Part A endpoints

### A1 - Patients by last name
GET /api/patients/by-last-name?lastName=uwase

Case-insensitive and sorted by first name ascending.

### A2 - Appointments by status
GET /api/appointments/by-status?status=SCHEDULED

The status is the AppointmentStatus enum and results are ordered by appointment date ascending.

### A3 - Appointments between dates
GET /api/appointments/between?start=2026-10-01&end=2026-10-31

Both dates are inclusive. The controller parses the String parameters into LocalDate.

### A4 - No double booking
POST /api/appointments/save?doctorId={doctorId}&patientId={patientId}

If the doctor already has an appointment on that date whose status is not CANCELLED,
the API returns HTTP 409 with:

Doctor is already booked on that date

## Important
The original singular CRUD paths are preserved as aliases, while the plural quiz paths
required by Part A are also available.

AppointmentStatus includes SCHEDULED, CONFIRMED, COMPLETED and CANCELLED.
PENDING is retained only for compatibility with existing records/code in the original project.
New appointments created by the save endpoint are assigned SCHEDULED.
