package proyecto.web.serviceguideBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import proyecto.web.serviceguideBackend.entities.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewReceiptDto {

    private Long id;

    private Water water;

    private Sewerage sewerage;

    private Energy energy;

    private Gas gas;

    private User user;
}
