
package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.DoctorService;

@RestController
@RequestMapping("/api")
public class DoctorController {

    @Autowired
    private DoctorService docServe;

    @PostMapping("/doctor/save")
    public ResponseEntity<?> saveDoctor(
            @RequestBody Doctor doctor,
            @RequestParam UUID officeId) {

        String message = docServe.saveDoctor(doctor, officeId);

        if (message.equals("Doctor is saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals("Office not found")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/doctor/all")
    public ResponseEntity<?> getAllDoctors() {
        return new ResponseEntity<>(docServe.getAllDoctors(), HttpStatus.OK);
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable UUID id) {
        Optional<Doctor> doctor = docServe.getDoctorById(id);

        if (doctor.isPresent()) {
            return new ResponseEntity<>(doctor.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/doctor/update/{id}")
    public ResponseEntity<?> updateDoctor(
            @PathVariable UUID id,
            @RequestBody Doctor doctor) {

        String message = docServe.updateDoctor(id, doctor);

        if (message.equals("Doctor is updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/doctor/delete/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable UUID id) {
        String message = docServe.deleteDoctor(id);

        if (message.equals("Doctor is deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals("Doctor not found")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @PutMapping("/doctor/add-specialization")
    public ResponseEntity<?> addSpecializationToDoctor(
            @RequestParam UUID doctorId,
            @RequestParam UUID specializationId) {

        String message =
                docServe.addSpecializationToDoctor(doctorId, specializationId);

        if (message.equals("Specialization is added to doctor successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals("Doctor not found")
                || message.equals("Specialization not found")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @PutMapping("/doctor/remove-specialization")
    public ResponseEntity<?> removeSpecializationFromDoctor(
            @RequestParam UUID doctorId,
            @RequestParam UUID specializationId) {

        String message =
                docServe.removeSpecializationFromDoctor(doctorId, specializationId);

        if (message.equals("Specialization is removed from doctor successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals("Doctor not found")
                || message.equals("Specialization not found")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @PutMapping("/doctors/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<?> addSpecializationForQuiz2(
            @PathVariable UUID doctorId,
            @PathVariable UUID specializationId) {

        String message =
                docServe.addSpecializationToDoctor(doctorId, specializationId);

        if (message.equals("Specialization is added to doctor successfully")) {
            return new ResponseEntity<>(
                    "Specialization added to the doctor", HttpStatus.OK);
        } else if (message.equals("Doctor not found")) {
            return new ResponseEntity<>(
                    "The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        } else if (message.equals("Specialization not found")) {
            return new ResponseEntity<>(
                    "That specialization does not exist", HttpStatus.NOT_FOUND);
        } else if (message.equals("Doctor already has this specialization")) {
            return new ResponseEntity<>(
                    "The doctor already has that specialization", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/doctors/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<?> removeSpecializationForQuiz2(
            @PathVariable UUID doctorId,
            @PathVariable UUID specializationId) {

        String message =
                docServe.removeSpecializationFromDoctor(doctorId, specializationId);

        if (message.equals("Specialization is removed from doctor successfully")) {
            return new ResponseEntity<>(
                    "Specialization removed from the doctor", HttpStatus.OK);
        } else if (message.equals("Doctor not found")) {
            return new ResponseEntity<>(
                    "The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        } else if (message.equals("Specialization not found")) {
            return new ResponseEntity<>(
                    "That specialization does not exist", HttpStatus.NOT_FOUND);
        } else if (message.equals("Doctor does not have this specialization")) {
            return new ResponseEntity<>(
                    "The doctor does not have that specialization", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PatchMapping("/doctors/{doctorId}/office")
    public ResponseEntity<?> changeDoctorOffice(
            @PathVariable UUID doctorId,
            @RequestParam int officeNumber) {

        String message = docServe.changeDoctorOffice(doctorId, officeNumber);

        if (message.equals("Office changed successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals("The doctor with that id does not exist")
                || message.equals("The office with that office number does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else if (message.equals("That office is taken by other doctor")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/doctors/by-specialization")
    public ResponseEntity<?> getDoctorsBySpecialization(@RequestParam String name) {
        List<Doctor> doctors = docServe.getDoctorsBySpecialization(name);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping("/doctors/without-office")
    public ResponseEntity<?> getDoctorsWithoutOffice() {
        List<Doctor> doctors = docServe.getDoctorsWithoutOffice();
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping("/doctors/{doctorId}/agenda")
    public ResponseEntity<?> getDoctorAgenda(
            @PathVariable UUID doctorId,
            @RequestParam LocalDate date) {

        Optional<Doctor> doctor = docServe.getDoctorById(doctorId);

        if (doctor.isEmpty()) {
            return new ResponseEntity<>(
                    "The doctor with that id does not exist",
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                docServe.getDoctorAgenda(doctorId, date),
                HttpStatus.OK);
    }

    @GetMapping("/doctors/search")
    public ResponseEntity<?> searchDoctorsByName(@RequestParam String name) {
        List<Doctor> doctors = docServe.searchDoctorsByName(name);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }
}
