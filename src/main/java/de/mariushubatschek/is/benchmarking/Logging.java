package de.mariushubatschek.is.benchmarking;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logging {

    public static void log(final File file, final String instance, final String algorithm, final String variableHeuristic, final String valueHeuristic, final long nodeCount, final long backtrackCount, final boolean timeout) throws IOException {
        final int timedOut = timeout ? 1 : 0;
        try (CSVWriter writer = new CSVWriter(new FileWriter(file, true))) {
            writer.writeNext(new String[]{instance, algorithm, variableHeuristic, valueHeuristic, Long.toString(nodeCount), Long.toString(backtrackCount), Integer.toString(timedOut)});
        }
    }

}
