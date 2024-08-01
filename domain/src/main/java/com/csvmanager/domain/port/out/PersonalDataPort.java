package com.csvmanager.domain.port.out;

import com.csvmanager.domain.jpa.PersonalData;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PersonalDataPort {
  void save(PersonalData personalData);

  Optional<PersonalData> findByImportDateCityFC(LocalDate importDate, String city, String fiscalCode);

  List<PersonalData> findAllImportedCsv();
}
