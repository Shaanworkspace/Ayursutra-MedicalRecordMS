package com.medicalrecordms.Service;

import com.medicalrecordms.DTO.Request.MedicalRecordRequestDTO;
import com.medicalrecordms.DTO.Response.MedicalRecordResponseDTO;
import com.medicalrecordms.ENUM.Status;
import com.medicalrecordms.Entity.MedicalRecord;
import com.medicalrecordms.Messaging.MedicalRecordMessageProducer;
import com.medicalrecordms.Repository.MedicalRecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    private final Logger logger = Logger.getLogger(MedicalRecordService.class.getName());

    // Create new medical record
    public MedicalRecord createMedicalRecord(Long patientId, MedicalRecord record) {
        logger.info("createMedicalRecord reached");

        record.setPatientId(patientId);
        // record.setDoctorId(doctorId); // Optional: attach doctor properly in a later step

        logger.info("Patient and doctor assigned");
        return medicalRecordRepository.save(record);
    }

    // Update therapy requirements
    @Transactional
    public MedicalRecord updateTherapies(Long recordId, boolean needTherapy, List<Long> therapyIds) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical Record not found with id " + recordId));

        record.setNeedTherapy(needTherapy);
        if (therapyIds != null) {
            record.setRequiredTherapyIds(therapyIds);
        }

        return medicalRecordRepository.save(record);
    }

    // Get all medical records as DTO list
    public List<MedicalRecordResponseDTO> getAllMedicalRecords() {
        return medicalRecordRepository.findAll()
                .stream()
                .map(this::medicalRecordConvertToMedicalRecordResponseDTO)
                .collect(Collectors.toList());
    }

    // Get record by ID (converted to DTO)
    public MedicalRecordResponseDTO getMedicalRecordById(Long recordId) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical Record not found with ID: " + recordId));
        return medicalRecordConvertToMedicalRecordResponseDTO(record);
    }

    // Get records by patient
    public List<MedicalRecordResponseDTO> getMedicalRecordsByPatient(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId)
                .stream()
                .map(this::medicalRecordConvertToMedicalRecordResponseDTO)
                .collect(Collectors.toList());
    }

    // Get records by doctor
    public List<MedicalRecordResponseDTO> getMedicalRecordsByDoctor(Long doctorId) {
        return medicalRecordRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::medicalRecordConvertToMedicalRecordResponseDTO)
                .collect(Collectors.toList());
    }

    // Convert entity to DTO
    public MedicalRecordResponseDTO medicalRecordConvertToMedicalRecordResponseDTO(MedicalRecord record) {
        return new MedicalRecordResponseDTO(
                record.getId(),
                record.getVisitDate(),
                record.getSymptoms(),
                record.getDiagnosis(),
                record.getPrescribedTreatment(),
                record.getPatientId(),
                record.getDoctorId(),
                record.getTherapistId(),
                record.getCreatedDate(),
                record.getTherapyName(),
                record.getStartDate(),
                record.getEndDate(),
                record.getStatus() != null ? record.getStatus() : null,
                record.getNoOfDays(),
                record.getDoctorNotes(),
                record.getRating()
        );
    }

    // Convert entity to DTO
    public MedicalRecordRequestDTO medicalRecordConvertToMedicalRecordRequestDTO(MedicalRecord record) {
        return new MedicalRecordRequestDTO(
                record.getId(),
                record.getPatientId(),
                record.getDoctorId(),
                record.getVisitDate(),
                record.getSymptoms(),
                record.getAllergies(),
                record.getMedicalHistoryNotes(),
                record.getMedications(),
                record.getFollowUpRequired()
        );
    }

    // Update editable fields in a medical record
    @Transactional
    public MedicalRecord updateMedicalRecord(Long id, MedicalRecord updatedRecord) {
        MedicalRecord existing = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MedicalRecord not found with id: " + id));

        if (updatedRecord.getMedicalHistoryNotes() != null && !updatedRecord.getMedicalHistoryNotes().isEmpty())
            existing.setMedicalHistoryNotes(updatedRecord.getMedicalHistoryNotes());
        if (updatedRecord.getMedications() != null && !updatedRecord.getMedications().isEmpty())
            existing.setMedications(updatedRecord.getMedications());
        if (updatedRecord.getFollowUpRequired() != null && !updatedRecord.getFollowUpRequired().isEmpty())
            existing.setFollowUpRequired(updatedRecord.getFollowUpRequired());

        existing.setNeedTherapy(updatedRecord.isNeedTherapy());

        if (updatedRecord.getRequiredTherapyIds() != null)
            existing.setRequiredTherapyIds(updatedRecord.getRequiredTherapyIds());
        if (updatedRecord.getTherapyPlanId() != null)
            existing.setTherapyPlanId(updatedRecord.getTherapyPlanId());
        if (updatedRecord.getTherapistId() != null)
            existing.setTherapistId(updatedRecord.getTherapistId());
        if (updatedRecord.getDiagnosis() != null && !updatedRecord.getDiagnosis().isEmpty())
            existing.setDiagnosis(updatedRecord.getDiagnosis());
        if (updatedRecord.getPrescribedTreatment() != null && !updatedRecord.getPrescribedTreatment().isEmpty())
            existing.setPrescribedTreatment(updatedRecord.getPrescribedTreatment());
        if (updatedRecord.getDoctorNotes() != null && !updatedRecord.getDoctorNotes().isEmpty())
            existing.setDoctorNotes(updatedRecord.getDoctorNotes());
        if (updatedRecord.getRating() != null)
            existing.setRating(updatedRecord.getRating());
        if (updatedRecord.getStatus() != null)
            existing.setStatus(updatedRecord.getStatus());
        if (updatedRecord.getNoOfDays() != null)
            existing.setNoOfDays(updatedRecord.getNoOfDays());

        return medicalRecordRepository.save(existing);
    }

    // Assign therapist to a record
    @Transactional
    public MedicalRecord assignTherapist(Long recordId, Long therapistId) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical Record not found with id " + recordId));

        record.setTherapistId(therapistId);
        return medicalRecordRepository.save(record);
    }

    // Delete record
    public void deleteMedicalRecord(Long recordId) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical Record not found with ID: " + recordId));
        medicalRecordRepository.delete(record);
    }

    // Create record from patient request DTO
    public MedicalRecord createFromPatientRequest(MedicalRecordRequestDTO dto) {
        MedicalRecord record = MedicalRecord.builder()
                .patientId(dto.getPatientId())
                .doctorId(dto.getDoctorId())
                .visitDate(dto.getVisitDate())
                .symptoms(dto.getSymptoms())
                .allergies(dto.getAllergies())
                .medicalHistoryNotes(dto.getMedicalHistoryNotes())
                .medications(dto.getMedications())
                .followUpRequired(dto.getFollowUpRequired())
                .status(Status.PENDING)
                .build();



        return medicalRecordRepository.save(record);
    }

    // Update record status
    public void updateStatus(Long id, Status newStatus,Long doctorId) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medical Record not found with id: " + id));

        if (!Objects.equals(record.getDoctorId(), doctorId)) {
            throw new IllegalArgumentException("Doctor is not authorized to update this record.");
        }

        record.setStatus(newStatus);

        medicalRecordRepository.save(record);
    }
}