package proyecto.web.serviceguideBackend.house.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import proyecto.web.serviceguideBackend.city.City;
import proyecto.web.serviceguideBackend.user.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HouseDto {

    @Nullable
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Integer stratum;

    @NotNull
    private String neighborhood;

    @Nullable
    private String address;

    @NotNull
    private String contract;

    @NotNull
    private City cities;

    @Nullable
    private User user;
}
