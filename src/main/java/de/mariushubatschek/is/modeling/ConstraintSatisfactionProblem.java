package de.mariushubatschek.is.modeling;

import java.util.List;

public interface ConstraintSatisfactionProblem {

    List<Variable> getVariables();

    List<Constraint> getConstraints();

    List<Constraint> getConstraintsFor(final Variable variable);

    List<Object> getDomainFor(final Variable variable);

}
