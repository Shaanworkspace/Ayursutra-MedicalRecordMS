package com.medicalrecordms.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordRequestDTO {

    private Long patientId;              // ID of the patient booking the appointment
    private Long doctorId;               // ID of the doctor being booked

    private LocalDateTime visitDate;     // Preferred appointment date & time
    private String symptoms;             // Description of symptoms or reason for visit
    private String allergies;            // Known allergies (if any)
    private String medicalHistoryNotes;  // Relevant past medical history
    private String medications;          // Current medications being taken
    private String followUpRequired;     // Yes/No/Maybe - if patient needs follow-up

}