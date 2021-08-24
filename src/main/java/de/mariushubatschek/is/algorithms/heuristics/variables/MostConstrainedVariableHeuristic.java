package de.mariushubatschek.is.algorithms.heuristics.variables;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Variable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Get the variable with highest constraint count
 */
public class MostConstrainedVariableHeuristic implements VariableHeuristic {

    @Override
    public Variable apply(ConstraintSatisfactionProblem csp, SearchAlgorithm searchAlgorithm) {
        List<Variable> vars = new ArrayList<>(searchAlgorithm.getUninstantiatedVariables());
        Comparator<Variable> comparator = Comparator.comparingInt(o -> csp.getConstraintsFor(o).size());
        vars.sort(comparator.reversed());
        return vars.get(0);
    }

}
