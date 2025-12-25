package com.trans.asgard.domain.historiquevente.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueVenteDto {
    @NotNull
    @PastOrPresent
    private LocalDate dateVente;

    @Min(1)
    private int quantiteVendue;

    @NotNull
    private Long productId;

    @NotNull
    private Long entrepotId;


}
