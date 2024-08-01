package com.csvmanager.auth.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  @NotBlank(message = "Must not be empty")
  private String firstName;
  @NotBlank(message = "Must not be empty")
  private String lastName;
  @NotBlank(message = "Proper password")
  private String password;
  @Email(message = "Email should be valid")
  private String email;
}
