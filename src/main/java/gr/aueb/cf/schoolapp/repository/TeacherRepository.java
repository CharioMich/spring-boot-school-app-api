package gr.aueb.cf.schoolapp.repository;

import gr.aueb.cf.schoolapp.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {
    Optional<Teacher> findByUser(Long id); // @OnetoOne relation (Iser - Teacher) so same as findTeacherByUserId
    Optional<Teacher> findByUuid(String uuid);
}
