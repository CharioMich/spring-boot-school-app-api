package gr.aueb.cf.schoolapp.dto;

public record TeacherReadOnlyDTO(
        Long id,
        String uuid,
        Boolean isActive,
        UserReadOnlyDTO user,
        PersonalInfoReadOnlyDTO personalInfo
) {}
