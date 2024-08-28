package com.csvmanager.application.service.dto.async;

import com.csvmanager.domain.jpa.async.ProcessStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class FileProcessDto {
    private Long id;
    private ProcessStatus status;
    private String fileName;
    private LocalDate importDate;
    private List<LineDataDto> lineDataList;

    public static FileProcessDto createNew() {
        FileProcessDto file = new FileProcessDto();
        file.status = ProcessStatus.CREATED;
        return file;
    }
}
