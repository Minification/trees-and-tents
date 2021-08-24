package de.mariushubatschek.is.problems.assignment.constraints;

import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.modeling.VariableAssignment;

import java.util.ArrayList;
import java.util.List;

public class TentColumnCountConstraint2 implements Constraint {

    private final List<Variable> variables = new ArrayList<>();

    private final int columnCount;

    private List<Variable> variablesOnColumn = new ArrayList<>();

    private List<Variable> variablesLeftFromColumn = new ArrayList<>();

    private List<Variable> variablesRightFromColumn = new ArrayList<>();

    public TentColumnCountConstraint2(int columnCount, List<Variable> variablesOnColumn, List<Variable> variablesLeftFromColumn, List<Variable> variablesRightFromColumn){
        variables.addAll(variablesOnColumn);
        variables.addAll(variablesLeftFromColumn);
        variables.addAll(variablesRightFromColumn);
        this.variablesOnColumn.addAll(variablesOnColumn);
        this.variablesLeftFromColumn.addAll(variablesLeftFromColumn);
        this.variablesRightFromColumn.addAll(variablesRightFromColumn);
        this.columnCount = columnCount;
    }

    @Override
    public boolean isSatisfied(VariableAssignment variableAssignment) {
        if (variableAssignment.isComplete(variables)){
            int count = 0;
            for(Variable variable : variablesOnColumn){
                int value = (Integer) variable.getDomain().getValue(variableAssignment.getValueFor(variable));
                if(Math.abs(value) == 2){
                    count++;
                }
            }
            for(Variable variable : variablesLeftFromColumn){
                int value = (Integer) variable.getDomain().getValue(variableAssignment.getValueFor(variable));
                if(value == 1){
                    count++;
                }
            }
            for(Variable variable : variablesRightFromColumn){
                int value = (Integer) variable.getDomain().getValue(variableAssignment.getValueFor(variable));
                if(value == -1){
                    count++;
                }
            }
            return count == columnCount;
        }

        return false;
    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "TentColumnCountConstraint2{" +
                "variables=" + variables +
                ", columnCount=" + columnCount +
                '}';
    }

    public List<Variable> getVariablesOnColumn() {
        return variablesOnColumn;
    }

    public List<Variable> getVariablesLeftFromColumn() {
        return variablesLeftFromColumn;
    }

    public List<Variable> getVariablesRightFromColumn() {
        return variablesRightFromColumn;
    }

    public int getColumnCount() {
        return columnCount;
    }
}
