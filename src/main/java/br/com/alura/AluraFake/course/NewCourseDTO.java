package br.com.alura.AluraFake.course;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class NewCourseDTO {

    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @NotBlank
    @Length(min = 4, max = 255)
    private String description;

    public NewCourseDTO() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
