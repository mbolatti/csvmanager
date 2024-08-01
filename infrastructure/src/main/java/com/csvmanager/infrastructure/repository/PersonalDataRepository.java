package com.csvmanager.infrastructure.repository;

import com.csvmanager.domain.jpa.PersonalData;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalDataRepository extends JpaRepository<PersonalData, String> {

  Optional<PersonalData> findByImportDateAndCityAndFiscalCode(LocalDate importDate, String city, String fiscalCode);
}
