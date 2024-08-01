package com.csvmanager.domain.model;

import com.csvmanager.domain.model.validators.ValidFiscalCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class PersonalData {
    long lineId;

    @NotBlank(message = "Import id cannot be blank or 0")
    String importId;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 50, message = "Name cannot be longer than 50 characters")
    String name;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name cannot be longer than 50 characters")
    String lastName;

    @NotBlank(message = "Birth date cannot be blank")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Birth date must be in the format DD/MM/YYYY")
    String birthDate;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City cannot be longer than 100 characters")
    String city;

    @ValidFiscalCode
    @NotBlank(message = "Fiscal code cannot be blank")
    String fiscalCode;


    @Override
    public String toString() {
        return "importId=" + importId +
            ", nome='" + name + '\'' +
            ", cognome='" + lastName + '\'' +
            ", DataDiNascita='" + birthDate + '\'' +
            ", città='" + city + '\'' +
            ", codiceFiscale='" + fiscalCode + '\'';
    }
}
