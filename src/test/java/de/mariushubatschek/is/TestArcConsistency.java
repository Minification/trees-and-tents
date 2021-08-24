package de.mariushubatschek.is;

import de.mariushubatschek.is.algorithms.heuristics.variables.DomVariableHeuristic;
import de.mariushubatschek.is.algorithms.heuristics.values.LexicoValueHeuristic;
import de.mariushubatschek.is.algorithms.heuristics.variables.scoring.DomVariableScoring;
import de.mariushubatschek.is.algorithms.propagation.ArcConsistencyAlgorithm;
import de.mariushubatschek.is.algorithms.search.*;
import de.mariushubatschek.is.modeling.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestArcConsistency {

    @Test
    public void testAC() {
        ArcConsistencyAlgorithm arcConsistencyAlgorithm = new ArcConsistencyAlgorithm();

        ConstraintSatisfactionProblem csp = CSPs.getSlideCsp();

        assertTrue(arcConsistencyAlgorithm.run(csp, new SearchAlgorithm() {

            @Override
            public SearchData run(ConstraintSatisfactionProblem csp) {
                return null;
            }

            @Override
            public List<Variable> getInstantiationVariables() {
                return null;
            }

            @Override
            public int getDomainSize(Variable variable) {
                return 0;
            }

            @Override
            public List<Integer> getDomainValues(Variable variable) {
                return null;
            }

            @Override
            public List<Variable> getUninstantiatedVariables() {
                return null;
            }

            @Override
            public int getConstraintWeight(Constraint constraint) {
                return 0;
            }

            @Override
            public void onDomainWipeout(Variable variable, Constraint constraint) {

            }

            @Override
            public List<Variable> getConstraintUninstantiatedVariables(Constraint constraint) {
                return null;
            }
        }));

        for (Variable variable : csp.getVariables()) {
            System.out.println(variable.getName() + variable.getDomain());
        }
    }

    @Test
    public void testACNumber2() {
        GeneralSearch generalSearch = new GeneralSearch(new DomVariableScoring(), new LexicoValueHeuristic());
        generalSearch.setLookAhead(LookAhead.MAC);
        generalSearch.setLookBack(LookBack.SBT);

        ConstraintSatisfactionProblem csp = CSPs.getSlideCsp();
        generalSearch.initialize(csp);

        assertNull(generalSearch.enforceGAC(csp, csp.getVariables()));

        for (Variable variable : csp.getVariables()) {
            System.out.println(variable.getName() + ", " + generalSearch.getDomainValues(variable));
        }
    }

    @Test
    public void testACVar() {
        GeneralSearch generalSearch = new GeneralSearch(new DomVariableScoring(), new LexicoValueHeuristic());
        generalSearch.setLookAhead(LookAhead.MAC);
        generalSearch.setLookBack(LookBack.SBT);

        ConstraintSatisfactionProblem csp = CSPs.getSlideCsp();
        generalSearch.initialize(csp);

        assertNull(generalSearch.enforceGACVar(csp, csp.getVariables()));

        for (Variable variable : csp.getVariables()) {
            System.out.println(variable.getName() + ", " + generalSearch.getDomainValues(variable));
        }
    }

}
