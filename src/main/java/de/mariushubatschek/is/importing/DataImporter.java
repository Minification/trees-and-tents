package de.mariushubatschek.is.importing;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import de.mariushubatschek.is.data.PuzzleInfo;
import de.mariushubatschek.is.data.RowData;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataImporter {

    public PuzzleInfo importFile(final Path path) throws Exception {
        var parser = new CSVParserBuilder()
                .withSeparator(',')
                .build();
        var csvReader = new CSVReaderBuilder(new StringReader(Files.readString(path, StandardCharsets.UTF_8)))
                .withCSVParser(parser)
                .build();
        var records = csvReader.readAll();

        if (records.size() <= 1) {
            throw new Exception("File must have more than one row");
        }

        var firstRow = records.remove(0);

        if (firstRow.length <= 1) {
            throw new Exception("File must have more than one column");
        }

        List<Integer> collect = Arrays.stream(firstRow).skip(1).map(Integer::parseInt).collect(Collectors.toList());
        int[] maxRowCounts = new int[collect.size()];

        for (int i = 0; i < collect.size(); i++) {
            maxRowCounts[i] = collect.get(i);
        }

        var puzzleInfo = new PuzzleInfo();
        puzzleInfo.maxColumnTentCounts = maxRowCounts;

        var columnCount = firstRow.length;

        var rows = new ArrayList<RowData>();
        for (var row : records) {
            if (row.length != columnCount) {
                throw new Exception("All rows must have the same amount of columns");
            }
            var rowData = new RowData();
            rowData.tentCount = Integer.parseInt(row[0]);
            rowData.columns = Arrays.stream(row).skip(1).collect(Collectors.toList());
            rows.add(rowData);
        }
        puzzleInfo.rows = rows;

        return puzzleInfo;
    }

}
