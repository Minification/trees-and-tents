package de.mariushubatschek.is.problems.assignment.constraints;

import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.modeling.VariableAssignment;

import java.util.ArrayList;
import java.util.List;

public class TentRowCountConstraint2 implements Constraint {

    private final List<Variable> variables = new ArrayList<>();

    private final int rowCount;

    private List<Variable> variablesOnRow = new ArrayList<>();

    private List<Variable> variablesAboveRow = new ArrayList<>();

    private List<Variable> variablesBelowRow = new ArrayList<>();

    public TentRowCountConstraint2(int rowCount, List<Variable> variablesOnRow, List<Variable> variablesAboveRow, List<Variable> variablesBelowRow){
        variables.addAll(variablesOnRow);
        variables.addAll(variablesAboveRow);
        variables.addAll(variablesBelowRow);
        this.variablesOnRow.addAll(variablesOnRow);
        this.variablesAboveRow.addAll(variablesAboveRow);
        this.variablesBelowRow.addAll(variablesBelowRow);
        this.rowCount = rowCount;
    }

    @Override
    public boolean isSatisfied(VariableAssignment variableAssignment) {
        if (variableAssignment.isComplete(variables)){
            int count = 0;
            for(Variable variable : variablesOnRow){
                int value = (Integer) variable.getDomain().getValue(variableAssignment.getValueFor(variable));
                if(Math.abs(value) == 1){
                    count++;
                }
            }
            for(Variable variable : variablesBelowRow){
                int value = (Integer) variable.getDomain().getValue(variableAssignment.getValueFor(variable));
                if(value == -2){
                    count++;
                }
            }
            for(Variable variable : variablesAboveRow){
                int value = (Integer) variable.getDomain().getValue(variableAssignment.getValueFor(variable));
                if(value == 2){
                    count++;
                }
            }
            return count == rowCount;
        }

        return false;
    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "TentRowCountConstraint2{" +
                "variables=" + variables +
                ", rowCount=" + rowCount +
                '}';
    }

    public List<Variable> getVariablesAboveRow() {
        return variablesAboveRow;
    }

    public List<Variable> getVariablesBelowRow() {
        return variablesBelowRow;
    }

    public List<Variable> getVariablesOnRow() {
        return variablesOnRow;
    }

    public int getRowCount() {
        return rowCount;
    }
}
