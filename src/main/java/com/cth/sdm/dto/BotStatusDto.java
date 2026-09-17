package com.cth.sdm.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotStatusDto {

    private boolean running;
    private String statusMessage;
    private long totalProcessed;
    private String currentKeyword;
}
