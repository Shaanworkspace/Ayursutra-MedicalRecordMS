package com.medicalrecordms.Controller;

import com.medicalrecordms.DTO.Request.TherapyUpdateRequest;
import com.medicalrecordms.DTO.Response.MedicalRecordResponseDTO;
import com.medicalrecordms.Entity.MedicalRecord;
import com.medicalrecordms.Service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> getRecordById(@PathVariable Long recordId) {
        try {
            return ResponseEntity.ok(medicalRecordService.getMedicalRecordById(recordId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Oops !! " + e.getMessage());
        }
    }

    /** Get all records for a patient */
    @GetMapping("/patient")
    public ResponseEntity<List<MedicalRecord>> getRecordsByPatient(@RequestParam  Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByPatient(patientId));
    }

    /** Get all records for a doctor */
    @GetMapping("/doctor")
    public ResponseEntity<List<MedicalRecord>> getRecordsByDoctor(@RequestParam Long doctorId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByDoctor(doctorId));
    }

    // ======================================
    //              POST METHODS
    // ======================================

    /** Create a medical record (patient + doctor passed in) */
    @PostMapping
    public ResponseEntity<?> createMedicalRecord(@RequestParam Long patientId,
                                                 @RequestBody MedicalRecord record) {
        try {
            MedicalRecord saved = medicalRecordService.createMedicalRecord(patientId, record);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ " + e.getMessage());
        }
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ " + e.getMessage());
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