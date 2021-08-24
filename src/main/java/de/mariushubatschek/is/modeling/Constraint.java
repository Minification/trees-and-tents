package de.mariushubatschek.is.modeling;

import java.util.List;

public interface Constraint {

    boolean isSatisfied(final VariableAssignment variableAssignment);

    List<Variable> getVariables();

}
