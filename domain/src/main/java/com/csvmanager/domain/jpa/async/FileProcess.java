package com.csvmanager.domain.jpa.async;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(name = "file_process")
@Data
public class FileProcess {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Enumerated(EnumType.STRING)
  private ProcessStatus status;
  private String fileName;
  private LocalDate importDate;
  /*@OneToMany(mappedBy = "fileProcess", fetch = FetchType.EAGER)
  private List<LineData> lineDataList;*/

  public static FileProcess createNew() {
    FileProcess file = new FileProcess();
    file.status = ProcessStatus.CREATED;
    return file;
  }
}
