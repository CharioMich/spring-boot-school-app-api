package gr.aueb.cf.schoolapp.dto;

import lombok.*;

@Builder
public record AuthenticationResponseDTO (
        String firstname,
        String lastname,
        String token
) {}
