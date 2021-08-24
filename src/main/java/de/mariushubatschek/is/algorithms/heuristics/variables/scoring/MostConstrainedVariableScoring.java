package de.mariushubatschek.is.algorithms.heuristics.variables.scoring;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;

public class MostConstrainedVariableScoring extends AbstractVariableScoring {
    @Override
    public void onInitialize(SearchAlgorithm searchAlgorithm) {

    }

    @Override
    public void onInitializeConstraint(Constraint constraint, SearchAlgorithm searchAlgorithm) {
        for (Variable v : constraint.getVariables()) {
            v.changeWeightBy(1);
        }
    }

    @Override
    public double score(Variable variable, SearchAlgorithm searchAlgorithm) {
        return -variable.getWeight();
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
