
package com.csvmanager.web.controller;

import com.csvmanager.domain.port.in.ProcessCsvUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/messaging/")
@Tag(name = "Send messages", description = "Send Messages")
@Slf4j
public class SendMessagestController {
  private final ProcessCsvUseCase processCsvUseCase;

  @Autowired
  public SendMessagestController(ProcessCsvUseCase processCsvUseCase) {
    this.processCsvUseCase = processCsvUseCase;
  }

  @PostMapping(path = "messages/{queueId}", consumes = "text/plain")
  @Operation(summary = "Sends a message", description = "Sends a message")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message sent successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid message"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<String> sendMessage(@PathVariable String queueId, @RequestBody String message) {
    log.info("sending message");
    int queue = Integer.parseInt(queueId);
    switch (queue) {
      case 1:
        processCsvUseCase.sendMessage1(message);
        break;
      case 2:
        processCsvUseCase.sendMessage2(message);
        break;
      case 3:
        processCsvUseCase.sendMessage3(message);
        break;
      default:
        throw new RuntimeException("Invalid queue id");
    }
    return new ResponseEntity<>(String.format("Message sent to queue %s", queue), HttpStatus.OK);
  }

  @GetMapping(path = "messages", produces = "application/json")
  @Operation(summary = "Get all messages from the queue", description = "Return all messages from the queue")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Get all messages from the queue"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<Object> getAllImportedCSV() {
    log.info("Get all messages from the queue");
    throw new RuntimeException("not implemented yet");
  }

}
