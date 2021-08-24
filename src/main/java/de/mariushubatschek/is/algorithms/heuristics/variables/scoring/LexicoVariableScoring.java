package de.mariushubatschek.is.algorithms.heuristics.variables.scoring;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;

import java.util.*;

public class LexicoVariableScoring extends AbstractVariableScoring {

    private Map<Variable, Double> ordering;

    @Override
    public void onInitialize(SearchAlgorithm searchAlgorithm) {
        ordering = new HashMap<>();
        List<Variable> variables = new ArrayList<>(searchAlgorithm.getUninstantiatedVariables());
        variables.sort(Comparator.comparing(Variable::getName));
        int i = 0;
        for (Variable v : variables) {
            ordering.put(v, (double) i);
            i++;
        }
    }

    @Override
    public void onInitializeConstraint(Constraint constraint, SearchAlgorithm searchAlgorithm) {

    }

    @Override
    public double score(Variable variable, SearchAlgorithm searchAlgorithm) {
        return ordering.get(variable);
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
