package de.mariushubatschek.is.algorithms.heuristics.variables.scoring;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomWdegVariableScoring extends AbstractVariableScoring {

    private Map<Constraint, Integer> constraintWeightMap;

    @Override
    public void reset(SearchAlgorithm searchAlgorithm) {
        for(final Variable variable : searchAlgorithm.getInstantiationVariables()) {
            variable.setWeight(1);
        }
    }

    @Override
    public void onInitialize(SearchAlgorithm searchAlgorithm) {
        constraintWeightMap = new HashMap<>();
    }

    @Override
    public void onInitializeConstraint(Constraint constraint, SearchAlgorithm searchAlgorithm) {
        for (Variable v : constraint.getVariables()) {
            v.changeWeightBy(constraintWeightMap.getOrDefault(constraint, 1));
        }
    }

    @Override
    public double score(Variable variable, SearchAlgorithm searchAlgorithm) {
        return searchAlgorithm.getDomainSize(variable) / variable.getWeight();
    }

    @Override
    public void onDomainWipeout(Variable variable, Constraint constraint, SearchAlgorithm searchAlgorithm) {
        List<Variable> uninstantiatedScope = searchAlgorithm.getConstraintUninstantiatedVariables(constraint);
        for (Variable v : uninstantiatedScope) {
            v.changeWeightBy(1);
        }
        constraintWeightMap.put(constraint, constraintWeightMap.getOrDefault(constraint, 1)+1);
    }

    @Override
    public void onSelection(Variable variable, SearchAlgorithm searchAlgorithm) {

    }

    @Override
    public void onSelectionConstraint(Variable variable, Constraint constraint, SearchAlgorithm searchAlgorithm) {
        List<Variable> uninstantiatedScope = searchAlgorithm.getConstraintUninstantiatedVariables(constraint);
        if (uninstantiatedScope.size() == 1) {
            for (Variable v : uninstantiatedScope) {
                v.changeWeightBy(-constraintWeightMap.getOrDefault(constraint, 1));
            }
        }
    }

    @Override
    public void onBacktrack(Variable variable, SearchAlgorithm searchAlgorithm) {

    }

    @Override
    public void onBacktrackConstraint(Variable variable, Constraint constraint, SearchAlgorithm searchAlgorithm) {
        List<Variable> uninstantiatedScope = searchAlgorithm.getConstraintUninstantiatedVariables(constraint);
        if (uninstantiatedScope.size() != 1) {
            for (Variable v : uninstantiatedScope) {
                v.changeWeightBy(constraintWeightMap.getOrDefault(constraint, 1));
            }
        }
    }

}
