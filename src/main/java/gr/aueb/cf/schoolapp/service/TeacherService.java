package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.schoolapp.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.schoolapp.dto.TeacherInsertDTO;
import gr.aueb.cf.schoolapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.schoolapp.mapper.Mapper;
import gr.aueb.cf.schoolapp.model.Attachment;
import gr.aueb.cf.schoolapp.model.PersonalInfo;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.repository.PersonalInfoRepository;
import gr.aueb.cf.schoolapp.repository.TeacherRepository;
import gr.aueb.cf.schoolapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // Class which contains file bytes and metadata

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service    // Spring - Adds instance to IOC container
@RequiredArgsConstructor    // Lombok - creates constructor with final fields (for DI)
public class TeacherService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TeacherService.class);
    private final TeacherRepository teacherRepository;
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final PersonalInfoRepository personalInfoRepository;

    
    @Transactional(rollbackOn = {AppObjectAlreadyExists.class, IOException.class}) // In case of runtime exceptions or specified checked exceptions, rollback.
    public TeacherReadOnlyDTO saveTeacher(TeacherInsertDTO teacherInsertDTO, MultipartFile amkaFile)
        throws AppObjectAlreadyExists, AppObjectInvalidArgumentException, IOException {

        if (userRepository.findByVat(teacherInsertDTO.user().afm()).isPresent()) {
            throw new AppObjectAlreadyExists("User", "User with vat=" + teacherInsertDTO.user().afm() + " already exists.");
        }

        if (userRepository.findByUsername(teacherInsertDTO.user().username()).isPresent()) {
            throw new AppObjectAlreadyExists("User", "User with username=" + teacherInsertDTO.user().username() + " already exists.");
        }

        Teacher teacher = mapper.mapToTeacherEntity(teacherInsertDTO);  // saving the teacher cascades user & personal info

        saveAmkaFile(teacher.getPersonalInfo(), amkaFile);
        Teacher savedTeacher = teacherRepository.save(teacher); // savedTeacher also contains the "id" from the database

        return mapper.mapToTeacherReadOnlyDTO(savedTeacher);
    }


    /**
     *  Saves the Attachment file to disk or some storage
     */
    public void saveAmkaFile(PersonalInfo personalInfo, MultipartFile amkaFile)
        throws IOException {
        if (amkaFile == null || amkaFile.isEmpty()) return;

        String originalFileName = amkaFile.getOriginalFilename();   // The user's file name
        String savedName = UUID.randomUUID().toString() + getFileExtension(originalFileName);

        String uploadDirectory = "uploads/";
        Path filePath = Paths.get(uploadDirectory + savedName); // Creates a Path
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, amkaFile.getBytes()); // Write file bytes to storage

        // Get the metadata to be saved in our database
        Attachment attachment = new Attachment();
        attachment.setFilename(originalFileName);
        attachment.setSavedName(savedName);
        attachment.setFilePath(filePath.toString());
        attachment.setContentType(amkaFile.getContentType());
        attachment.setExtension(getFileExtension(originalFileName));

        personalInfo.setAmkaFile(attachment);
    }

    // Util function to be used above, in saveAmkaFile()
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf("."));
    }


    @Transactional
    public Page<TeacherReadOnlyDTO> getPaginatedTeachers(int page, int size) {
        String defaultSort = "id";
        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());
        return teacherRepository.findAll(pageable).map(mapper::mapToTeacherReadOnlyDTO);
    }


    @Transactional
    public Page<TeacherReadOnlyDTO> getPaginatedSortedTeachers(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return teacherRepository.findAll(pageable).map(mapper::mapToTeacherReadOnlyDTO);
    }

}
