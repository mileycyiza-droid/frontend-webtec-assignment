
package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String savePrescription(Prescription prescription, UUID appointmentId) {
        Optional<Appointment> appointment = appointmentRepo.findById(appointmentId);

        if (appointment.isEmpty()) {
            return "The appointment with that id does not exist";
        }

        if (appointment.get().getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }

        prescription.setAppointment(appointment.get());
        prescription.setIssuedDate(LocalDate.now());

        prescriptionRepo.save(prescription);

        return "Prescription saved successfully";
    }

    public boolean appointmentExists(UUID appointmentId) {
        return appointmentRepo.existsById(appointmentId);
    }

    public List<Prescription> getPrescriptionsByAppointment(UUID appointmentId) {
        return prescriptionRepo.findByAppointment_Id(appointmentId);
    }

    public List<Prescription> getPatientPrescriptionHistory(String ssNumber) {
        return prescriptionRepo.findPatientPrescriptionHistory(ssNumber);
    }

    public String updatePrescription(UUID id, Prescription prescription) {
        Optional<Prescription> existing = prescriptionRepo.findById(id);

        if (existing.isEmpty()) {
            return "The prescription with that id does not exist";
        }

        Prescription prescriptionToUpdate = existing.get();
        prescriptionToUpdate.setMedicineName(prescription.getMedicineName());
        prescriptionToUpdate.setDosage(prescription.getDosage());
        prescriptionToUpdate.setDurationDays(prescription.getDurationDays());

        prescriptionRepo.save(prescriptionToUpdate);

        return "Prescription updated successfully";
    }

    public String deletePrescription(UUID id) {
        Optional<Prescription> prescription = prescriptionRepo.findById(id);

        if (prescription.isEmpty()) {
            return "The prescription with that id does not exist";
        }

        prescriptionRepo.delete(prescription.get());

        return "Prescription deleted successfully";
    }

    public boolean appointmentHasPrescriptions(UUID appointmentId) {
        return prescriptionRepo.existsByAppointment_Id(appointmentId);
    }
}
