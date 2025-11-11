package com.medicalrecordms.Messaging;

import com.medicalrecordms.DTO.Request.MedicalRecordRequestDTO;
import com.medicalrecordms.Entity.MedicalRecord;
import com.medicalrecordms.Service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalRecordMessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MedicalRecordService medicalRecordService;

    public void sendRecord(MedicalRecord medicalRecord) {
        log.info("Triggering sendRecord: preparing message for medicalRecordQueue. MedicalRecord ID: {}", medicalRecord.getId());

        try {
            MedicalRecordRequestDTO dto = medicalRecordService.medicalRecordConvertToMedicalRecordRequestDTO(medicalRecord);
            rabbitTemplate.convertAndSend("medicalRecordQueue", dto);

            log.info("Message successfully sent to medicalRecordQueue for MedicalRecord ID: {}", medicalRecord.getId());
        } catch (Exception ex) {
            log.error("Failed to send message for MedicalRecord ID: {} - {}", medicalRecord.getId(), ex.getMessage(), ex);
        }
    }
}