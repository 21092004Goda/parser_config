package org.kuro.adapters.outgoing;

import org.kuro.core.domain.ActionType;
import org.kuro.exceptions.model.ErrorCode;
import org.kuro.exceptions.special.FileException;
import org.kuro.exceptions.special.ProcessingException;
import org.kuro.port.outgoing.FileReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;

public class FileReaderImpl implements FileReader {

    @Override
    public List<List<String>> readFiles(List<String> paths) {
        List<List<String>> fileContents = new ArrayList<>();

        for (String path : paths) {
            try {
                List<String> lines = Files.readAllLines(Path.of(path));
                fileContents.add(lines);
            } catch (NoSuchFileException e) {
                throw new FileException(
                        ErrorCode.FILE_NOT_FOUND,
                        path, e
                );
            } catch (IOException e) {
                throw new FileException(
                        ErrorCode.FILE_READ_ERROR,
                        path, e
                );
            }
        }

        return fileContents;
    }

    @Override
    public Map<Integer, List<String>> processFiles(List<List<String>> fileContents, ActionType actionType) {
        try {
            int maxLine = fileContents.stream().mapToInt(List::size).max().orElse(0);
            Map<Integer, List<String>> result = new HashMap<>();

            for (int k = 0; k < maxLine; k++) {
                List<String> lineGroup = new ArrayList<>();

                for (int fileIndex = 0; fileIndex < fileContents.size(); fileIndex++) {
                    List<String> file = fileContents.get(fileIndex);
                    String line = k < file.size() ? file.get(k) : "";

                    if (actionType == ActionType.STRING) {
                        lineGroup.add(line);
                    } else if (actionType == ActionType.COUNT) {
                        lineGroup.add(String.valueOf(line.isEmpty() ? 0 : line.split("\\s+").length));
                    } else if (actionType == ActionType.REPLACE) {
                        lineGroup.add(replaceChars(line, fileIndex + 1));
                    }
                }
                result.put(k + 1, lineGroup);
            }
            return result;
        } catch (Exception e) {
            throw new ProcessingException(
                    ErrorCode.PROCESSING_ERROR,
                    e
            );
        }
    }

    private static String replaceChars(String line, int fileIndex) {
        return line.replaceAll("a", String.valueOf(1 + fileIndex))
                .replaceAll("b", String.valueOf(2 + fileIndex))
                .replaceAll("c", String.valueOf(3 + fileIndex));
    }
}
