package gr.aueb.cf.schoolapp.core.filters;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;


public record Paginated<T> (
        List<T> data,
        long totalElements,
        int totalPages,
        int numberOfElements,
        int currentPage,
        int pageSize
) {

    public Paginated(Page<T> page) {
        this(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.getNumber(),
                page.getSize()
        );
    }

}
