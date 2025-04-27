package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

public record OpenTextTaskDTO(
        @NotNull
        Long courseId,

        @NotBlank
        @Length(min = 4, max = 255)
        String statement,

        @NotNull
        @Positive
        Integer order

) implements Serializable {
}