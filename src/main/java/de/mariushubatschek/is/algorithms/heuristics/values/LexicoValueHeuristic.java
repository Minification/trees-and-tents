package de.mariushubatschek.is.algorithms.heuristics.values;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Variable;

/**
 * Select the first value in the current domain
 */
public class LexicoValueHeuristic implements ValueHeuristic {

    @Override
    public int apply(ConstraintSatisfactionProblem csp, SearchAlgorithm searchAlgorithm, Variable x) {
        return searchAlgorithm.getDomainValues(x).get(0);
    }

}
