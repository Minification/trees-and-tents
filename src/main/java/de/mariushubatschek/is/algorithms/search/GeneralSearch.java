package de.mariushubatschek.is.algorithms.search;

import de.mariushubatschek.is.algorithms.heuristics.variables.scoring.DomWdegVariableScoring;
import de.mariushubatschek.is.algorithms.util.AlmostStack;
import de.mariushubatschek.is.algorithms.util.Arc;
import de.mariushubatschek.is.algorithms.util.VariableValue;
import de.mariushubatschek.is.algorithms.heuristics.variables.scoring.VariableScoring;
import de.mariushubatschek.is.algorithms.heuristics.values.ValueHeuristic;
import de.mariushubatschek.is.algorithms.heuristics.variables.VariableHeuristic;
import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Variable;
import de.mariushubatschek.is.modeling.VariableAssignment;

import java.util.*;
import java.util.stream.Collectors;

public class GeneralSearch implements SearchAlgorithm {

    private Map<VariableValue, Set<VariableValue>> expl;

    //private Stack<VariableValue> instantiation;

    private AlmostStack instantiation;

    private VariableHeuristic variableHeuristic;

    private ValueHeuristic valueHeuristic;

    private LookBack lookBack = LookBack.SBT;

    private LookAhead lookAhead = LookAhead.MAC;

    private Map<Constraint, Integer> constraintWeightMap;

    private ConstraintSatisfactionProblem csp;

    private VariableScoring variableScoring = new DomWdegVariableScoring();

    private List<Variable> uninstantiatedVariables;

    private Map<Constraint, List<Variable>> constraintUninstantiatedScopeListMap = new HashMap<>();

    public void setLookAhead(LookAhead lookAhead) {
        this.lookAhead = lookAhead;
    }

    public void setLookBack(LookBack lookBack) {
        this.lookBack = lookBack;
    }

    public GeneralSearch(VariableScoring variableScoring, ValueHeuristic valueHeuristic) {
        this.variableScoring = variableScoring;
        this.valueHeuristic = valueHeuristic;
    }

    public void initialize(ConstraintSatisfactionProblem csp) {
        this.csp = csp;
        expl = new HashMap<>();
        constraintWeightMap = new HashMap<>();
        uninstantiatedVariables = new ArrayList<>(csp.getVariables());
        instantiation = new AlmostStack();
        variableScoring.reset(this);
        variableScoring.onInitialize(this);
        for (Constraint c : csp.getConstraints()) {
            constraintUninstantiatedScopeListMap.put(c, new ArrayList<>(c.getVariables()));
            variableScoring.onInitializeConstraint(c, this);
        }
        // Technically not necessary (anything not in map returns null)
        for (final Variable variable : csp.getVariables()) {
            List<Integer> domain = getValuesInDomainOf(variable);
            for (int value : domain) {
                VariableValue vv = new VariableValue(variable, value);
                expl.put(vv, null);
            }
        }
    }

    @Override
    public SearchData run(final ConstraintSatisfactionProblem csp) {
        if (csp.getVariables().size() == 0) {
            System.out.println("Nothing to solve.");
            SearchData searchData = new SearchData();
            searchData.almostStack = new AlmostStack();
            searchData.nodeCount = 0;
            searchData.backtrackCount = 0;
            return searchData;
        }
        initialize(csp);

        boolean finished = lookAhead == LookAhead.MAC && enforceGACVar(csp, csp.getVariables(), new ArrayList<>(), instantiation) != null;
        long nodeCount = 0;
        long backtrackCount = 0;
        //System.out.println("Finished before starting: " + finished);

        /*System.out.print("[");
        for (Variable v : csp.getVariables()) {
            List<Object> values = new ArrayList<>();
            for (Integer l : getValuesInDomainOf(v)) {
                values.add(v.getDomain().getValue(l));
            }
            System.out.print("{" + v.getName() + ", " + values + "}, ");
        }
        System.out.println("]");*/


        while (!finished) {

            //Variable chosenVariable = variableHeuristic.apply(csp, this);

            if (Thread.currentThread().isInterrupted()) {
                SearchData searchData = new SearchData();
                searchData.almostStack = instantiation;
                searchData.nodeCount = nodeCount;
                searchData.backtrackCount = backtrackCount;
                searchData.timedOut = true;
                return searchData;
            }

            Variable chosenVariable = null;
            double bestScore = Double.MAX_VALUE;
            for (final Variable v : uninstantiatedVariables) {
                double score = variableScoring.score(v, this);
                if (score < bestScore) {
                    chosenVariable = v;
                    bestScore = score;
                }
            }

            int chosenValue = valueHeuristic.apply(csp, this, chosenVariable);
            VariableValue vv = new VariableValue(chosenVariable, chosenValue);

            //System.out.println("Chose: " + vv.variable.getName() + ", " + getValuesInDomainOf(vv.variable).stream().map(vv.variable.getDomain()::getValue).collect(Collectors.toList()));
            instantiation.push(vv);
            nodeCount++;
            //Record that x <-- a, i.e. x != b for all b != a
            List<Integer> vals = getValuesInDomainOf(vv.variable);
            for (int value : vals) {
                if (value == vv.value) {
                    continue;
                }
                Set<VariableValue> e = new HashSet<>();
                e.add(vv);
                expl.put(new VariableValue(vv.variable, value), e);
            }

            uninstantiatedVariables.remove(chosenVariable);
            variableScoring.onSelection(chosenVariable, this);
            for (Constraint c : csp.getConstraintsFor(chosenVariable)) {
                constraintUninstantiatedScopeListMap.get(c).remove(chosenVariable);
                variableScoring.onSelectionConstraint(chosenVariable, c, this);
            }

            Set<VariableValue> noGood = checkConsistencyAfterAssignment(csp, vv.variable, instantiation);
            //System.out.println("Consistent: " + (noGood == null) + ", |I|=" + instantiation.size() + ", vars(P)=" + csp.getVariables().size());
            //System.out.println("No good after assignment: " + noGood);
            // A solution has been found
            if (noGood == null && instantiation.size() == csp.getVariables().size()) {
                //System.out.println(instantiation.toList());
                noGood = new HashSet<>(instantiation.toList());

                for (VariableValue m : instantiation.toList()) {
                    Variable mm = m.variable;
                    mm.replaceDomain(new Object[]{ mm.getDomain().getValue(m.value) });
                }

                SearchData searchData = new SearchData();
                searchData.almostStack = instantiation;
                searchData.nodeCount = nodeCount;
                searchData.backtrackCount = backtrackCount;
                return searchData;
            }
            // Not a solution yet
            while (noGood != null && !noGood.isEmpty()) {

                //vv <-- vv from top in I and vv in noGood
                vv = instantiation.findFirstFromTopIn(noGood);

                if (lookBack == LookBack.IBT) {
                    while (!instantiation.top().equals(vv)) {
                        undoAssignment(instantiation.top(), instantiation, csp);
                    }
                }
                undoAssignment(vv, instantiation, csp);
                //System.out.println("Popped " + vv.variable.getName() + ", " + vv.value);
                Set<VariableValue> newNoGood = new HashSet<>(noGood);
                newNoGood.remove(vv);
                expl.put(vv, newNoGood);
                backtrackCount++;

                /*System.out.print("[");
                for (Variable v : csp.getVariables()) {
                    List<Object> values = new ArrayList<>();
                    for (Integer l : getValuesInDomainOf(v)) {
                        values.add(v.getDomain().getValue(l));
                    }
                    System.out.print("{" + v.getName() + ", " + values + "}, ");
                }
                System.out.println("]");*/

                //System.out.println("Expl for " + vv + " is " + newNoGood);
                noGood = checkConsistencyAfterRefutation(csp, vv.variable, instantiation);
                //System.out.println("Identified nogood after refutation: " + noGood);
                //System.out.println("Refuted: " + vv);
            }
            //TODO: Careful, originally it is noGood.isEmpty(), but this is prone to NPEs
            if (noGood != null) {
                finished = true;
            }
        }
        System.out.println("No solution found");
        SearchData searchData = new SearchData();
        searchData.almostStack = new AlmostStack();
        searchData.nodeCount = nodeCount;
        return searchData;
    }

    private void undoAssignment(final VariableValue vv, final AlmostStack instantiation, final ConstraintSatisfactionProblem csp) {
        instantiation.delete(vv);
        //System.out.println("Undoing assignment");

        //System.out.println("Restore domains");
        for (Variable y : csp.getVariables()) {
            for (int b : y.getInitialDomain().getValues()) {
                VariableValue yb = new VariableValue(y, b);
                //TODO: Careful, I added expl.get(yb) != null to avoid an NPE
                if (expl.get(yb) != null && expl.get(yb).contains(vv)) {
                    if (instantiation.variables().contains(y)) {
                        Set<VariableValue> newExplanation = new HashSet<>(instantiation.withVariable(y));
                        //System.out.println("New explanation: " + newExplanation);
                        expl.put(yb, newExplanation);
                    } else {
                        expl.put(yb, null);
                    }
                }
            }
        }


        uninstantiatedVariables.add(vv.variable);
        variableScoring.onBacktrack(vv.variable, this);
        for (Constraint c : csp.getConstraintsFor(vv.variable)) {
            constraintUninstantiatedScopeListMap.get(c).add(vv.variable);
            variableScoring.onBacktrackConstraint(vv.variable, c, this);
        }
    }

    private Set<VariableValue> checkConsistencyAfterAssignment(final ConstraintSatisfactionProblem csp, final Variable x, final AlmostStack instantiation) {
        switch (lookAhead) {
            case BC:
                for (Constraint c : csp.getConstraintsFor(x)) {
                    if (instantiation.variables().containsAll(c.getVariables())) {
                        VariableAssignment variableAssignment = new VariableAssignment();
                        instantiation.toList().forEach(v -> {
                            variableAssignment.assignValueTo(v.variable, v.value);
                        });
                        if (!c.isSatisfied(variableAssignment)) {
                            //constraintWeightMap.put(c, constraintWeightMap.getOrDefault(c, 1) + 1);
                            onDomainWipeout(null, c);
                            if (lookBack == LookBack.SBT) {
                                return new HashSet<>(instantiation.toList());
                            } else {
                                return instantiation.toList().stream().filter(v -> c.getVariables().contains(v.variable)).collect(Collectors.toSet());
                            }
                        }
                    }
                }
                return null;
            case FC:
                return applyFC(csp, x, instantiation);
            case MAC:
                return enforceGACVar(csp, Collections.singletonList(x), instantiation.variables(), instantiation);
        }
        throw new RuntimeException("Something went seriously wrong here.");
    }

    private Set<VariableValue> checkConsistencyAfterRefutation(final ConstraintSatisfactionProblem csp, final Variable x, AlmostStack instantiation) {
        if (isDomainEmpty(x)) {
            //System.out.println("Domain wipeout for " + x.getName());
            return handleEmptyDomain(x, instantiation);
        }
        switch (lookAhead) {
            case BC:
                return null;
            case FC:
                if (lookBack == LookBack.DBT) {
                    return applyFC(csp, instantiation.variables(), instantiation);
                } else {
                    return null;
                }
            case MAC:
                if (lookBack == LookBack.SBT) {
                    return enforceGACVar(csp, Collections.singletonList(x), instantiation.variables(), instantiation);
                } else {
                    return enforceGACVar(csp, csp.getVariables(), instantiation.variables(), instantiation);
                }
        }
        throw new RuntimeException("Something went seriously wrong here.");
    }

    private Set<VariableValue> applyFC(ConstraintSatisfactionProblem csp, Variable event, AlmostStack instantiation) {
        return applyFC(csp, Collections.singletonList(event), instantiation);
    }

    private Set<VariableValue> applyFC(ConstraintSatisfactionProblem csp, List<Variable> event, AlmostStack instantiation) {
        for (Constraint c : csp.getConstraints()) {
            var v1 = new ArrayList<>(event);
            v1.retainAll(c.getVariables());
            if (v1.isEmpty()) {
                continue;
            }
            for (Variable y : c.getVariables()) {
                if (!instantiation.variables().contains(y)) {
                    if (revise(csp, new Arc(c, y), instantiation.size(), instantiation)) {
                        if (isDomainEmpty(y)) {
                            //constraintWeightMap.put(c, constraintWeightMap.getOrDefault(c, 1)+1);
                            onDomainWipeout(y, c);
                            return handleEmptyDomain(y, instantiation);
                        }
                    }
                }
            }
        }
        return null;
    }

    public Set<VariableValue> enforceGAC(ConstraintSatisfactionProblem csp, List<Variable> event) {
        return enforceGAC(csp, event, instantiation.variables(), instantiation);
    }

    public Set<VariableValue> enforceGAC(ConstraintSatisfactionProblem csp, List<Variable> event, List<Variable> past, AlmostStack instantiation) {
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
            //System.out.println("queue: " + queue);
            Arc currentArc = queue.remove();
            //System.out.println("Chosen arc: ");
            //System.out.println(currentArc);
            if (revise(csp, currentArc, past.size(), instantiation)) {
                if (isDomainEmpty(currentArc.variable)) {
                    //System.out.println("Handle empty domain for variable: " + currentArc.variable.getName());
                    //constraintWeightMap.put(currentArc.constraint, constraintWeightMap.getOrDefault(currentArc.constraint, 1)+1);
                    onDomainWipeout(currentArc.variable, currentArc.constraint);
                    return handleEmptyDomain(currentArc.variable, instantiation);
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
        return null;
    }

    public Set<VariableValue> enforceGACVar(ConstraintSatisfactionProblem csp, List<Variable> event) {
        return enforceGACVar(csp, event, instantiation.variables(), instantiation);
    }

    public Set<VariableValue> enforceGACVar(ConstraintSatisfactionProblem csp, List<Variable> event, List<Variable> past, AlmostStack instantiation) {
        Map<Variable, Integer> varStamps = new HashMap<>();
        Map<Constraint, Integer> constraintStamps = new HashMap<>();
        Queue<Variable> q = new ArrayDeque<>();
        int time = 0;
        for (Variable v : event) {
            q.add(v);
            time++;
            varStamps.put(v, time);
        }
        while (!q.isEmpty()) {
            Variable x = q.remove();
            for (Constraint c : csp.getConstraintsFor(x)) {
                if (varStamps.getOrDefault(x, 0) > constraintStamps.getOrDefault(c, 0)) {
                    for (Variable y : c.getVariables()) {
                        if (!past.contains(y)) {
                            if (!y.equals(x) || c.getVariables().stream().filter(z -> !z.equals(x) && varStamps.getOrDefault(z, 0) > constraintStamps.getOrDefault(c, 0)).count() > 0) {
                                if (revise(csp, new Arc(c, y), instantiation.size(), instantiation)) {
                                    if (isDomainEmpty(y)) {
                                        //constraintWeightMap.put(c, constraintWeightMap.getOrDefault(c, 1)+1);
                                        onDomainWipeout(y, c);
                                        return handleEmptyDomain(y, instantiation);
                                    }
                                    q.add(y);
                                    time++;
                                    varStamps.put(y, time);
                                }
                            }
                        }
                    }
                    time++;
                    constraintStamps.put(c, time);
                }
            }
        }
        return null;
    }

    private Set<VariableValue> handleEmptyDomain(final Variable x, final AlmostStack instantiation) {
        if (lookBack == LookBack.SBT) {
            return new HashSet<>(instantiation.toList());
        }
        Set<VariableValue> noGood = new HashSet<>();
        for (int a : x.getInitialDomain().getValues()) {
            noGood.addAll(expl.get(new VariableValue(x, a)));
        }
        return noGood;
    }

    private Set<VariableValue> getExplanation(final Arc arc, final int value, final AlmostStack instantiation) {
        if (lookBack == LookBack.SBT) {
            return new HashSet<>(instantiation.toList());
        }
        Set<VariableValue> explanation = new HashSet<>();
        for (Variable y : arc.constraint.getVariables()) {
            if (y.equals(arc.variable)) {
                continue;
            }

            for (int b : y.getInitialDomain().getValues()) {
                Set<VariableValue> ybExpl = expl.get(new VariableValue(y, b));
                if (ybExpl != null && !ybExpl.isEmpty()) {
                    //exists tau in rel(c) with tau[x]==a and tau[y] == b
                    if (seekSupportWithVValues(arc.constraint, arc.variable, value, y, b)) {
                        explanation.addAll(ybExpl);
                    }
                }
            }

        }
        return explanation;
    }

    private Set<VariableValue> getExplanation2(final Arc arc, final int value, final AlmostStack instantiation) {
        if (lookBack == LookBack.SBT) {
            return new HashSet<>(instantiation.toList());
        }
        Set<VariableValue> explanation = new HashSet<>();
        for (Variable y : arc.constraint.getVariables()) {
            if (y.equals(arc.variable)) {
                continue;
            }

            if (instantiation.variables().contains(y)) {
                explanation.addAll(instantiation.withVariable(y));
            } else {
                for (int b : y.getInitialDomain().getValues()) {
                    Set<VariableValue> ybExpl = expl.get(new VariableValue(y, b));
                    if (ybExpl != null) {
                        explanation.addAll(ybExpl);
                    }
                }
            }
        }
        return explanation;
    }

    private boolean seekSupportWithVValues(final Constraint c, final Variable one, final int a, final Variable two, final int b) {
        VariableAssignment variableAssignment = getFirstValidTuple2(c, one, a, two, b);
        while (variableAssignment != null) {
            if (c.isSatisfied(variableAssignment)) {
                return true;
            }
            variableAssignment = getNextValidTuple2(c, one, two, variableAssignment);
        }
        return false;
    }

    private VariableAssignment getNextValidTuple2(final Constraint c, final Variable one, final Variable two, VariableAssignment variableAssignment) {
        VariableAssignment newVariableAssignment = variableAssignment.copy();
        for (int i = c.getVariables().size() - 1; i >= 0; i--) {
            Variable y = c.getVariables().get(i);
            if (!y.equals(one) && !y.equals(two)) {
                if (domainNext(y, newVariableAssignment.getValueFor(y)) == -1) {
                    newVariableAssignment.assignValueTo(y, domainHead(y));
                } else {
                    newVariableAssignment.assignValueTo(y, domainNext(y, newVariableAssignment.getValueFor(y)));
                    return newVariableAssignment;
                }
            }
        }
        return null;
    }

    private VariableAssignment getNextValidTuple(ConstraintSatisfactionProblem csp, Arc arc, Object value, VariableAssignment variableAssignment) {
        VariableAssignment newVariableAssignment = variableAssignment.copy();
        for (int i = arc.constraint.getVariables().size() - 1; i >= 0; i --) {
            Variable y = arc.constraint.getVariables().get(i);
            if (!y.equals(arc.variable)) {
                if (domainNext(y, newVariableAssignment.getValueFor(y)) == -1) {
                    newVariableAssignment.assignValueTo(y, domainHead(y));
                } else {
                    newVariableAssignment.assignValueTo(y, domainNext(y, newVariableAssignment.getValueFor(y)));
                    return newVariableAssignment;
                }
            }
        }
        return null;
    }

    private VariableAssignment getFirstValidTuple2(Constraint c, Variable one, int value, Variable two, int b) {
        VariableAssignment variableAssignment = new VariableAssignment();
        variableAssignment.assignValueTo(one, value);
        variableAssignment.assignValueTo(two, b);
        for (Variable other : c.getVariables()) {
            if (other.equals(one) || other.equals(two)) {
                continue;
            }
            variableAssignment.assignValueTo(other, domainHead(other));
        }
        return variableAssignment;
    }

    private VariableAssignment getFirstValidTuple(ConstraintSatisfactionProblem csp, Arc arc, int value) {
        VariableAssignment variableAssignment = new VariableAssignment();
        variableAssignment.assignValueTo(arc.variable, value);
        for (Variable other : arc.constraint.getVariables()) {
            if (other.equals(arc.variable)) {
                continue;
            }
            //System.out.println("Domain head in get first valid tuple for variable " + other.getName() + " is: " + domainHead(other));
            //System.out.println(getValuesInDomainOf(other));
            variableAssignment.assignValueTo(other, domainHead(other));
        }
        return variableAssignment;
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
        //System.out.println("No valid tuple found");
        return false;
    }

    private boolean revise(final ConstraintSatisfactionProblem csp, final Arc arc, final int searchDepth, final AlmostStack instantiation) {
        int nbElements = domainSizeOf(arc.variable);
        List<Integer> values = getValuesInDomainOf(arc.variable);
        //System.out.println("Revise values: " + values + " for variable: " + arc.variable.getName());
        for (int value : values) {
            if (!seekSupport3(csp, arc, value)) {
            //if (!seekSupport3rm(csp, arc, value, residues)) {
                //if (!seekSupport2001(csp, arc, value, residues)) {
                //System.out.println("Remove value " + value + " from domain of " + arc.variable.getName());
                //arc.variable.getDomain().printInfo();
                //arc.variable.getDomain().removeValue(value, searchDepth);
                //System.out.println("Values of " + arc.variable.getName() + " are " + getValuesInDomainOf(arc.variable));
                expl.put(new VariableValue(arc.variable, value), getExplanation2(arc, value, instantiation));
                //System.out.println("Values of " + arc.variable.getName() + " are after: " + getValuesInDomainOf(arc.variable));
                //System.out.println("Now expl " + arc.variable.getName() + ", " + value + " is " + expl.get(new VariableValue(arc.variable, value)));
                //System.out.println("Values in " + arc.variable.getName() + ": " + getValuesInDomainOf(arc.variable));
                //System.out.println("Domain head: " + domainHead(arc.variable));
                for (int current : getValuesInDomainOf(arc.variable)) {
                    //System.out.println("Next of " + current + " is " + domainNext(arc.variable, current));
                }
                //System.out.println("New domain is " + getValuesInDomainOf(arc.variable));
                //arc.variable.getDomain().printInfo();
            }
        }
        return nbElements != domainSizeOf(arc.variable);
    }

    private List<Integer> getValuesInDomainOf(final Variable variable) {
        final List<Integer> values = new ArrayList<>();
        List<Integer> initialValues = variable.getInitialDomain().getValues();
        for (int value : initialValues) {
            //System.out.println("Expl " + variable.getName() + ", " + value + " = " + expl.get(new VariableValue(variable, value)));
            if (expl.get(new VariableValue(variable, value)) == null) {
                values.add(value);
            }
        }
        //System.out.println("Values in domain of " + variable.getName() + ": " + values);
        return values;
    }

    private int domainSizeOf(final Variable variable) {
        return getValuesInDomainOf(variable).size();
    }

    private boolean isDomainEmpty(final Variable variable) {
        return domainSizeOf(variable) == 0;
    }

    private int domainHead(final Variable variable) {
        return domainSizeOf(variable) > 0 ? getValuesInDomainOf(variable).get(0) : -1;
    }

    private int domainTail(final Variable variable) {
        List<Integer> values = getValuesInDomainOf(variable);
        return domainSizeOf(variable) > 0 ? values.get(values.size()-1) : -1;
    }

    private int domainNext(final Variable variable, final int current) {
        List<Integer> values = getValuesInDomainOf(variable);
        int index = values.indexOf(current);
        if (index >= 0) {
            if (index == values.size()-1) {
                return -1;
            } else {
                return values.get(index + 1);
            }
        }
        return -1;
    }

    @Override
    public List<Variable> getInstantiationVariables() {
        return instantiation.variables();
    }

    @Override
    public int getDomainSize(Variable variable) {
        return domainSizeOf(variable);
    }

    @Override
    public List<Integer> getDomainValues(Variable variable) {
        return getValuesInDomainOf(variable);
    }

    @Override
    public List<Variable> getUninstantiatedVariables() {
        /*List<Variable> variablesWithoutI = new ArrayList<>(csp.getVariables());
        variablesWithoutI.removeAll(instantiation.variables());
        return variablesWithoutI;*/
        return uninstantiatedVariables;
    }

    @Override
    public int getConstraintWeight(Constraint constraint) {
        return constraintWeightMap.getOrDefault(constraint, 1);
    }

    @Override
    public void onDomainWipeout(Variable variable, Constraint constraint) {
        variableScoring.onDomainWipeout(variable, constraint, this);
    }

    @Override
    public List<Variable> getConstraintUninstantiatedVariables(Constraint constraint) {
        return constraintUninstantiatedScopeListMap.get(constraint);
    }
}
