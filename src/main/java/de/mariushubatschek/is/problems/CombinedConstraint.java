package de.mariushubatschek.is.problems;

import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.modeling.VariableAssignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombinedConstraint implements Constraint {

    private Set<Constraint> constraints = new HashSet<>();

    private List<Variable> variables = new ArrayList<>();

    public CombinedConstraint(final List<Constraint> constraints) {
        this.constraints.addAll(constraints);
        variables.addAll(constraints.get(0).getVariables());
    }

    @Override
    public boolean isSatisfied(VariableAssignment variableAssignment) {
        //System.out.println("Checking if satisfied: " + this);
        for (final Constraint constraint : constraints) {
            //System.out.println("Constraint inside: " + constraint);
            if (!constraint.isSatisfied(variableAssignment)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "CombinedConstraint{" +
                "constraints=" + constraints +
                ", variables=" + variables +
                '}';
    }
}
