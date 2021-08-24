package de.mariushubatschek.is;

import de.mariushubatschek.is.modeling.*;

import java.util.ArrayList;
import java.util.List;

public class CSPs {

    public static ConstraintSatisfactionProblem getSlideCsp() {
        return new SlideExample();
    }

    /**
     * Example: IntSys_05_ext.pdf, slide 45
     */
    private static class SlideExample implements ConstraintSatisfactionProblem {

        private List<Variable> variables = new ArrayList<>();

        private List<Constraint> constraints = new ArrayList<>();

        public SlideExample() {
            Domain D_A = new Domain(new Integer[] {2, 3, 4, 5});
            Variable A = new Variable("A", D_A);

            Domain D_B = new Domain(new Integer[] {2, 3, 4, 5});
            Variable B = new Variable("B", D_B);

            Domain D_C = new Domain(new Integer[] {1, 2, 3});
            Variable C = new Variable("C", D_C);

            Constraint AB = new FirstOneLessThanSecondConstraint(A, B);
            Constraint AC = new GreaterThanConstraint(A, C);
            Constraint BC = new FirstIsHalfOfSecondPlusOneConstraint(C, B);

            variables.add(A);
            variables.add(B);
            variables.add(C);

            constraints.add(AB);
            constraints.add(AC);
            constraints.add(BC);
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
            return null;
        }
    }

    private static class GreaterThanConstraint implements Constraint {

        private List<Variable> variables = new ArrayList<>();

        private Variable lhs;

        private Variable rhs;

        public GreaterThanConstraint(final Variable lhs, final Variable rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
            variables.add(lhs);
            variables.add(rhs);
        }

        @Override
        public boolean isSatisfied(VariableAssignment variableAssignment) {
            if (variableAssignment.isComplete(variables)) {
                Integer lhsValue = (Integer) lhs.getDomain().getValue(variableAssignment.getValueFor(lhs));
                Integer rhsValue = (Integer) rhs.getDomain().getValue(variableAssignment.getValueFor(rhs));
                return lhsValue > rhsValue;
            }
            return false;
        }

        @Override
        public List<Variable> getVariables() {
            return variables;
        }

    }

    private static class FirstOneLessThanSecondConstraint implements Constraint {

        private List<Variable> variables = new ArrayList<>();

        private Variable lhs;

        private Variable rhs;

        public FirstOneLessThanSecondConstraint(final Variable lhs, final Variable rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
            variables.add(lhs);
            variables.add(rhs);
        }

        @Override
        public boolean isSatisfied(VariableAssignment variableAssignment) {
            if (variableAssignment.isComplete(variables)) {
                Integer lhsValue = (Integer) lhs.getDomain().getValue(variableAssignment.getValueFor(lhs));
                Integer rhsValue = (Integer) rhs.getDomain().getValue(variableAssignment.getValueFor(rhs));
                //System.out.println(lhsValue + ", " + rhsValue);
                return lhsValue == rhsValue - 1;
            }
            return false;
        }

        @Override
        public List<Variable> getVariables() {
            return variables;
        }

    }

    private static class FirstIsHalfOfSecondPlusOneConstraint implements Constraint {

        private List<Variable> variables = new ArrayList<>();

        private Variable lhs;

        private Variable rhs;

        public FirstIsHalfOfSecondPlusOneConstraint(final Variable lhs, final Variable rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
            variables.add(lhs);
            variables.add(rhs);
        }

        @Override
        public boolean isSatisfied(VariableAssignment variableAssignment) {
            if (variableAssignment.isComplete(variables)) {
                Integer lhsValue = (Integer) lhs.getDomain().getValue(variableAssignment.getValueFor(lhs));
                Integer rhsValue = (Integer) rhs.getDomain().getValue(variableAssignment.getValueFor(rhs));
                return lhsValue == (rhsValue + 1) / 2;
            }
            return false;
        }

        @Override
        public List<Variable> getVariables() {
            return variables;
        }

    }

}
