package de.mariushubatschek.is.algorithms.propagation;

import de.mariushubatschek.is.algorithms.search.SearchAlgorithm;
import de.mariushubatschek.is.algorithms.util.Arc;
import de.mariushubatschek.is.algorithms.util.Residue;
import de.mariushubatschek.is.modeling.*;

import java.util.*;

/**
 * Christophe Lecoutre, Constraint Networks: Techniques and Algorithms, pp. 189, September, 2009, John Wiley & Sons,
 */
public class ArcConsistencyAlgorithm {

    private Map<Residue, VariableAssignment> residues = new HashMap<>();

    private boolean use2001 = false;

    /**
     * Standalone application
     * @param csp The csp
     * @param searchAlgorithm
     * @return
     */
    public boolean run(final ConstraintSatisfactionProblem csp, SearchAlgorithm searchAlgorithm) {
        return run(csp, csp.getVariables(), new ArrayList<>(), searchAlgorithm);
    }

    /**
     *
     * @param csp The csp
     * @param event set of variables for which an event occurred just before
     * @param past The set of already considered variables
     * @param searchAlgorithm
     * @return
     */
    public boolean run(final ConstraintSatisfactionProblem csp, final List<Variable> event, final List<Variable> past, final SearchAlgorithm searchAlgorithm) {
        if (use2001) {
            residues.clear();
        }
        Queue<Arc> queue = new ArrayDeque<>();

        for (Constraint constraint : csp.getConstraints()) {
            for (Variable x : constraint.getVariables()) {
                //enforces x not in past
                if (past.contains(x)) {
                    continue;
                }

                //find out i there is a y in scp(x), simultaneously in event and y != x
                for (Variable y : constraint.getVariables()) {
                    if (event.contains(y) && !y.equals(x)) {
                        Arc arc = new Arc(constraint, x);
                        queue.add(arc);
                        //Stop processing after first of such variables found
                        break;
                    }
                }

            }
        }

        //System.out.println("Starting queue is: ");
        //System.out.println(queue);

        while (!queue.isEmpty()) {
            //System.out.println("queue size: " + queue.size());
            Arc currentArc = queue.remove();
            //System.out.println("Chosen arc: ");
            //System.out.println(currentArc);
            if (revise(csp, currentArc, residues, past.size())) {
                if (currentArc.variable.getDomain().size() <= 0) {
                    //System.out.println("Variable: " + currentArc.variable + " is empty");
                    searchAlgorithm.onDomainWipeout(currentArc.variable, currentArc.constraint);
                    return false;
                } else {
                    for (final Constraint constraint : csp.getConstraints()) {
                        if (constraint.equals(currentArc.constraint) || !constraint.getVariables().contains(currentArc.variable)) {
                            continue;
                        }


                        for (final Variable variable : constraint.getVariables()) {
                            if (currentArc.variable.equals(variable) || past.contains(variable)) {
                                continue;
                            }

                            Arc arc = new Arc(constraint, variable);

                            queue.add(arc);

                        }


                    }
                }
            }
        }
        return true;
    }

    private boolean revise(final ConstraintSatisfactionProblem csp, final Arc arc, Map<Residue, VariableAssignment> residues, final int searchDepth) {
        int nbElements = arc.variable.getDomain().size();
        List<Integer> values = arc.variable.getDomain().getValues();
        //System.out.println("Constraint is: " + arc.constraint);
        //System.out.println("Revise values: " + values);
        for (int value : values) {
            //if (!seekSupport3(csp, arc, value)) {
            //if(!seekSupportNib(new Residue(arc, value))) {
            if (!seekSupport3rm(csp, arc, value, residues)) {
            //if (!seekSupport2001(csp, arc, value, residues)) {
                //System.out.println("Remove value " + arc.variable.getDomain().getValue(value) + " from domain of " + arc.variable);
                //arc.variable.getDomain().printInfo();
                arc.variable.getDomain().removeValue(value, searchDepth);
                //System.out.println("New domain is " + arc.variable.getDomain());
                //arc.variable.getDomain().printInfo();
            }
        }
        return nbElements != arc.variable.getDomain().size();
    }

    private boolean seekSupport3(ConstraintSatisfactionProblem csp, Arc arc, int value) {
        VariableAssignment tuple = getFirstValidTuple(csp, arc, value);
        //System.out.println("First valid tuple is: " + tuple);
        while (tuple != null) {
            if (arc.constraint.isSatisfied(tuple)) {
                //System.out.println(tuple + " satisfies constraint");
                return true;
            }
            tuple = getNextValidTuple(csp, arc, value, tuple);
            //System.out.println("Next valid tuple is " + tuple);
        }
        return false;
    }

    /**
     * Seeking supports with residues
     * @param csp
     * @param arc
     * @param value
     * @param residues
     * @return
     */
    private boolean seekSupport3rm(ConstraintSatisfactionProblem csp, Arc arc, int value, Map<Residue, VariableAssignment> residues) {
        //TODO: Fix this
        Residue residue = new Residue(arc, value);
        if (isValidTuple(arc.constraint, residues.get(residue))) {
            return true;
        }
        //System.out.println("Constraint: " + arc.constraint);
        //System.out.println("Variable: " + arc.variable);
        VariableAssignment tuple = getFirstValidTuple(csp, arc, value);
        while (tuple != null) {
            //System.out.println("Constraint: " + arc.constraint);
            if (arc.constraint.isSatisfied(tuple)) {
                for (Variable y : arc.constraint.getVariables()) {
                    Arc resArc = new Arc(arc.constraint, y);
                    Residue res = new Residue(resArc, tuple.getValueFor(y));
                    residues.put(res, tuple);
                }
                return true;
            }
            tuple = getNextValidTuple(csp, arc, value, tuple);
        }
        return false;
    }

    /**
     * Tests if each value in the tuple is in the current domain of its variable
     * @param constraint
     * @param tuple
     * @return
     */
    public boolean isValidTuple(Constraint constraint, VariableAssignment tuple) {
        if (tuple == null) {
            return false;
        }
        for (Variable x : constraint.getVariables()) {
            if (!x.getDomain().isValueInDom(tuple.getValueFor(x))) {
                return false;
            }
        }
        return true;
    }

    private VariableAssignment getNextValidTuple(ConstraintSatisfactionProblem csp, Arc arc, Object value, VariableAssignment variableAssignment) {
        VariableAssignment newVariableAssignment = variableAssignment.copy();
        for (int i = arc.constraint.getVariables().size() - 1; i >= 0; i --) {
            Variable y = arc.constraint.getVariables().get(i);
            if (!y.equals(arc.variable)) {
                if (y.getDomain().getNext(newVariableAssignment.getValueFor(y)) == -1) {
                    newVariableAssignment.assignValueTo(y, y.getDomain().getHead());
                } else {
                    newVariableAssignment.assignValueTo(y, y.getDomain().getNext(newVariableAssignment.getValueFor(y)));
                    return newVariableAssignment;
                }
            }
        }
        return null;
    }

    private VariableAssignment getFirstValidTuple(ConstraintSatisfactionProblem csp, Arc arc, int value) {
        VariableAssignment variableAssignment = new VariableAssignment();
        variableAssignment.assignValueTo(arc.variable, value);
        for (Variable other : arc.constraint.getVariables()) {
            if (other.equals(arc.variable)) {
                continue;
            }
            variableAssignment.assignValueTo(other, other.getDomain().getHead());
        }
        return variableAssignment;
    }

    private int getFirstInvalidPosition(ConstraintSatisfactionProblem csp, Arc arc, VariableAssignment tuple) {
        for (int i = 0; i < arc.constraint.getVariables().size(); i++) {
            Variable y = arc.constraint.getVariables().get(i);
            if (!y.getDomain().isValueInDom(tuple.getValueFor(y))) {
                return i;
            }
        }
        return -1;
    }

    private VariableAssignment getNextValidTuple(ConstraintSatisfactionProblem csp, Arc arc, Object value, VariableAssignment variableAssignment, int limit) {
        //System.out.println("Get next valid tuple, limit " + limit);
        VariableAssignment newVariableAssignment = variableAssignment.copy();
        for (int i = limit; i < arc.constraint.getVariables().size(); i++) {
            Variable y = arc.constraint.getVariables().get(i);
            if (!y.equals(arc.variable)) {
               // System.out.println("Assigned new value " + y.getDomain().getHead() + " to " + y.getName());
                newVariableAssignment.assignValueTo(y, y.getDomain().getHead());
            }
        }
        for (int i = limit - 1; i >= 0; i--) {
            //System.out.println("i is " + i);
            Variable y = arc.constraint.getVariables().get(i);
            if (!y.equals(arc.variable)) {
                if (newVariableAssignment.getValueFor(y) >= y.getDomain().getTail()) {
                    newVariableAssignment.assignValueTo(y, y.getDomain().getHead());
                } else {
                    newVariableAssignment.assignValueTo(y, y.getDomain().getNext(newVariableAssignment.getValueFor(y)));
                    while (y.getDomain().getAbsent(newVariableAssignment.getValueFor(y)) != -1) {
                        newVariableAssignment.assignValueTo(y, y.getDomain().getNext(newVariableAssignment.getValueFor(y)));
                    }
                    return newVariableAssignment;
                }
            }
        }
        return null;
    }

    private boolean seekSupport2001(ConstraintSatisfactionProblem csp, Arc arc, int value, Map<Residue, VariableAssignment> last) {
       // System.out.println("Seeking support for " + arc.variable.getName() + ", " + arc.constraint + ", " + value);
        VariableAssignment tuple;
        if (last.get(new Residue(arc, value)) == null) {
           // System.out.println("No previous support");
            tuple = getFirstValidTuple(csp, arc, value);
           // System.out.println("First valid tuple is " + tuple);
        } else {
          //  System.out.println("Found in last: ");
           // System.out.println(last.get(new Residue(arc, value)));
            int j = getFirstInvalidPosition(csp, arc, last.get(new Residue(arc, value)));
           // System.out.println("First invalid position is " + j);
            if (j == -1) {
                return true;
            } else {
                tuple = getNextValidTuple(csp, arc, value, last.get(new Residue(arc, value)), j);
               // System.out.println("Else branch tuple: " + tuple);
            }
        }
        while (tuple != null) {
            //System.out.println("Tuple != null, testing constraint");
            if (arc.constraint.isSatisfied(tuple)) {
                last.put(new Residue(arc, value), tuple);
             //   System.out.println("Found support for " + arc.variable.getName() + ", " + arc.constraint + ", " + value);
               // System.out.println("Last is now " + tuple);
                return true;
            }
            tuple = getNextValidTuple(csp, arc, value, tuple);
          //  System.out.println("Next tuple: " + tuple);
        }
      //  System.out.println("No support found");
        return false;
    }

}
