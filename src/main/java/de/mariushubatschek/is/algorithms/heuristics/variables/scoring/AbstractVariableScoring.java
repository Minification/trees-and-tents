package de.mariushubatschek.is.algorithms.heuristics.variables.scoring;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.modeling.Variable;

public abstract class AbstractVariableScoring implements VariableScoring {

    @Override
    public void reset(SearchAlgorithm searchAlgorithm) {
        for(final Variable variable : searchAlgorithm.getInstantiationVariables()) {
            variable.setWeight(0);
        }
    }

}
