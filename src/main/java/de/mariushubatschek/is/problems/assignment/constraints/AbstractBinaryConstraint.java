package de.mariushubatschek.is.problems.assignment.constraints;

import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.modeling.VariableAssignment;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBinaryConstraint implements Constraint {

    private final List<Variable> variables = new ArrayList<>();

    private final Variable v1;

    private final Variable v2;

    public AbstractBinaryConstraint(final Variable v1, final Variable v2) {
        variables.add(v1);
        variables.add(v2);
        this.v1 = v1;
        this.v2 = v2;
    }

    protected abstract boolean implementation(final int value1, final int value2);

    @Override
    public boolean isSatisfied(VariableAssignment variableAssignment) {
        if (variableAssignment.isComplete(variables)){
            int value1 = (Integer) v1.getDomain().getValue(variableAssignment.getValueFor(v1));
            int value2 = (Integer) v2.getDomain().getValue(variableAssignment.getValueFor(v2));
            return implementation(value1, value2);
        }

        return false;
    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "v1=" + v1 +
                ", v2=" + v2 +
                '}';
    }
}
