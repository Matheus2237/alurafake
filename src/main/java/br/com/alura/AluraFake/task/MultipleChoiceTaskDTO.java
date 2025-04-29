package br.com.alura.AluraFake.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record MultipleChoiceTaskDTO(
        @NotNull
        Long courseId,

        @NotBlank
        @Length(min = 4, max = 255)
        String statement,

        @NotNull
        @Positive
        Integer order,

        @NotNull
        @Valid
        @Size(min = 3, max = 5, message = "The activity must have between 3 and 5 options.")
        List<OptionDTO> options
) {
        public List<Option> optionsAsEntites() {
                return options.stream().map(OptionDTO::toEntity).toList();
        }
}
