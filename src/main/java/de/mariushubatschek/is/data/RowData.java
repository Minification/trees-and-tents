package de.mariushubatschek.is.data;

import java.util.List;

public class RowData {

    public List<String> columns;

    public int tentCount;

    @Override
    public String toString() {
        return "RowData{" +
                "columns=" + columns +
                ", tentCount=" + tentCount +
                '}';
    }
}
