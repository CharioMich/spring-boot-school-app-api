package gr.aueb.cf.schoolapp.core.filters;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class GenericFilters {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_COLUMN = "id";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    private int page;
    private int pageSize;
    private Sort.Direction sortDirection;
    private String sortBy;

    public int getPage() {
        return Math.max(page, 0);   // Only positive pages or 0
    }

    public int getPageSize() {
        return pageSize < 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public Sort.Direction getSortDirection() {
        if (sortDirection == null) return DEFAULT_SORT_DIRECTION;
        return sortDirection;
    }

    public String getSortBy() {
        if (sortBy == null || sortBy.isBlank()) return DEFAULT_SORT_COLUMN;
        return sortBy;
    }

    public Sort getSort() {
        return Sort.by(getSortDirection(), getSortBy());    // return direction & properties
    }

    public Pageable getPageable() {
        return PageRequest.of(getPage(), getPageSize(), getSort());
    }
}
