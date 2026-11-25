
package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kigali.clinic.rw.domain.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    List<Prescription> findByAppointment_Id(UUID appointmentId);

    @Query("SELECT p FROM Prescription p " +
           "WHERE p.appointment.patient.ssNumber = :ss " +
           "ORDER BY p.issuedDate DESC")
    List<Prescription> findPatientPrescriptionHistory(@Param("ss") String ss);

    boolean existsByAppointment_Id(UUID appointmentId);
}
