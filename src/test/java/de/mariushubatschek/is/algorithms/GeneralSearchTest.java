package de.mariushubatschek.is.algorithms;

import de.mariushubatschek.is.CSPs;
import de.mariushubatschek.is.algorithms.heuristics.values.LexicoValueHeuristic;
import de.mariushubatschek.is.algorithms.heuristics.variables.scoring.DomVariableScoring;
import de.mariushubatschek.is.algorithms.heuristics.variables.scoring.LexicoVariableScoring;
import de.mariushubatschek.is.algorithms.search.GeneralSearch;
import de.mariushubatschek.is.algorithms.search.LookAhead;
import de.mariushubatschek.is.algorithms.search.LookBack;
import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Domain;
import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.problems.assignment.constraints.AbstractBinaryConstraint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class GeneralSearchTest {

    @Test
    public void testBacktracking() {
        ConstraintSatisfactionProblem constraintSatisfactionProblem = CSPs.getSlideCsp();
        GeneralSearch backtracking = new GeneralSearch(new DomVariableScoring(), new LexicoValueHeuristic());
        backtracking.setLookBack(LookBack.SBT);
        backtracking.setLookAhead(LookAhead.MAC);
        backtracking.run(constraintSatisfactionProblem);
    }

    @Test
    public void testBT_IBT() {
        ConstraintSatisfactionProblem constraintSatisfactionProblem = new CSP();
        GeneralSearch backtracking = new GeneralSearch(new LexicoVariableScoring(), new LexicoValueHeuristic());
        backtracking.setLookAhead(LookAhead.BC);
        backtracking.setLookBack(LookBack.IBT);
        backtracking.run(constraintSatisfactionProblem);
    }

    private static class CSP implements ConstraintSatisfactionProblem {

        private List<Variable> variables = new ArrayList<>();

        private List<Constraint> constraints = new ArrayList<>();

        public CSP() {
            Variable x1 = new Variable("x1", new Domain(new Integer[] {1, 2}));
            Variable x2 = new Variable("x2", new Domain(new Integer[] {1, 2}));
            Variable x3 = new Variable("x3", new Domain(new Integer[] {1, 2}));
            Variable x4 = new Variable("x4", new Domain(new Integer[] {1, 2}));
            Variable x5 = new Variable("x5", new Domain(new Integer[] {1, 2}));
            Variable x6 = new Variable("x6", new Domain(new Integer[] {1, 2}));
            variables.addAll(Arrays.asList(x1, x2, x3, x4, x5, x6));

            constraints.add(new EqualityConstraint(x1, x2));
            constraints.add(new EqualityConstraint(x2, x3));
            constraints.add(new EqualityConstraint(x2, x4));
            constraints.add(new EqualityConstraint(x3, x4));
            constraints.add(new InequalityConstraint(x1, x5));
            constraints.add(new InequalityConstraint(x1, x6));
            constraints.add(new InequalityConstraint(x5, x6));
        }

        @Override
        public List<Variable> getVariables() {
            return variables;
        }

        @Override
        public List<Constraint> getConstraints() {
            return constraints;
        }

        @Override
        public List<Constraint> getConstraintsFor(Variable variable) {
            List<Constraint> cs = new ArrayList<>();
            for (Constraint c : constraints) {
                if (c.getVariables().contains(variable)) {
                    cs.add(c);
                }
            }
            return cs;
        }

        @Override
        public List<Object> getDomainFor(Variable variable) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    private static class EqualityConstraint extends AbstractBinaryConstraint {
        public EqualityConstraint(Variable v1, Variable v2) {
            super(v1, v2);
        }

        @Override
        protected boolean implementation(int value1, int value2) {
            return value1 == value2;
        }
    }

    private static class InequalityConstraint extends AbstractBinaryConstraint {
        public InequalityConstraint(Variable v1, Variable v2) {
            super(v1, v2);
        }

        @Override
        protected boolean implementation(int value1, int value2) {
            return value1 != value2;
        }
    }

}