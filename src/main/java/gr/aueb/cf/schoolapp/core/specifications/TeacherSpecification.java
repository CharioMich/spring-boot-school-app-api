package gr.aueb.cf.schoolapp.core.specifications;

import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class TeacherSpecification {

    private TeacherSpecification() {}

    /**
     * Get a Teacher by the afm of the corresponding User. Every Teacher is a User
     * @param afm User field
     * @return the implementation of "toPredicate". The Specification<T> interface is a functional interface
     * so we return a lambda to implement "toPredicate", which constructs a Predicate based on:
     * the root entity, the query criteria, and the criteria builder.
     */
    public static Specification<Teacher> teacherUserAfmIs(String afm) {
        return ((root, query, criteriaBuilder) -> {
            if (afm == null || afm.isBlank())
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true. No filtering applied

            Join<Teacher, User> user = root.join("user");   // root == Teacher. Join User. Join the two entities
            return criteriaBuilder.equal(user.get("afm"), afm); // compare database afm (left) with given one (right)
        });
    }


    /**
     * @param isActive true for active users, false for inactive users
     */
    public static Specification<Teacher> teacherIsActive(Boolean isActive) {
        return ((root, query, criteriaBuilder) -> {
            if (isActive == null) return criteriaBuilder.isTrue(criteriaBuilder.literal(true));

            Join<Teacher, User> user = root.join("user");   // Join the two entities
            return criteriaBuilder.equal(user.get("isActive"), isActive);
        });
    }


    /**
     * @param amka Teachers filtered by amka
     */
    public static Specification<Teacher> teacherPersonalInfoAmkais(String amka) {
        return ((root, query, criteriaBuilder) -> {
            if (amka == null || amka.isBlank()) return criteriaBuilder.isTrue(criteriaBuilder.literal(true));

            Join<Teacher, User> user = root.join("user");   // Join the two entities
            return criteriaBuilder.equal(user.get("amka"), amka);
        });
    }


    /**
     * General use filtering for string values using corresponding SQL LIKE syntax and '%'
     * @param field to be filtered
     * @param value value of filter
     */
    public static Specification<Teacher> teacherStringFieldLike(String field, String value) {
        return ((root, query, criteriaBuilder) -> {
            if (value == null || value.trim().isEmpty()) return criteriaBuilder.isTrue(criteriaBuilder.literal(true));

            return criteriaBuilder.like(criteriaBuilder.upper(root.get(field)), "%" + value.toUpperCase() + "%");
        });
    }

}
