package proyecto.web.serviceguideBackend.house.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import proyecto.web.serviceguideBackend.city.City;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlyHouse {

    private Long id;

    private String name;

    private Integer stratum;

    private String neighborhood;

    private String address;

    private String contract;

    private City cities;

}
