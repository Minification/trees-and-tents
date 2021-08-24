package de.mariushubatschek.is.algorithms.heuristics.variables;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Variable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Select the variable with the smallest domain size
 */
public class DomVariableHeuristic implements VariableHeuristic {
    @Override
    public Variable apply(ConstraintSatisfactionProblem csp, SearchAlgorithm searchAlgorithm) {
        List<Variable> vars = new ArrayList<>(searchAlgorithm.getUninstantiatedVariables());
        Comparator<Variable> comparator = Comparator.comparingInt(searchAlgorithm::getDomainSize);
        vars.sort(comparator);
        return vars.get(0);
    }
}
