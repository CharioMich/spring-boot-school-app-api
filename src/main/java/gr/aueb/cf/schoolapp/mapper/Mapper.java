package gr.aueb.cf.schoolapp.mapper;

import gr.aueb.cf.schoolapp.dto.*;
import gr.aueb.cf.schoolapp.model.PersonalInfo;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final PasswordEncoder passwordEncoder;

    public TeacherReadOnlyDTO mapToTeacherReadOnlyDTO(Teacher teacher) {

        // Map User to UserReadOnlyDTO
        UserReadOnlyDTO userDTO = new UserReadOnlyDTO(
                teacher.getUser().getFirstname(),
                teacher.getUser().getLastname(),
                teacher.getUser().getAfm()
        );

        // Map PersonalInfo to PersonalInfoReadOnlyDTO
        PersonalInfoReadOnlyDTO personalInfoDTO = new PersonalInfoReadOnlyDTO(
                teacher.getPersonalInfo().getAmka(),
                teacher.getPersonalInfo().getIdentityNumber()
        );

        return new TeacherReadOnlyDTO(
                teacher.getId(),
                teacher.getUuid(),
                teacher.getIsActive(),
                userDTO,
                personalInfoDTO
        );
    }


    public Teacher mapToTeacherEntity(TeacherInsertDTO dto) {
        Teacher teacher = new Teacher();
        teacher.setIsActive(dto.isActive());

        // Map fields from UserDTO
        UserInsertDTO userDTO = dto.user(); // extract user dto data
        User user = new User();
        user.setFirstname(userDTO.firstname());
        user.setLastname(userDTO.lastname());
        user.setUsername(userDTO.username());
        user.setPassword(passwordEncoder.encode(userDTO.password())); // This step could be implemented in a service layer
        user.setAfm(userDTO.afm());
        user.setFatherName(userDTO.fatherName());
        user.setFatherLastname(userDTO.fatherLastname());
        user.setMotherName(userDTO.motherName());
        user.setMotherLastname(userDTO.motherLastname());
        user.setDateOfBirth(userDTO.dateOfBirth());
        user.setGender(userDTO.gender());
        user.setRole(userDTO.role());
        user.setIsActive(dto.isActive());
        teacher.setUser(user);  // Set User entity to Teacher

        // Map fields from PersonalInfoDTO
        PersonalInfoInsertDTO personalInfoDTO = dto.personalInfo(); // extract personal info dto data
        PersonalInfo personalInfo = new PersonalInfo();
        personalInfo.setAmka(personalInfoDTO.amka());
        personalInfo.setIdentityNumber(personalInfoDTO.identityNumber());
        personalInfo.setPlaceOfBirth(personalInfoDTO.placeOfBirth());
        personalInfo.setMunicipalityOfRegistration(personalInfoDTO
                .municipalityOfRegistration());
        teacher.setPersonalInfo(personalInfo);  // Set PersonalInfo entity to Teacher

        return teacher;
    }
}
