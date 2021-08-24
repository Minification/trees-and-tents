package de.mariushubatschek.is.algorithms.heuristics.values;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Variable;

public interface ValueHeuristic {

    int apply(ConstraintSatisfactionProblem csp, SearchAlgorithm searchAlgorithm, Variable x);

}
