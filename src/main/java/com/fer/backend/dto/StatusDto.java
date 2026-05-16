package com.fer.backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusDto {
    private UUID statusId;
    private String nazivStatusa;
    private LocalDateTime vrijemeAzuriranja;
    private UUID narudzbaId;
}