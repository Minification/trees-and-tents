package de.mariushubatschek.is.algorithms.util;

import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;

import java.util.Objects;

public class Arc {

    public Variable variable;

    public Constraint constraint;

    public Arc(Constraint constraint, Variable variable) {
        this.constraint = constraint;
        this.variable = variable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arc arc = (Arc) o;
        return Objects.equals(variable, arc.variable) &&
                Objects.equals(constraint, arc.constraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, constraint);
    }

    @Override
    public String toString() {
        return "Arc{" +
                "variable=" + variable.getName() +
                ", constraint=" + constraint +
                '}';
    }
}
