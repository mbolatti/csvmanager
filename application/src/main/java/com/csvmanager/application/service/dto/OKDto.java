package com.csvmanager.application.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class OKDto {

  private int chargedNumber = 0;
  private List<String> chargedList = new ArrayList<>();

  public void addOK(String line){
    this.chargedNumber++;
    this.chargedList.add(line);
  }

}
