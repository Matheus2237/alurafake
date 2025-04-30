package br.com.alura.AluraFake.security;

import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static java.time.Instant.now;

@RestController
public class TokenController {

    private static final Long ONE_DAY = 86400L;

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;

    public TokenController(JwtEncoder jwtEncoder, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponseDTO> auth(@Valid @RequestBody AuthRequestDTO authRequestDTO) throws IllegalArgumentException {
        User user = userRepository.findByEmail(authRequestDTO.email())
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist"));
        if (!authRequestDTO.password().equals(user.getPassword())) {
            throw new IllegalArgumentException("Invalid password.");
        }
        String role = user.getRole().toString();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("alurafake")
                .subject(user.getId().toString())
                .issuedAt(now())
                .expiresAt(now().plusSeconds(ONE_DAY))
                .claim("scope", role)
                .build();
        final String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return ResponseEntity.ok(new AuthResponseDTO(token, ONE_DAY));
    }
}