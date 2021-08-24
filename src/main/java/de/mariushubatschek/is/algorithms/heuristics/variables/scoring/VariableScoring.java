package de.mariushubatschek.is.algorithms.heuristics.variables.scoring;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;

public interface VariableScoring {

    void reset(final SearchAlgorithm searchAlgorithm);

    void onInitialize(final SearchAlgorithm searchAlgorithm);

    void onInitializeConstraint(final Constraint constraint, final SearchAlgorithm searchAlgorithm);

    double score(final Variable variable, final SearchAlgorithm searchAlgorithm);

    void onDomainWipeout(final Variable variable, final Constraint constraint, final SearchAlgorithm searchAlgorithm);

    void onSelection(final Variable variable, final SearchAlgorithm searchAlgorithm);

    void onSelectionConstraint(final Variable variable, final Constraint constraint, final SearchAlgorithm searchAlgorithm);

    void onBacktrack(final Variable variable, final SearchAlgorithm searchAlgorithm);

    void onBacktrackConstraint(final Variable variable, final Constraint constraint, final SearchAlgorithm searchAlgorithm);

}
