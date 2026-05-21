package com.endava.insurance.insurance_service.application.service.ai;

import com.endava.insurance.insurance_service.application.dto.ai.AiClusteringPayload;
import com.endava.insurance.insurance_service.application.dto.ai.AiPythonClusteringResult;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AiPythonClusteringRunner {

    private static final Duration PROCESS_TIMEOUT = Duration.ofSeconds(30);

    private final ObjectMapper objectMapper;

    @Value("${ai.python.command:py}")
    private String pythonCommand;

    @Value("${ai.python.script-path:ml/kprototypes_segmentation.py}")
    private String scriptPath;

    public AiPythonClusteringResult run(AiClusteringPayload payload) throws ValidationException {
        ProcessBuilder processBuilder = new ProcessBuilder(pythonCommand, Path.of(scriptPath).toString());
        processBuilder.redirectErrorStream(false);

        try {
            Process process = processBuilder.start();
            try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
                objectMapper.writeValue(writer, payload);
            }

            CompletableFuture<String> stdoutFuture = CompletableFuture.supplyAsync(() -> read(process.inputReader(StandardCharsets.UTF_8)));
            CompletableFuture<String> stderrFuture = CompletableFuture.supplyAsync(() -> read(process.errorReader(StandardCharsets.UTF_8)));
            boolean finished = process.waitFor(PROCESS_TIMEOUT.toSeconds(), TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new ValidationException("AI clustering process timed out");
            }

            String stdout = stdoutFuture.get();
            String stderr = stderrFuture.get();
            if (process.exitValue() != 0) {
                throw new ValidationException("AI clustering process failed: " + stderr);
            }

            return objectMapper.readValue(stdout, AiPythonClusteringResult.class);
        } catch (IOException e) {
            throw new ValidationException("Could not start AI clustering process: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ValidationException("AI clustering process was interrupted");
        } catch (ExecutionException e) {
            throw new ValidationException("Could not read AI clustering process output: " + e.getMessage());
        }
    }

    private String read(BufferedReader reader) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
}
