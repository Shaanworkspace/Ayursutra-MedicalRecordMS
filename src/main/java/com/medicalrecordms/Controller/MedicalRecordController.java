package com.medicalrecordms.Controller;

import com.medicalrecordms.DTO.Request.MedicalRecordRequestDTO;
import com.medicalrecordms.DTO.Request.TherapyUpdateRequest;
import com.medicalrecordms.DTO.Response.MedicalRecordResponseDTO;
import com.medicalrecordms.ENUM.Status;
import com.medicalrecordms.Entity.MedicalRecord;
import com.medicalrecordms.Service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    // ======================================
    //              GET METHODS
    // ======================================

    /** Get all records */
    @GetMapping
    public ResponseEntity<List<MedicalRecordResponseDTO>> getAllRecords() {
        return ResponseEntity.ok(medicalRecordService.getAllMedicalRecords());
    }

    /** Get record by ID */
    @GetMapping("/{recordId}")
    public ResponseEntity<MedicalRecordResponseDTO> getRecordById(@PathVariable Long recordId) {
        try {
            MedicalRecordResponseDTO record = medicalRecordService.getMedicalRecordById(recordId);
            return ResponseEntity.ok(record);
        } catch (IllegalArgumentException e) {
            // If the record isn’t found, return 404 with an error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    /** Get all records for a patient */
    @GetMapping("/patient")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getRecordsByPatient(@RequestParam  Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByPatient(patientId));
    }

    /** Get all records for a doctor */
    @GetMapping("/doctor")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getRecordsByDoctor(@RequestParam Long doctorId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByDoctor(doctorId));
    }

    // ======================================
    //              POST METHODS
    // ======================================

    /** Create a medical record (patient + doctor passed in) */
    @PostMapping("/byPatient")
    public ResponseEntity<Long> createMedicalRecord(@RequestBody MedicalRecordRequestDTO dto) {
        MedicalRecord saved = medicalRecordService.createFromPatientRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
    }

    // ======================================
    //              PUT METHODS
    // ======================================

    /** Update therapies (assign or modify therapy list for a record) */
    @PutMapping("/{recordId}/therapies")
    public ResponseEntity<?> updateTherapies(
            @PathVariable Long recordId,
            @RequestBody TherapyUpdateRequest req) {
        try {
            MedicalRecord updated = medicalRecordService.updateTherapies(
                    recordId,
                    req.isNeedTherapy(),
                    req.getTherapyIds()
            );
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No!!! " + e.getMessage());
        }
    }
    @PutMapping("/status-change/{id}")
    public ResponseEntity<Map<String,Object>> changeStatus(@PathVariable Long id, @RequestParam String status, @RequestParam Long doctorId){
        try{
            Status newStatus = Status.fromValue(status); // Convert string --> enum safely (case-insensitive)

            medicalRecordService.updateStatus(id,newStatus,doctorId);

            Map<String, Object> response = Map.of(
                    "id", id,
                    "doctorId", doctorId,
                    "status", newStatus.name()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e){
            Map<String, Object> error = Map.of(
                    "success", false,
                    "error", "Invalid status or unauthorized doctor",
                    "message", e.getMessage()
            );
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                    "success", false,
                    "error", "Record not found",
                    "message", "Record not found for ID: " + id
            );
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error);
        }
    }

    /** Assign therapist to a medical record */
    @PutMapping("/{recordId}/assign-therapist")
    public ResponseEntity<?> assignTherapistToRecord(@PathVariable Long recordId,
                                                     @RequestParam Long therapistId) {
        try {
            MedicalRecord updated = medicalRecordService.assignTherapist(recordId, therapistId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ " + e.getMessage());
        }
    }

    /** Update a medical record (doctor adds diagnosis, treatment, etc.) */
    @PutMapping("/{recordId}")
    public ResponseEntity<?> updateMedicalRecord(@PathVariable Long recordId, @RequestBody MedicalRecord updatedData) {
        try {
            MedicalRecord updated = medicalRecordService.updateMedicalRecord(recordId, updatedData);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ " + e.getMessage());
        }
    }

    // ======================================
    //              DELETE METHODS
    // ======================================

    /** Delete a medical record */
    @DeleteMapping("/{recordId}")
    public ResponseEntity<String> deleteRecord(@PathVariable Long recordId) {
        try {
            medicalRecordService.deleteMedicalRecord(recordId);
            return ResponseEntity.ok("Medical record deleted with ID: " + recordId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ " + e.getMessage());
        }
    }
}