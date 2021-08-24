package de.mariushubatschek.is.algorithms.util;

import de.mariushubatschek.is.modeling.Variable;

import java.util.Objects;

public class VariableValue {

    public Variable variable;

    public int value;

    public VariableValue(final Variable variable, final int value) {
        this.variable = variable;
        this.value = value;
    }

    @Override
    public String toString() {
        return "VariableValue{" +
                "variable=" + variable +
                ", value=" + variable.getDomain().getValue(value) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableValue that = (VariableValue) o;
        return value == that.value &&
                Objects.equals(variable, that.variable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, value);
    }
}
