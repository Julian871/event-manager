package dev.sorokin.kafka;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeItem {

    @NotBlank
    private String field;

    private String oldValue;
    private String newValue;
}
