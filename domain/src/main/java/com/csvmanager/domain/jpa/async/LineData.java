package com.csvmanager.domain.jpa.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Table(name = "line_data", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"city", "fiscal_code"})
})

public class LineData {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "process_id")
  private FileProcess fileProcess;
  private Long lineNumber;
  private String name;
  private String lastName;
  private String birthDate;
  private String city;
  private String fiscalCode;
  @Enumerated(EnumType.STRING)
  private ProcessStatus status;
  @Column(name = "errors", columnDefinition = "TEXT")
  private String errors;
  @Transient
  private List<String> errorsListInternal = new ArrayList<>();

 /* public List<String> getErrors() {
    if (errors == null) {
      errors = new ArrayList<>();
    }
    return errors;
  }*/

  public void setErrors(List<String> errors) {
    this.errorsListInternal = errors;
    try {
      ObjectMapper mapper = new ObjectMapper();
      this.errors = mapper.writeValueAsString(errors);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public List<String> getErrors() {
    if (errors != null) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        return mapper.readValue(errors, new TypeReference<List<String>>() {});
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    } else {
      errorsListInternal = new ArrayList<>();
    }
    return errorsListInternal;
  }

}