package de.mariushubatschek.is.problems.assignment.constraints;

import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.modeling.VariableAssignment;

import java.util.ArrayList;
import java.util.List;

public class DiagonalsRange2With1Different implements Constraint {

    private final List<Variable> variables = new ArrayList<>();

    private final Variable v1;

    private final Variable v2;

    public DiagonalsRange2With1Different(final Variable v1, final Variable v2) {
        variables.add(v1);
        variables.add(v2);
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public boolean isSatisfied(VariableAssignment variableAssignment) {
        if (variableAssignment.isComplete(variables)){
            int value1 = (Integer) v1.getDomain().getValue(variableAssignment.getValueFor(v1));
            int value2 = (Integer) v2.getDomain().getValue(variableAssignment.getValueFor(v2));
            //Count invalid combinations
            return Math.abs(value1) != Math.abs(value2) || value1 == value2;
        }

        return false;
    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }

}
