package br.com.alura.AluraFake.security;

public record AuthResponseDTO(String accessToken, Long expiresIn) {
}