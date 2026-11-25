
package kigali.clinic.rw.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByStatusOrderByAppointmentDateAsc(
            AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            LocalDate startDate, LocalDate endDate);

    boolean existsByDoctor_IdAndAppointmentDateAndStatusNot(
            UUID doctorId, LocalDate date, AppointmentStatus status);

    boolean existsByDoctor_IdAndAppointmentDateAndStatusNotAndIdNot(
            UUID doctorId, LocalDate date, AppointmentStatus status, UUID id);

    List<Appointment> findByDoctor_Id(UUID doctorId);

    List<Appointment> findByPatient_Id(UUID patientId);

    boolean existsByPatient_Id(UUID patientId);

    boolean existsByDoctor_Id(UUID doctorId);

    List<Appointment> findByDoctor_IdAndStatusOrderByAppointmentDateAsc(
            UUID doctorId, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.status = :status AND a.appointmentDate < :date")
    List<Appointment> pendingOverdue(
            @Param("status") AppointmentStatus status,
            @Param("date") LocalDate date);

    @Query("SELECT a.doctor.id, COUNT(a) FROM Appointment a GROUP BY a.doctor.id")
    List<Object[]> appointmentsPerDoctor();

    @Query("SELECT a FROM Appointment a JOIN a.doctor d JOIN d.specializations s " +
            "WHERE LOWER(s.name) = LOWER(:specialization)")
    List<Appointment> bySpecialization(
            @Param("specialization") String specialization);

    Page<Appointment> findByDoctor_Id(
            UUID doctorId,
            Pageable pageable);

    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countAppointmentsByStatus();

    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :newStatus " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentDate = :date " +
            "AND a.status = :oldStatus")
    int cancelAppointmentsByDoctorAndDate(
            @Param("doctorId") UUID doctorId,
            @Param("date") LocalDate date,
            @Param("oldStatus") AppointmentStatus oldStatus,
            @Param("newStatus") AppointmentStatus newStatus);

    List<Appointment> findByDoctor_IdAndAppointmentDateAndStatusNot(
            UUID doctorId,
            LocalDate appointmentDate,
            AppointmentStatus status);

    List<Appointment> findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
            String ssNumber,
            LocalDate date,
            List<AppointmentStatus> statuses);

    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.status = :status AND a.appointmentDate < :date")
    int deleteCancelledAppointmentsBefore(
            @Param("status") AppointmentStatus status,
            @Param("date") LocalDate date);
}
