package gr.aueb.cf.schoolapp.core.filters;

import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TeacherFilters {

    @Nullable   // make obvious that it could be null
    private String uuid;
    @Nullable
    private String userAfm;
    @Nullable
    private String userAmka;
    @Nullable
    private Boolean isActive;

}
