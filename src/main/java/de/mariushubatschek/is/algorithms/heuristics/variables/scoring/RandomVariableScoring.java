package de.mariushubatschek.is.algorithms.heuristics.variables.scoring;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;

import java.util.Random;

public class RandomVariableScoring extends AbstractVariableScoring {

    private Random random;

    public RandomVariableScoring(final Random random) {
        this.random = random;
    }

    @Override
    public void onInitialize(SearchAlgorithm searchAlgorithm) {

    }

    @Override
    public void onInitializeConstraint(Constraint constraint, SearchAlgorithm searchAlgorithm) {

    }

    @Override
    public double score(Variable variable, SearchAlgorithm searchAlgorithm) {
        return random.nextDouble();
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
