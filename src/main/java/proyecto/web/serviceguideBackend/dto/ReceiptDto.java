package proyecto.web.serviceguideBackend.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.TypeService;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptDto {

    @Nullable
    private Long id;

    @NotNull
    private String receiptName;

    @NotNull
    private Double price;

    @NotNull
    private Double amount;

    @NotNull
    private Date date;

    @Nullable
    private String houseName;

    @Nullable
    private TypeService typeService;

    @Nullable
    private House house;
}
