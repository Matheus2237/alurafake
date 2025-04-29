package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OptionDTO(

        @NotBlank
        @Size(min = 4, max = 80)
        String option,

        @NotNull
        Boolean isCorrect
) {
        public Option toEntity() {
                return new Option(option, isCorrect);
        }
}
