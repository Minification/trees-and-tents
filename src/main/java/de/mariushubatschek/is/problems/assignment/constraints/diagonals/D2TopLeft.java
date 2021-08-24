package de.mariushubatschek.is.problems.assignment.constraints.diagonals;

import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.problems.assignment.constraints.AbstractBinaryConstraint;

public class D2TopLeft extends AbstractBinaryConstraint {
    public D2TopLeft(Variable v1, Variable v2) {
        super(v1, v2);
    }

    @Override
    protected boolean implementation(int value1, int value2) {
        return !(value1 == -2 && value2 == 1) && !(value1 == -1 && value2 == 2);
    }
}
