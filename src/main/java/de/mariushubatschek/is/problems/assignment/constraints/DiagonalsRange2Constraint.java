package de.mariushubatschek.is.problems.assignment.constraints;

import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.modeling.VariableAssignment;

import java.util.ArrayList;
import java.util.List;

public class DiagonalsRange2Constraint implements Constraint {

    private final List<Variable> variables = new ArrayList<>();

    private final Variable v1;

    private final Variable v2;

    public DiagonalsRange2Constraint(final Variable v1, final Variable v2) {
        variables.add(v1);
        variables.add(v2);
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public boolean isSatisfied(VariableAssignment variableAssignment) {
        if (variableAssignment.isComplete(variables)){
            int sum = 0;
            int value1 = (Integer) v1.getDomain().getValue(variableAssignment.getValueFor(v1));
            int value2 = (Integer) v2.getDomain().getValue(variableAssignment.getValueFor(v2));
            //Count invalid combinations
            if (value1 == -2 && value2 == -1) {
                sum++;
            }
            if (value1 == 2 && value2 == -1) {
                sum++;
            }
            if (value1 == -2 && value2 == 1) {
                sum++;
            }
            if (value1 == 2 && value2 == 1) {
                sum++;
            }

            if (value1 == 1 && value2 == 2) {
                sum++;
            }
            if (value1 == 1 && value2 == -2) {
                sum++;
            }
            if (value1 == -1 && value2 == 2) {
                sum++;
            }
            if (value1 == -1 && value2 == -2) {
                sum++;
            }
            return sum == 0;
        }

        return false;
    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }

}
