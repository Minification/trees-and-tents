package de.mariushubatschek.is;

import de.mariushubatschek.is.algorithms.heuristics.variables.scoring.DomWdegVariableScoring;
import de.mariushubatschek.is.algorithms.search.Backtracking;
import de.mariushubatschek.is.algorithms.heuristics.variables.DomVariableHeuristic;
import de.mariushubatschek.is.algorithms.heuristics.values.LexicoValueHeuristic;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import org.junit.jupiter.api.Test;

public class TestBacktracking {

    @Test
    public void testBacktracking() {
        ConstraintSatisfactionProblem constraintSatisfactionProblem = CSPs.getSlideCsp();
        Backtracking backtracking = new Backtracking(new DomWdegVariableScoring(), new LexicoValueHeuristic());
        backtracking.run(constraintSatisfactionProblem);
    }

}
