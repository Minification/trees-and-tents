package de.mariushubatschek.is.problems.assignment.constraints.hv;

import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.problems.assignment.constraints.AbstractBinaryConstraint;

public class HVLeft extends AbstractBinaryConstraint {

    public HVLeft(final Variable v1, final Variable v2) {
        super(v1, v2);
    }

    @Override
    protected boolean implementation(int value1, int value2) {
            return !(value1 == -1 && value2 == 1);
    }

}
