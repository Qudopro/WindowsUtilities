package com.qudopro.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Propeties {
    private static final Logger logger = LoggerFactory.getLogger(Propeties.class);

    public static void writeProperties(Path imagePath, Map<String, String> tags) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("C:\\Tools\\exiftool-13.59_64\\exiftool.exe");

        // Cada tag se pasa como -TagName=Valor
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            command.add("-" + entry.getKey() + "=" + entry.getValue());
        }
        command.add("-overwrite_original");
        command.add(imagePath.toString());

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        String output;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            output = reader.lines().collect(Collectors.joining("\n"));
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            logger.error("Fallo al hacer la modificación de las propiedades");
            throw new IOException("exiftool falló (código " + exitCode + "): " + output);
        }
    }
}
