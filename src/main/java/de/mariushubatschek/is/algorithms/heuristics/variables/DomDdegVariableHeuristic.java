package de.mariushubatschek.is.algorithms.heuristics.variables;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Variable;

import java.util.*;

/**
 * Select the variable with the smallest domain to dynamic degree ratio
 */
public class DomDdegVariableHeuristic implements VariableHeuristic {

    @Override
    public Variable apply(ConstraintSatisfactionProblem csp, SearchAlgorithm searchAlgorithm) {
        List<Variable> vars = new ArrayList<>(searchAlgorithm.getUninstantiatedVariables());
        Map<Variable, Double> ratioMap = buildRatioMap(csp, searchAlgorithm, vars);
        Comparator<Variable> comparator = Comparator.comparingDouble(ratioMap::get);
        vars.sort(comparator);
        return vars.get(0);
    }

    private Map<Variable, Double> buildRatioMap(final ConstraintSatisfactionProblem csp, final SearchAlgorithm searchAlgorithm, final List<Variable> vars) {
        Map<Variable, Double> map = new HashMap<>();
        for (Variable v : vars) {
            double count = 1;
            List<Constraint> constraints = csp.getConstraintsFor(v);
            for (Constraint c : constraints) {
                for (Variable other : c.getVariables()) {
                    if (!other.equals(v) && vars.contains(other)) {
                        count++;
                    }
                }
            }
            map.put(v, searchAlgorithm.getDomainSize(v) / count);
        }
        return map;
    }

}
