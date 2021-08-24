package de.mariushubatschek.is.problems.generation;

import com.opencsv.CSVWriter;
import de.mariushubatschek.is.algorithms.util.ExtendedCells;
import de.mariushubatschek.is.data.PuzzleInfo;
import de.mariushubatschek.is.data.RowData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Generator {

    private List<RowCol> rowCols;

    private ExtendedCells[][] cells;

    public PuzzleInfo run(final int width, final int height, final int treeCount) throws IOException {
        cells = new ExtendedCells[height][width];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new ExtendedCells[width];
            Arrays.fill(cells[i], ExtendedCells.UNDETERMINED);
        }
        rowCols = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rowCols.add(new RowCol(j, i));
            }
        }
        Collections.shuffle(rowCols);
        int k = 0;
        while (k < treeCount && !rowCols.isEmpty()) {
            RowCol rowCol = rowCols.remove(0);
            place(rowCol.row, rowCol.col, height, width, ExtendedCells.TREE);
            List<RowCol> eligibleTentPositions = getEligibleTentPositions(rowCol.row, rowCol.col, height, width);
            if (!eligibleTentPositions.isEmpty()) {
                Collections.shuffle(eligibleTentPositions);
                RowCol tentPosition = eligibleTentPositions.get(0);
                placeTent(tentPosition.row, tentPosition.col, height, width);
                k++;
            } else {
                //Force remove temporarily placed tree
                //place(rowCol.row, rowCol.col, height, width, ExtendedCells.UNDETERMINED);
                cells[rowCol.row][rowCol.col] = ExtendedCells.UNDETERMINED;
                //rowCols.add(rowCol);
            }
        }
        PuzzleInfo puzzleInfo = new PuzzleInfo();
        puzzleInfo.rows = new ArrayList<>();
        int[] colData = new int[width];
        for (int i = 0; i < height; i++) {
            RowData rowData = new RowData();
            rowData.columns = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                ExtendedCells type = cells[i][j];
                if (type == ExtendedCells.TREE) {
                    rowData.columns.add("t");
                } else {
                    rowData.columns.add("");
                }
                if (type == ExtendedCells.TENT) {
                    rowData.tentCount++;
                    colData[j] = colData[j] + 1;
                }
            }
            puzzleInfo.rows.add(rowData);
        }
        puzzleInfo.maxColumnTentCounts = colData;

        for (int i = 0; i < colData.length; i++) {
            System.out.print(colData[i]);
        }
        System.out.println();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                switch (cells[i][j]) {
                    case TENT:
                        System.out.print("E");
                        break;
                    case UNDETERMINED:
                        System.out.print("U");
                        break;
                    case TREE:
                        System.out.print("T");
                        break;
                    case BLANK:
                        System.out.print("B");
                        break;
                }
            }
            System.out.println("|"+puzzleInfo.rows.get(i).tentCount);
        }

        String[] cols = new String[colData.length+1];
        cols[0] = "";
        for (int i = 1; i < cols.length; i++) {
            cols[i] = String.valueOf(colData[i-1]);
        }

        File file = new File(System.getProperty("user.dir") + File.separator + "generatedPuzzles" + File.separator + "generated.csv");
        file.mkdirs();
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        CSVWriter writer = new CSVWriter(new FileWriter(file), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
        writer.writeNext(cols);
        for (RowData rowData : puzzleInfo.rows) {
            String[] row = new String[rowData.columns.size() + 1];
            row[0] = String.valueOf(rowData.tentCount);
            for (int i = 1; i < row.length; i++) {
                row[i] = String.valueOf(rowData.columns.get(i-1));
            }
            writer.writeNext(row);
        }
        writer.close();
        return puzzleInfo;
    }

    private List<RowCol> getEligibleTentPositions(int row, int col, int rows, int cols) {
        //Fix
        List<RowCol> eligiblePositions = new ArrayList<>();
        for (int r : Arrays.asList(-1, 0, 1)) {
            for (int c : Arrays.asList(-1, 0, 1)) {
                if (r == -1 && c == -1) {
                    continue;
                }
                if (r == -1 && c == 1) {
                    continue;
                }
                if (r == 1 && c == -1) {
                    continue;
                }
                if (r == 1 && c == 1) {
                    continue;
                }
                if (((r == 0) ^ (c == 0)) && in(row+r, col+c, rows, cols) && cells[row+r][col+c] == ExtendedCells.UNDETERMINED) {
                    //if no (y, x) around (r, c) is a tent
                    if (!hasTentAround(row+r, col+c, rows, cols)) {
                        eligiblePositions.add(new RowCol(row+r, col+c));
                    }
                }
            }
        }
        return eligiblePositions;
    }

    private boolean hasTentAround(int row, int col, int rows, int cols) {
        for (int r : Arrays.asList(-1, 0, 1)) {
            for (int c : Arrays.asList(-1, 0, 1)) {
                if (!(r == 0 && c == 0) && in(row+r, col+c, rows, cols) && cells[row+r][col+c] == ExtendedCells.TENT) {
                    return true;
                }
            }
        }
        return false;
    }

    private void placeTent(int row, int col, int rows, int cols) {
        place(row, col, rows, cols, ExtendedCells.TENT);
        place(row-1, col, rows, cols, ExtendedCells.BLANK);
        place(row+1, col, rows, cols, ExtendedCells.BLANK);
        place(row, col-1, rows, cols, ExtendedCells.BLANK);
        place(row, col+1, rows, cols, ExtendedCells.BLANK);
        place(row-1, col-1, rows, cols, ExtendedCells.BLANK);
        place(row-1, col+1, rows, cols, ExtendedCells.BLANK);
        place(row+1, col-1, rows, cols, ExtendedCells.BLANK);
        place(row+1, col+1, rows, cols, ExtendedCells.BLANK);
    }

    private void place(int row, int col, int rows, int cols, ExtendedCells value) {
        if (in(row, col, rows, cols) && cells[row][col] == ExtendedCells.UNDETERMINED) {
            cells[row][col] = value;
            rowCols.removeIf(rc -> rc.row == row && rc.col == col);
        }
    }

    private boolean in(int row, int col, int rows, int cols) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    private static class RowCol {
        public int row;
        public int col;
        public RowCol(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    public static void main(String[] args) throws IOException {
        new Generator().run(240, 140, 1500);
    }

}
