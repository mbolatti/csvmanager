package com.csvmanager.infrastructure.adapter.out;

import com.csvmanager.domain.jpa.PersonalData;
import com.csvmanager.domain.port.out.PersonalDataPort;
import com.csvmanager.infrastructure.repository.PersonalDataRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DatabaseAdapter  implements PersonalDataPort {
  
  private final PersonalDataRepository personalDataRepository;

  public DatabaseAdapter(PersonalDataRepository personalDataRepository) {
    this.personalDataRepository = personalDataRepository;
  }

  @Override
  @Transactional
  public void save(PersonalData personalData) {
    personalDataRepository.save(personalData);
  }

  @Override
  public Optional<PersonalData> findByImportDateCityFC(LocalDate importDate, String city,
      String fiscalCode) {
    return personalDataRepository.findByImportDateAndCityAndFiscalCode(importDate, city, fiscalCode);
  }

  @Override
  public List<PersonalData> findAllImportedCsv() {
    return personalDataRepository.findAll();
  }

}
