package com.csvmanager.service.dto;

import com.csvmanager.service.dto.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class PersonalDataDto {
    @JsonView(Views.Internal.class)
    private Long id;
    @JsonView(Views.Internal.class)
    private String importId;
    @JsonView(Views.Internal.class)
    private LocalDate importDate;
    @JsonView(Views.Public.class)
    private String name;
    @JsonView(Views.Public.class)
    private String lastName;
    @JsonView(Views.Public.class)
    private String birthDate;
    @JsonView(Views.Public.class)
    private String city;
    @JsonView(Views.Public.class)
    private String fiscalCode;
}
