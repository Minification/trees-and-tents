package de.mariushubatschek.is.problems.assignment.constraints;

import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.modeling.VariableAssignment;

import java.util.ArrayList;
import java.util.List;

public class HVRange1And2Constraint implements Constraint {

    private final List<Variable> variables = new ArrayList<>();

    public HVRange1And2Constraint(final Variable v1, final Variable v2) {
        variables.add(v1);
        variables.add(v2);
    }

    @Override
    public boolean isSatisfied(VariableAssignment variableAssignment) {
        if (variableAssignment.isComplete(variables)){
            int sum = 0;
            for(Variable variable : variables){
                int value = (Integer) variable.getDomain().getValue(variableAssignment.getValueFor(variable));
                sum += value;
            }
            return sum != 0;
        }

        return false;
    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }

}
