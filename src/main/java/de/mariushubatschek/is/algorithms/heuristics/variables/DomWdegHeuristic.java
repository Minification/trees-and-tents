package de.mariushubatschek.is.algorithms.heuristics.variables;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Variable;

import java.util.*;

public class DomWdegHeuristic implements VariableHeuristic {

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
            double count = 0;
            for (Constraint c : csp.getConstraintsFor(v)) {
                List<Variable> scope = new ArrayList<>(c.getVariables());
                scope.removeAll(searchAlgorithm.getInstantiationVariables());
                if (scope.size() != 1 || !scope.get(0).equals(v)) {
                    count += searchAlgorithm.getConstraintWeight(c);
                }
            }
            map.put(v, searchAlgorithm.getDomainSize(v) / (count != 0 ? count : count+1));
        }
        return map;
    }

}
