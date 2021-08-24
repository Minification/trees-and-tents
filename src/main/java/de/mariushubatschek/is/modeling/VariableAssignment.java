package de.mariushubatschek.is.modeling;

import java.util.*;

/**
 * A variable assignment maps variables to values, i.e. it is a vector function from variables to values
 */
public class VariableAssignment {

    private Map<Variable, Integer> variableValueMap = new HashMap<>();

    public List<Variable> getVariables() {
        return new ArrayList<>(variableValueMap.keySet());
    }

    public int getValueFor(final Variable variable) {
        return variableValueMap.get(variable);
    }

    /**
     * An assignment for a set of variables is consistent wrt. a set of constraints each constraint is satisfied for it.
     * @param constraints
     * @return
     */
    public boolean isConsistent(final List<Constraint> constraints) {
        for (Constraint constraint : constraints) {
            if (!constraint.isSatisfied(this)) {
                return false;
            }
        }
        return true;
    }

    /**
     * An assignment for a set of variables is a solution to a CSP, if it is complete and consistent.
     * @param csp
     * @return
     */
    public boolean isSolution(final ConstraintSatisfactionProblem csp) {
        return isComplete(csp.getVariables()) && isConsistent(csp.getConstraints());
    }

    /**
     * A variable assignment is complete wrt. a set of variables A, if it is well-defined for A.
     * @param variables
     * @return
     */
    public boolean isComplete(final List<Variable> variables) {
        for (final Variable variable : variables) {
            if (!hasAssignmentFor(variable)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAssignmentFor(final Variable variable) {
        return variableValueMap.get(variable) != null;
    }

    public void assignValueTo(final Variable variable, final int value) {
        if (value < 0) {
            System.out.println("Variable: " + variable);
            System.out.println("Domaininfo: ");
            variable.getDomain().printInfo();
            System.out.println("Value: " + value);
            throw new ArrayIndexOutOfBoundsException();
        }
        variableValueMap.put(variable, value);
    }

    public void removeValueAssignmentFor(final Variable variable) {
        variableValueMap.remove(variable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Variable v : getVariables()) {
            sb.append(v.getName()).append("=").append(getValueFor(v)).append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public VariableAssignment copy() {
        Map<Variable, Integer> map = new HashMap<>(variableValueMap);
        VariableAssignment copy = new VariableAssignment();
        copy.variableValueMap = map;
        return copy;
    }

}
