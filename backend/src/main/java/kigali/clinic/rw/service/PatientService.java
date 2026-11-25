
package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patRepo;

    @Autowired
    private AppointmentRepository appRepo;

    @Autowired
    private DoctorRepository docRepo;

    public String savePatient(Patient patient) {
        patRepo.save(patient);
        return "Patient is saved successfully";
    }

    public List<Patient> getPatientsByLastName(String lastName) {
        return patRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    public List<Patient> getAllPatients() {
        return patRepo.findAll();
    }

    public Optional<Patient> getPatientById(UUID id) {
        return patRepo.findById(id);
    }

    public Optional<Patient> getPatientBySsNumber(String ssNumber) {
        return patRepo.findBySsNumber(ssNumber);
    }

    public String updatePatient(UUID id, Patient patient) {
        Optional<Patient> existingPatient = patRepo.findById(id);

        if (existingPatient.isEmpty()) {
            return "Patient not found";
        }

        Patient patientToUpdate = existingPatient.get();

        patientToUpdate.setFirstName(patient.getFirstName());
        patientToUpdate.setLastName(patient.getLastName());
        patientToUpdate.setDateOfBirth(patient.getDateOfBirth());
        patientToUpdate.setSsNumber(patient.getSsNumber());

        patRepo.save(patientToUpdate);

        return "Patient is updated successfully";
    }

    public String deletePatient(UUID id) {
        if (!patRepo.existsById(id)) {
            return "Patient not found";
        }

        if (appRepo.existsByPatient_Id(id)) {
            return "Patient has appointments and cannot be deleted";
        }

        patRepo.deleteById(id);
        return "Patient is deleted successfully";
    }

    public List<Patient> getPatientsOfDoctor(UUID doctorId) {
        return patRepo.findPatientsByDoctor(doctorId);
    }

    public boolean doctorExists(UUID doctorId) {
        return docRepo.existsById(doctorId);
    }

    public List<Patient> getFrequentPatients(long min) {
        return patRepo.findFrequentPatients(min);
    }

    public List<Appointment> getUpcomingAppointments(String ssNumber) {
        return appRepo
                .findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                        ssNumber,
                        LocalDate.now(),
                        List.of(
                                AppointmentStatus.SCHEDULED,
                                AppointmentStatus.CONFIRMED));
    }
}
