package de.mariushubatschek.is.problems.assignment.constraints.hiddens;

import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.modeling.VariableAssignment;

import java.util.ArrayList;
import java.util.List;

public class AddCountConstraint implements Constraint {
    private List<Variable> variables = new ArrayList<>();

    private Variable helper1;
    private Variable v2;
    private Variable helper2;
    private List<Integer> vals1;
    private int requiredSum;

    public AddCountConstraint(Variable helper1, Variable v2, Variable helper2, List<Integer> vals1) {
        variables.add(helper1);
        variables.add(v2);
        variables.add(helper2);
        this.helper1 = helper1;
        this.v2 = v2;
        this.helper2 = helper2;
        this.vals1 = vals1;
    }

    public AddCountConstraint(Variable helper1, Variable v2, int requiredSum, List<Integer> vals1) {
        variables.add(helper1);
        variables.add(v2);
        this.helper1 = helper1;
        this.v2 = v2;
        this.requiredSum = requiredSum;
        this.vals1 = vals1;
    }

    @Override
    public boolean isSatisfied(VariableAssignment variableAssignment) {
        if (variableAssignment.isComplete(variables)) {
            int sum = (int) helper1.getDomain().getValue(variableAssignment.getValueFor(helper1));
            //count in v2
            int val1 = (int) v2.getDomain().getValue(variableAssignment.getValueFor(v2));
            for (int val : vals1) {
                if (val1 == val) {
                    sum++;
                }
            }
            int h2 = helper2 != null ? (int) helper2.getDomain().getValue(variableAssignment.getValueFor(helper2)) : requiredSum;
            return sum == h2;
        }
        return false;
    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "AddCountConstraint{" +
                "variables=" + variables +
                ", helper1=" + helper1 +
                ", v2=" + v2 +
                ", helper2=" + helper2 +
                ", vals1=" + vals1 +
                ", requiredSum=" + requiredSum +
                '}';
    }
}
