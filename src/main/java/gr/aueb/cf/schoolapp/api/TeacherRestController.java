package gr.aueb.cf.schoolapp.api;

import gr.aueb.cf.schoolapp.core.exceptions.*;
import gr.aueb.cf.schoolapp.core.filters.Paginated;
import gr.aueb.cf.schoolapp.core.filters.TeacherFilters;
import gr.aueb.cf.schoolapp.dto.TeacherInsertDTO;
import gr.aueb.cf.schoolapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.schoolapp.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor    // for DI
public class TeacherRestController {

    private final TeacherService teacherService;


    /**
     * REST Controller. Saves a new Teacher entity
     * @param teacherInsertDTO @Valid -> Enables Hibernate-JPA validation as per the annotated DTOs (Records).
     * @param amkaFile @RequestPart because we have the DTO and the file. So two parts.
     * @param bindingResult Error container if errors occur
     * @return ResponseEntity container, handles the data and the Response Status code
     */
    @PostMapping("/teachers/save")
    public ResponseEntity<TeacherReadOnlyDTO> saveTeacher(
            @Valid @RequestPart(name = "teacher")TeacherInsertDTO teacherInsertDTO,
            @Nullable @RequestPart("amkaFile") MultipartFile amkaFile,
            BindingResult bindingResult)
            throws AppObjectInvalidArgumentException, ValidationException, AppObjectAlreadyExists, AppServerException, IOException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        try {
            TeacherReadOnlyDTO teacherReadOnlyDTO = teacherService.saveTeacher(teacherInsertDTO, amkaFile);
            return new ResponseEntity<>(teacherReadOnlyDTO, HttpStatus.CREATED);
        } catch (IOException e) {
            throw new AppServerException("Attachment", "Attachment cannot get uploaded"); // Catch the IOException and throw custom AppServerException to be handled by our custom global Exception Handler
        }
    }


    /**
     * REST Controller GET paginated teachers
     * @param page with @RequestParam, we give sa default value. Note that it's a string
     * @param size of elements per page
     * @return ResponseEntity container > Page > TeacherReadOnlyDTO. We could also return custom Paginated container instead of Page
     */
    @GetMapping("/teachers/paginated")
    public ResponseEntity<Page<TeacherReadOnlyDTO>> getPaginatedTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<TeacherReadOnlyDTO> teachersPage = teacherService.getPaginatedTeachers(page, size);
        return new ResponseEntity<>(teachersPage, HttpStatus.OK);
    }


    /**
     * @param filters @RequestBody -> JSON. @Nullable -> May be null. We may have or not have filters
     */
    @PostMapping("/teachers/filtered")
    public ResponseEntity<List<TeacherReadOnlyDTO>> getFilteredTeachers(@Nullable @RequestBody TeacherFilters filters)
        throws AppObjectNotAuthorizedException {
        if (filters == null) TeacherFilters.builder().build();  // Create an instance but without any filters. Return ALL
        return ResponseEntity.ok(teacherService.getTeachersFiltered(filters));
    }


    /**
     * @return Teachers filtered and Paginated via custom Paginated container wrapper by the ResponseEntity
     */
    @PostMapping("/teachers/filtered/paginated")
    public ResponseEntity<Paginated<TeacherReadOnlyDTO>> getTeachersFilteredPaginated(@Nullable @RequestBody TeacherFilters filters)
        throws AppObjectNotAuthorizedException {
        if (filters == null) TeacherFilters.builder().build();  // Create an instance but without any filters. Return ALL
        return ResponseEntity.ok(teacherService.getTeachersFilteredPaginated(filters));
    }
}
