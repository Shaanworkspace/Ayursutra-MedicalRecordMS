package com.medicalrecordms.Entity;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.medicalrecordms.ENUM.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Entity
@Table(name = "medical_records")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long patientId;


    private Long doctorId;

    // Visit Details
    private LocalDateTime visitDate;   // kab doctor ko dikhaaya

    @Column(length = 255)
    private String symptoms;

    @Lob
    private String diagnosis;

    @Lob
    private String prescribedTreatment;

    @Lob
    private String allergies;

    @Lob
    private String medicalHistoryNotes; //updated By doctor

    @Lob
    private String medications; //updated By doctor

    private String followUpRequired; //updated By doctor

    private boolean needTherapy; //updated By doctor


    private List<Long> requiredTherapyIds = new ArrayList<>(); //updated By doctor


    private Long therapyPlanId; //updated By doctor


    private Long therapistId;  //Patient krega update

    private boolean approvedByTherapist;

    // Record creation date (auto set)
    private LocalDate createdDate;

    // Therapy name (like Panchakarma 5 types)
    @Column(length = 100)
    private String therapyName;   // e.g. "Vamana", "Virechana", "Basti", "Nasya", "Raktamokshana"

    private LocalDate startDate;   // therapy start
    private LocalDate endDate;     // therapy end

    private Status status;        // Active, Completed, Pending
    private Integer noOfDays;      // duration

    @Lob
    private String doctorNotes;     //updated By doctor

    private Double rating;         // Patient feedback rating (0–5)

    //Automatically set createdDate & visitDate when record is first created
    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDate.now();
        if (this.visitDate == null) {
            this.visitDate = LocalDateTime.now();
        }
    }
}