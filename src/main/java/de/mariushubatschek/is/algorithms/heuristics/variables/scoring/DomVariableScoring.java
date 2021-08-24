package de.mariushubatschek.is.algorithms.heuristics.variables.scoring;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;

public class DomVariableScoring extends AbstractVariableScoring {
    @Override
    public void onInitialize(SearchAlgorithm searchAlgorithm) {

    }

    @Override
    public void onInitializeConstraint(Constraint constraint, SearchAlgorithm searchAlgorithm) {

    }

    @Override
    public double score(Variable variable, SearchAlgorithm searchAlgorithm) {
        return searchAlgorithm.getDomainSize(variable);
    }

    @Override
    public void onDomainWipeout(Variable variable, Constraint constraint, SearchAlgorithm searchAlgorithm) {

    }

    @Override
    public void onSelection(Variable variable, SearchAlgorithm searchAlgorithm) {

    }

    @Override
    public void onSelectionConstraint(Variable variable, Constraint constraint, SearchAlgorithm searchAlgorithm) {

    }

    @Override
    public void onBacktrack(Variable variable, SearchAlgorithm searchAlgorithm) {

    }

    @Override
    public void onBacktrackConstraint(Variable variable, Constraint constraint, SearchAlgorithm searchAlgorithm) {

    }
}
