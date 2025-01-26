package com.forex.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Error response object")
public class ErrorDTO {

    @Schema(description = "Error message")
    private Object msg;

    @Schema(description = "HTTP status code")
    private int status;

    @Schema(description = "Timestamp of the error")
    private LocalDateTime time;
}
