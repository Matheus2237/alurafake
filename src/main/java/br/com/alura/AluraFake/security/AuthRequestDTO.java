package br.com.alura.AluraFake.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {}