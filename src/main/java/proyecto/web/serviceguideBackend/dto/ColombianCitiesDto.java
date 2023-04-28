package proyecto.web.serviceguideBackend.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColombianCitiesDto {

    @Nullable
    private Long id;

    @NotNull
    private String city;

}
