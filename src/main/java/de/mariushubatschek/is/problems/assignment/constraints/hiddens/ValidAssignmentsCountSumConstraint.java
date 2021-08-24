package de.mariushubatschek.is.problems.assignment.constraints.hiddens;

import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.modeling.VariableAssignment;

import java.util.ArrayList;
import java.util.List;

public class ValidAssignmentsCountSumConstraint implements Constraint {

    private List<Variable> variables = new ArrayList<>();

    private Variable v1;
    private Variable v2;
    private Variable helper;
    private List<Integer> vals1;
    private List<Integer> vals2;
    private int requiredSum;

    public ValidAssignmentsCountSumConstraint(Variable v1, Variable v2, Variable helper, List<Integer> vals1, List<Integer> vals2) {
        variables.add(v1);
        variables.add(v2);
        variables.add(helper);
        this.v1 = v1;
        this.v2 = v2;
        this.helper = helper;
        this.vals1 = vals1;
        this.vals2 = vals2;
    }

    public ValidAssignmentsCountSumConstraint(Variable v1, Variable v2, int requiredSum, List<Integer> vals1, List<Integer> vals2) {
        variables.add(v1);
        variables.add(v2);
        this.v1 = v1;
        this.v2 = v2;
        this.requiredSum = requiredSum;
        this.vals1 = vals1;
        this.vals2 = vals2;
    }

    @Override
    public boolean isSatisfied(VariableAssignment variableAssignment) {
        if (variableAssignment.isComplete(variables)) {
            int sum = 0;
            //count in v1
            int val1 = (int) v1.getDomain().getValue(variableAssignment.getValueFor(v1));
            for (int val : vals1) {
                if (val1 == val) {
                    sum++;
                }
            }
            //count in v2
            int val2 = (int) v2.getDomain().getValue(variableAssignment.getValueFor(v2));
            for (int val : vals2) {
                if (val2 == val) {
                    sum++;
                }
            }
            int h = helper != null ? (int) helper.getDomain().getValue(variableAssignment.getValueFor(helper)) : requiredSum;
            return sum == h;
        }
        return false;
    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "ValidAssignmentsCountSumConstraint{" +
                "variables=" + variables +
                ", v1=" + v1 +
                ", v2=" + v2 +
                ", helper=" + helper +
                ", vals1=" + vals1 +
                ", vals2=" + vals2 +
                ", requiredSum=" + requiredSum +
                '}';
    }
}
