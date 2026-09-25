package proyecto.web.serviceguideBackend.receipt.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.receipt.typeService.TypeService;

import java.time.LocalDate;

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
    private Float amount;

    @Nullable
    private String houseName;

    @NotNull
    private LocalDate date;

    @NotNull
    private TypeService typeService;

    @NotNull
    private House house;

}
