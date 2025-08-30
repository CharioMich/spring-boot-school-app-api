package gr.aueb.cf.schoolapp.core.filters;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Custom container to use instead of 'Page<>'
 */
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
