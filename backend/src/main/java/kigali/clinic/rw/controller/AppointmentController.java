
package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.service.AppointmentService;
import kigali.clinic.rw.service.PatientService;
import kigali.clinic.rw.service.PrescriptionService;

@RestController
@RequestMapping(value = {"/api/appointment", "/api/appointments"})
public class AppointmentController {

    @Autowired
    private AppointmentService appServe;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PatientService patientService;

    @PostMapping(value = "/save")
    public ResponseEntity<?> saveAppointment(
            @RequestBody Appointment appointment,
            @RequestParam UUID doctorId,
            @RequestParam UUID patientId) {

        if (appointment.getAppointmentDate() == null) {
            return new ResponseEntity<>(
                    "Appointment date is required",
                    HttpStatus.BAD_REQUEST);
        }

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            return new ResponseEntity<>(
                    "Appointment date cannot be in the past",
                    HttpStatus.BAD_REQUEST);
        }

        if (appServe.doctorHasActiveAppointmentOnDate(
                doctorId, appointment.getAppointmentDate())) {
            return new ResponseEntity<>(
                    "Doctor is already booked on that date",
                    HttpStatus.CONFLICT);
        }

        String message = appServe.saveAppointment(
                appointment, doctorId, patientId);

        if (message.equals("Appointment is saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals("Appointment date is required")) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        } else if (message.equals("Appointment date cannot be in the past")) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        } else if (message.equals("Doctor not found")
                || message.equals("Patient not found")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeAppointmentStatus(
            @PathVariable UUID id,
            @RequestParam AppointmentStatus status) {

        String message = appServe.changeAppointmentStatus(id, status);

        if (message.equals("Appointment status updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals(
                "The appointment with that id does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable UUID id,
            @RequestParam String newDate) {

        LocalDate date;

        try {
            date = LocalDate.parse(newDate);
        } catch (DateTimeParseException ex) {
            return new ResponseEntity<>(
                    "Invalid date format. Use yyyy-MM-dd",
                    HttpStatus.BAD_REQUEST);
        }

        String message = appServe.rescheduleAppointment(id, date);

        if (message.equals("Appointment rescheduled successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals(
                "The appointment with that id does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else if (message.equals(
                "Doctor is already booked on that date")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/by-status")
    public ResponseEntity<?> getAppointmentsByStatus(
            @RequestParam AppointmentStatus status) {

        List<Appointment> appointments =
                appServe.getAppointmentsByStatus(status);

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping(value = "/between")
    public ResponseEntity<?> getAppointmentsBetween(
            @RequestParam String start,
            @RequestParam String end) {

        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);

            List<Appointment> appointments =
                    appServe.getAppointmentsBetween(startDate, endDate);

            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (DateTimeParseException ex) {
            return new ResponseEntity<>(
                    "Invalid date format. Use yyyy-MM-dd",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllAppointments() {
        List<Appointment> appointments = appServe.getAllAppointments();
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping(value = "/stats/by-status")
    public ResponseEntity<?> getAppointmentStatsByStatus() {
        List<Object[]> stats = appServe.getAppointmentStatsByStatus();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @PatchMapping(value = "/cancel-day")
    public ResponseEntity<?> cancelAppointmentsByDoctorAndDate(
            @RequestParam UUID doctorId,
            @RequestParam String date) {

        try {
            LocalDate appointmentDate = LocalDate.parse(date);
            String message = appServe.cancelAppointmentsByDoctorAndDate(
                    doctorId, appointmentDate);

            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (DateTimeParseException ex) {
            return new ResponseEntity<>(
                    "Invalid date format. Use yyyy-MM-dd",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable UUID id) {
        Optional<Appointment> appointment = appServe.getAppointmentById(id);

        if (appointment.isPresent()) {
            return new ResponseEntity<>(appointment.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(
                "Appointment not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/by-doctor/{doctorId}")
    public ResponseEntity<?> getAppointmentsByDoctor(
            @PathVariable UUID doctorId) {

        List<Appointment> appointments =
                appServe.getAppointmentsByDoctor(doctorId);

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping(value = "/by-patient/{patientId}")
    public ResponseEntity<?> getAppointmentsByPatient(
            @PathVariable UUID patientId) {

        List<Appointment> appointments =
                appServe.getAppointmentsByPatient(patientId);

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping(value = "/patients/{ssNumber}/upcoming")
    public ResponseEntity<?> getUpcomingAppointmentsByPatient(
            @PathVariable String ssNumber) {

        Optional<Patient> patient =
                patientService.getPatientBySsNumber(ssNumber);

        if (patient.isEmpty()) {
            return new ResponseEntity<>(
                    "That patient with that ssnumber does not exist",
                    HttpStatus.NOT_FOUND);
        }

        List<Appointment> appointments =
                appServe.getUpcomingAppointmentsByPatient(ssNumber);

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable UUID id,
            @RequestBody Appointment appointment,
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) UUID patientId) {

        String message = appServe.updateAppointment(
                id, appointment, doctorId, patientId);

        if (message.equals("Appointment is updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals("Appointment date is required")) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        } else if (message.equals("Appointment not found")
                || message.equals("Doctor not found")
                || message.equals("Patient not found")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id) {

        Optional<Appointment> appointment = appServe.getAppointmentById(id);

        if (appointment.isEmpty()) {
            return new ResponseEntity<>(
                    "Appointment not found", HttpStatus.NOT_FOUND);
        }

        if (prescriptionService.appointmentHasPrescriptions(id)) {
            return new ResponseEntity<>(
                    "That appointment has prescriptions, delete them first",
                    HttpStatus.CONFLICT);
        }

        String message = appServe.deleteAppointment(id);

        if (message.equals("Appointment is deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/pending/doctor/{doctorId}")
    public ResponseEntity<?> getPendingAppointmentsOfDoctor(
            @PathVariable UUID doctorId) {

        List<Appointment> appointments =
                appServe.getPendingAppointmentsOfDoctor(doctorId);

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping(value = "/pending/overdue")
    public ResponseEntity<?> getOverduePendingAppointments() {
        List<Appointment> appointments =
                appServe.getOverduePendingAppointments();

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping(value = "/count-per-doctor")
    public ResponseEntity<?> getAppointmentsPerDoctor() {
        List<Object[]> results = appServe.getAppointmentsPerDoctor();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping(value = "/by-specialization")
    public ResponseEntity<?> getAppointmentsBySpecialization(
            @RequestParam String name) {

        List<Appointment> appointments =
                appServe.getAppointmentsBySpecialization(name);

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping(value = "/page/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorAppointmentsPage(
            @PathVariable UUID doctorId,
            @RequestParam(defaultValue = "0") int page) {

        Page<Appointment> appointments =
                appServe.getDoctorAppointmentsPage(doctorId, page);

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<?> getAppointmentsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "appointmentDate,desc") String sort) {

        String[] sortParams = sort.split(",");

        Sort sorting = Sort.by(
                Sort.Direction.fromString(sortParams[1]),
                sortParams[0]);

        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<Appointment> appointments =
                appServe.getAppointmentsPage(pageable);

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @DeleteMapping("/cancelled-before")
    public ResponseEntity<?> deleteCancelledAppointmentsBefore(
            @RequestParam String date) {

        try {
            LocalDate appointmentDate = LocalDate.parse(date);

            String message =
                    appServe.deleteCancelledAppointmentsBefore(appointmentDate);

            return new ResponseEntity<>(message, HttpStatus.OK);

        } catch (DateTimeParseException ex) {
            return new ResponseEntity<>(
                    "Invalid date format. Use yyyy-MM-dd",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
