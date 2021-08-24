package de.mariushubatschek.is.problems.assignment.constraints.diagonals;

import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.problems.assignment.constraints.AbstractBinaryConstraint;

public class D1Bottom extends AbstractBinaryConstraint {

    public D1Bottom(Variable v1, Variable v2) {
        super(v1, v2);
    }

    @Override
    protected boolean implementation(int value1, int value2) {
        return !(Math.abs(value1) == 1 && value2 == -2);
    }

}
