package de.mariushubatschek.is.algorithms.search;

import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Variable;

import java.util.List;

public interface SearchAlgorithm {

    SearchData run(final ConstraintSatisfactionProblem csp);

    List<Variable> getInstantiationVariables();

    int getDomainSize(final Variable variable);

    List<Integer> getDomainValues(final Variable variable);

    List<Variable> getUninstantiatedVariables();

    int getConstraintWeight(final Constraint constraint);

    void onDomainWipeout(final Variable variable, final Constraint constraint);

    List<Variable> getConstraintUninstantiatedVariables(final Constraint constraint);

}
