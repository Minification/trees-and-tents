package de.mariushubatschek.is.data;

import java.util.Arrays;
import java.util.List;

public class PuzzleInfo {

    public List<RowData> rows;

    public int[] maxColumnTentCounts;

    @Override
    public String toString() {
        return "PuzzleInfo{" +
                "rows=" + rows +
                ", columnTentCounts=" + Arrays.toString(maxColumnTentCounts) +
                '}';
    }
}
