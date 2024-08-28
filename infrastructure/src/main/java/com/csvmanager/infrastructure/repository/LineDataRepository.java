package com.csvmanager.infrastructure.repository;

import com.csvmanager.domain.jpa.async.LineData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LineDataRepository extends JpaRepository<LineData, String> {

  @Query("SELECT ld FROM LineData ld WHERE ld.fileProcess.id = :processId")
  List<LineData> findByFileProcess(@Param("processId") Long processId);
}
