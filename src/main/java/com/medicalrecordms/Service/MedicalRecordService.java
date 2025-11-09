package com.medicalrecordms.Service;


import com.medicalrecordms.DTO.Response.MedicalRecordResponseDTO;
import com.medicalrecordms.Entity.MedicalRecord;
import com.medicalrecordms.Repository.MedicalRecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    Logger logger = Logger.getLogger(MedicalRecordService.class.getName());
    /**
     * Creates a new Medical Record for a patient with a linked doctor.
     */
    public MedicalRecord createMedicalRecord(Long patientId, MedicalRecord record) {
        logger.info("createMedicalRecord reached ");
        // Fetch Patient

        //  Fetch Doctor

        //  Attach properly
        record.setPatientId(null);
        record.setDoctorId(null);
        logger.info("Set the patient and doctor");
        return medicalRecordRepository.save(record);
    }
    @Transactional
    public MedicalRecord updateTherapies(Long recordId, boolean needTherapy, List<Long> therapyIds) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() ->
                        new IllegalArgumentException("❌ Medical Record not found with id " + recordId));

        record.setNeedTherapy(needTherapy);

        if (therapyIds != null) {
            List<Long> therapies = List.of();

            record.setRequiredTherapyIds(therapies);
        }

        return medicalRecordRepository.save(record);
    }

    public List<MedicalRecordResponseDTO> getAllMedicalRecords() {
        List<MedicalRecord> records = medicalRecordRepository.findAll();
        // Yeh sirf collect karega
        return records.stream()
                .map(this::medicalRecordToDto)   // 👈 converter method call
                .collect(Collectors.toList());
    }

    // Converter: Entity -> DTO
    public MedicalRecordResponseDTO medicalRecordToDto(MedicalRecord record) {
        return new MedicalRecordResponseDTO(
                record.getId(),
                record.getVisitDate(),
                record.getSymptoms(),
                record.getDiagnosis(),
                record.getPrescribedTreatment(),
                record.getPatientId() != null ? record.getPatientId() : null,
                record.getDoctorId() != null ? record.getDoctorId() : null,
                record.getTherapistId() != null ? record.getTherapistId() : null,

                record.getCreatedDate(),
                record.getTherapyName(),
                record.getStartDate(),
                record.getEndDate(),
                record.getStatus() != null ? record.getStatus().name() : null,
                record.getNoOfDays(),
                record.getDoctorNotes(),
                record.getRating()
        );
    }

    /**
     *  Get a record by ID
     */
    public MedicalRecord getMedicalRecordById(Long recordId) {
        return medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical Record not found with ID: " + recordId));
    }

    /**
     *  Get all records for a patient
     */
    public List<MedicalRecord> getMedicalRecordsByPatient(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }

    /**
     *  Get all records for a doctor
     */
    public List<MedicalRecord> getMedicalRecordsByDoctor(Long doctorId) {
        return medicalRecordRepository.findByDoctorId(doctorId);
    }

    @Transactional
    public MedicalRecord updateMedicalRecord(Long id, MedicalRecord updatedRecord) {
        MedicalRecord existing = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MedicalRecord not found with id: " + id));

        // =========================================================================
        // Business Rule: DO NOT ALLOW CHANGING PATIENT OR DOCTOR VIA THIS UPDATE
        // =========================================================================
        // This means the incoming 'updatedRecord' JSON can have patient/doctor fields,
        // but our logic will ignore them to prevent accidental or unauthorized changes.
        // The doctor who initially created the record or is currently linked to it
        // should not be changeable here. Patient is also fixed.
        // =========================================================================

        // Only overwrite if value is not null and not empty for Strings
        if (updatedRecord.getMedicalHistoryNotes() != null && !updatedRecord.getMedicalHistoryNotes().isEmpty())
            existing.setMedicalHistoryNotes(updatedRecord.getMedicalHistoryNotes());

        if (updatedRecord.getMedications() != null && !updatedRecord.getMedications().isEmpty())
            existing.setMedications(updatedRecord.getMedications());

        if (updatedRecord.getFollowUpRequired() != null && !updatedRecord.getFollowUpRequired().isEmpty())
            existing.setFollowUpRequired(updatedRecord.getFollowUpRequired());

        // Booleans update directly (false is a valid update)
        existing.setNeedTherapy(updatedRecord.isNeedTherapy()); // This always updates if provided

        // Lists (ManyToMany)
        // If an empty list is sent, it will clear the requiredTherapy.
        // If you want to prevent clearing on empty list, use:
        // if (updatedRecord.getRequiredTherapy() != null && !updatedRecord.getRequiredTherapy().isEmpty())
        if (updatedRecord.getRequiredTherapyIds() != null) // Allows clearing the list if [] is sent
            existing.setRequiredTherapyIds(updatedRecord.getRequiredTherapyIds());
        // Note: For ManyToMany, complex updates (add/remove specific items) would need custom logic
        // This simply replaces the whole list.

        // OneToOne / ManyToOne object fields
        if (updatedRecord.getTherapyPlanId() != null) // Allows setting/clearing TherapyPlan
            existing.setTherapyPlanId(updatedRecord.getTherapyPlanId());

        if (updatedRecord.getTherapistId() != null) // Allows setting/clearing Therapist
            existing.setTherapistId(updatedRecord.getTherapistId());

        // New fields
        // Assuming diagnosis, prescribedTreatment are also doctor-updatable
        if (updatedRecord.getDiagnosis() != null && !updatedRecord.getDiagnosis().isEmpty())
            existing.setDiagnosis(updatedRecord.getDiagnosis());
        if (updatedRecord.getPrescribedTreatment() != null && !updatedRecord.getPrescribedTreatment().isEmpty())
            existing.setPrescribedTreatment(updatedRecord.getPrescribedTreatment());
        if (updatedRecord.getDoctorNotes() != null && !updatedRecord.getDoctorNotes().isEmpty())
            existing.setDoctorNotes(updatedRecord.getDoctorNotes());
        if (updatedRecord.getRating() != null) // Rating can be null or a double
            existing.setRating(updatedRecord.getRating());

        // Status and NoOfDays
        if (updatedRecord.getStatus() != null)
            existing.setStatus(updatedRecord.getStatus());
        if (updatedRecord.getNoOfDays() != null)
            existing.setNoOfDays(updatedRecord.getNoOfDays());


        // --- Important: Auto-update `updatedAt` field if it exists in MedicalRecord ---
        // If your MedicalRecord entity has an `updatedAt` field with @PreUpdate, it will be handled automatically.
        // Otherwise, you might set it here:
        // existing.setUpdatedAt(LocalDateTime.now());

        return medicalRecordRepository.save(existing);
    }
    @Transactional
    public MedicalRecord assignTherapist(Long recordId, Long therapistId) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("❌ Medical Record not found with id " + recordId));

        Long therapist = null;

        record.setTherapistId(therapist);

        return medicalRecordRepository.save(record);
    }

    /**
     *  Delete a record
     */
    public void deleteMedicalRecord(Long recordId) {
        MedicalRecord record = getMedicalRecordById(recordId);
        medicalRecordRepository.delete(record);
    }
}