
package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.PatientRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class DashboardService {

    @Autowired
    private PatientRepository patRepo;

    @Autowired
    private DoctorRepository docRepo;

    @Autowired
    private OfficeRepository officeRepo;

    @Autowired
    private AppointmentRepository appRepo;

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    public Map<String, Long> getDashboardSummary() {
        Map<String, Long> summary = new LinkedHashMap<>();

        summary.put("patients", patRepo.count());
        summary.put("doctors", docRepo.count());
        summary.put("freeOffices", (long) officeRepo.findFreeOffices().size());
        summary.put("appointmentsToday",
                (long) appRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                        LocalDate.now(), LocalDate.now()).size());
        summary.put("scheduled",
                (long) appRepo.findByStatusOrderByAppointmentDateAsc(
                        AppointmentStatus.SCHEDULED).size());
        summary.put("prescriptions", prescriptionRepo.count());

        return summary;
    }
}
