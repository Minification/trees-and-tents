package de.mariushubatschek.is.algorithms.heuristics.variables;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Randomly select a variable
 */
public class RandomVariableHeuristic implements VariableHeuristic {

    private Random random;

    public RandomVariableHeuristic(final Random random) {
        this.random = random;
    }

    @Override
    public Variable apply(ConstraintSatisfactionProblem csp, SearchAlgorithm searchAlgorithm) {
        List<Variable> vars = new ArrayList<>(searchAlgorithm.getUninstantiatedVariables());
        int rdm = random.nextInt(vars.size());
        return vars.get(rdm);
    }

}
