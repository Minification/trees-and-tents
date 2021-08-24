package de.mariushubatschek.is.algorithms.heuristics.variables;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Variable;

public interface VariableHeuristic {

    Variable apply(ConstraintSatisfactionProblem csp, SearchAlgorithm searchAlgorithm);

}
