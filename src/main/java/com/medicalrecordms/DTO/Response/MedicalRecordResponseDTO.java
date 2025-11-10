package com.medicalrecordms.DTO.Response;

import com.medicalrecordms.ENUM.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordResponseDTO {

    private Long id;
    private LocalDateTime visitDate;
    private String symptoms;
    private String diagnosis;
    private String prescribedTreatment;

    // Essentials for doctor & patient
    private Long patientId;


    private Long doctorId;


    private Long therapistId;


    private LocalDate createdDate;
    private String therapyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private Integer noOfDays;
    private String doctorNotes;
    private Double rating;
}