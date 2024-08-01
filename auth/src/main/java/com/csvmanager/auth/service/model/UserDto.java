package com.csvmanager.auth.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDto implements Serializable {

  @NotBlank(message = "Must not be empty")
  private String firstName;
  @NotBlank(message = "Must not be empty")
  private String lastName;
  @NotBlank(message = "Proper password")
  private String password;
  @Email(message = "Email should be valid")
  private String email;
  @NotEmpty(message = "Roles must not be empty")
  private List<Integer> rolesIds;
}