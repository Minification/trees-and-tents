package de.mariushubatschek.is.algorithms.heuristics.values;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Variable;

import java.util.List;
import java.util.Random;

/**
 * Select a random value
 */
public class RandomValueHeuristic implements ValueHeuristic {

    private Random random;

    public RandomValueHeuristic(final Random random) {
        this.random = random;
    }

    @Override
    public int apply(ConstraintSatisfactionProblem csp, SearchAlgorithm searchAlgorithm, Variable x) {
        List<Integer> values = searchAlgorithm.getDomainValues(x);
        int index = random.nextInt(values.size());
        return values.get(index);
    }
}
