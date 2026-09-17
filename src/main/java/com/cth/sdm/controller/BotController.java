package com.cth.sdm.controller;

import com.cth.sdm.dto.BotStatusDto;
import com.cth.sdm.workflow.LinkedInBotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bot")
@RequiredArgsConstructor
@Tag(name = "Bot Control Engine", description = "APIs for controlling and monitoring the automated job discovery bot")
public class BotController {

    private final LinkedInBotService botService;

    @GetMapping("/status")
    @Operation(summary = "Get Bot Status", description = "Returns current status, active execution state, and count")
    public ResponseEntity<BotStatusDto> getStatus() {
        return ResponseEntity.ok(botService.getStatus());
    }

    @PostMapping("/start")
    @Operation(summary = "Start Job Bot", description = "Triggers the automated job discovery workflow")
    public ResponseEntity<String> startBot() {
        String result = botService.startBot();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/stop")
    @Operation(summary = "Stop Job Bot", description = "Stops the automated job discovery workflow")
    public ResponseEntity<String> stopBot() {
        String result = botService.stopBot();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/trigger-cycle")
    @Operation(summary = "Trigger Single Apply Cycle", description = "Executes one immediate job search and apply cycle asynchronously")
    public ResponseEntity<String> triggerCycle() {
        botService.triggerAsyncJobApplicationCycle();
        return ResponseEntity.ok("Async job listing cycle triggered.");
    }
}
