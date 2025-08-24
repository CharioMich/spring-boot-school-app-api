package gr.aueb.cf.schoolapp.dto;

import jakarta.validation.constraints.NotNull;

public record TeacherInsertDTO (
        @NotNull(message = "isActive field is required")
        Boolean isActive,

        @NotNull(message = "User details are required")
        UserInsertDTO user,

        @NotNull(message = "Personal Info is required")
        PersonalInfoInsertDTO personalInfo
) {}
