package com.csvmanager.domain.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Table(name = "personal_data", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"import_date", "city", "fiscal_code"})
})

public class PersonalData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String importId;
    private LocalDate importDate;
    private String name;
    private String lastName;
    private String birthDate;
    private String city;
    private String fiscalCode;
}
