package com.medicalrecordms.DTO.Request;

import lombok.Data;

import java.util.List;

@Data
public class TherapyUpdateRequest {
    private boolean needTherapy;
    private List<Long> therapyIds;
}