package de.mariushubatschek.is.algorithms.util;

import java.util.Objects;

public class Residue {

    public Arc arc;

    public int value;

    public Residue(Arc arc, int value) {
        this.arc = arc;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Residue residue = (Residue) o;
        return value == residue.value &&
                Objects.equals(arc, residue.arc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arc, value);
    }
}
