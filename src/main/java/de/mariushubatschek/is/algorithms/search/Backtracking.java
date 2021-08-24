package de.mariushubatschek.is.algorithms.search;

import de.mariushubatschek.is.algorithms.util.AlmostStack;
import de.mariushubatschek.is.algorithms.propagation.ArcConsistencyAlgorithm;
import de.mariushubatschek.is.algorithms.util.VariableValue;
import de.mariushubatschek.is.algorithms.heuristics.variables.scoring.DomWdegVariableScoring;
import de.mariushubatschek.is.algorithms.heuristics.variables.scoring.VariableScoring;
import de.mariushubatschek.is.algorithms.heuristics.values.ValueHeuristic;
import de.mariushubatschek.is.algorithms.heuristics.variables.VariableHeuristic;
import de.mariushubatschek.is.modeling.Constraint;
import de.mariushubatschek.is.modeling.ConstraintSatisfactionProblem;
import de.mariushubatschek.is.modeling.Variable;

import java.util.*;

public class Backtracking implements SearchAlgorithm {

    private VariableHeuristic variableHeuristic;

    private ValueHeuristic valueHeuristic;

    private AlmostStack instantiation;

    private ConstraintSatisfactionProblem csp;

    private Map<Constraint, Integer> constraintWeightMap;

    private List<Variable> uninstantiatedVariables;

    private Map<Constraint, List<Variable>> constraintUninstantiatedScopeListMap = new HashMap<>();

    private VariableScoring variableScoring;

    public Backtracking(VariableScoring variableScoring, ValueHeuristic valueHeuristic) {
        this.variableScoring = variableScoring;
        this.valueHeuristic = valueHeuristic;
    }

    @Override
    public SearchData run(final ConstraintSatisfactionProblem csp) {
        this.csp = csp;
        constraintWeightMap = new HashMap<>();
        uninstantiatedVariables = new ArrayList<>(csp.getVariables());
        instantiation = new AlmostStack();
        variableScoring.reset(this);
        variableScoring.onInitialize(this);
        for (Constraint c : csp.getConstraints()) {
            constraintUninstantiatedScopeListMap.put(c, new ArrayList<>(c.getVariables()));
            variableScoring.onInitializeConstraint(c, this);
            /*for (Variable v : c.getVariables()) {
                v.changeWeightBy(constraintWeightMap.getOrDefault(c, 1));
            }*/
        }
        //System.out.println("Vars: " + csp.getVariables().stream().map(Variable::getName).collect(Collectors.joining(", ")));
        //priorityQueue.addAll(csp.getVariables());
        //same(uninstantiatedVariables, priorityQueue);
        //System.out.println("Uninstantiated variables: " + uninstantiatedVariables.size());
        //System.out.println("Initial size: " + priorityQueue.size());
        ArcConsistencyAlgorithm arcConsistencyAlgorithm = new ArcConsistencyAlgorithm();
        //System.out.println("Initial consistency check");
        long nodeCount = 0;
        long backtrackCount = 0;
        boolean consistent = arcConsistencyAlgorithm.run(csp, this);
        //System.out.println("After consistency: " + csp.getVariables());
        if (!consistent) {
            System.out.println("Input is inconsistent");
            SearchData searchData = new SearchData();
            searchData.almostStack = instantiation;
            searchData.nodeCount = nodeCount;
            searchData.backtrackCount = backtrackCount;
            return searchData;
        }
        //System.out.println("Consistency check done.");
        boolean finished = false;
        while (!finished) {

            if (Thread.currentThread().isInterrupted()) {
                SearchData searchData = new SearchData();
                searchData.almostStack = instantiation;
                searchData.nodeCount = nodeCount;
                searchData.backtrackCount = backtrackCount;
                searchData.timedOut = true;
                return searchData;
            }

            //Variable chosenVariable = variableHeuristic.apply(csp, this);
            Variable chosenVariable = null;
            double bestScore = Double.MAX_VALUE;
            for (final Variable v : uninstantiatedVariables) {
                double score = variableScoring.score(v, this);
                if (score < bestScore) {
                    chosenVariable = v;
                    bestScore = score;
                }
            }
            //Variable chosenVariable = priorityQueue.peek();
            int chosenValue = valueHeuristic.apply(csp, this, chosenVariable);
            VariableValue variableValue = new VariableValue(chosenVariable, chosenValue);
            //System.out.println("Chose: " + variableValue);
            instantiation.push(variableValue);
            uninstantiatedVariables.remove(chosenVariable);
            //priorityQueue.remove(chosenVariable);
            //same(uninstantiatedVariables, priorityQueue);
            //System.out.println("After choice: " + priorityQueue.size());
            variableScoring.onSelection(chosenVariable, this);
            for (Constraint c : csp.getConstraintsFor(chosenVariable)) {
                constraintUninstantiatedScopeListMap.get(c).remove(chosenVariable);
                variableScoring.onSelectionConstraint(chosenVariable, c, this);
                /*List<Variable> uninstantiatedScope = constraintUninstantiatedScopeListMap.get(c);
                if (uninstantiatedScope.size() == 1) {
                    for (Variable v : uninstantiatedScope) {
                        v.changeWeightBy(-constraintWeightMap.getOrDefault(c, 1));
                        //if (priorityQueue.remove(v)) {
                        //    priorityQueue.add(v);
                            //System.out.println("Removed now added after choice: " + priorityQueue.size());
                        //}
                    }
                }*/
            }
            nodeCount++;
            //System.out.println("Instantiation is now: " + instantiation);
            variableValue.variable.getDomain().reduceTo(variableValue.value, instantiation.size());
            //System.out.println("Reduced variable: " + variableValue.variable);
            //System.out.println(instantiation);
            consistent = arcConsistencyAlgorithm.run(csp, Collections.singletonList(variableValue.variable), instantiation.variables(), this);
            //System.out.println("Consistent: " + consistent + ", |I|=" + instantiation.size() + ", vars(P)=" + csp.getVariables().size());
            if (consistent && instantiation.size() == csp.getVariables().size()) {
                //System.out.println(instantiation);
                consistent = false;
                //Remove next line to list all solutions
                SearchData searchData = new SearchData();
                searchData.almostStack = instantiation;
                searchData.nodeCount = nodeCount;
                searchData.backtrackCount = backtrackCount;
                return searchData;
            }

            while (!consistent && !instantiation.isEmpty()) {
                VariableValue vv = instantiation.pop();
                uninstantiatedVariables.add(vv.variable);
                //priorityQueue.add(vv.variable);
                //same(uninstantiatedVariables, priorityQueue);
                //System.out.println("After backtrack add: " + priorityQueue.size());
                variableScoring.onBacktrack(vv.variable, this);
                for (Constraint c : csp.getConstraintsFor(vv.variable)) {
                    constraintUninstantiatedScopeListMap.get(c).add(vv.variable);
                    variableScoring.onBacktrackConstraint(vv.variable, c, this);
                    /*List<Variable> uninstantiatedScope = constraintUninstantiatedScopeListMap.get(c);
                    if (uninstantiatedScope.size() != 1) {
                        for (Variable v : uninstantiatedScope) {
                            v.changeWeightBy(constraintWeightMap.getOrDefault(c, 1));
                            //if (priorityQueue.remove(v)) {
                            //    priorityQueue.add(v);
                                //System.out.println("After backtrack add update: " + priorityQueue.size());
                            //}
                        }
                    }*/
                }
                //System.out.println("Popped " + vv.variable.getName() + ", " + vv.value);
                List<Variable> variablesWithoutI = getUninstantiatedVariables();
                //System.out.println("Before restoration: " + vv.variable);
                //System.out.println(csp.getVariables());
                for (Variable y : variablesWithoutI) {
                    y.getDomain().restoreUpTo(instantiation.size() + 1);
                }
                vv.variable.getDomain().removeValue(vv.value, instantiation.size());
                backtrackCount++;
                //System.out.println("After: " + vv.variable);
                //System.out.println(csp.getVariables());
                //System.out.println("Before consistency");
                consistent = vv.variable.getDomain().size() > 0 && arcConsistencyAlgorithm.run(csp, Collections.singletonList(vv.variable), instantiation.variables(), this);
                //System.out.println("After consistency");
            }

            if (!consistent) {
                finished = true;
            }
        }
        System.out.println("No solution found");
        SearchData searchData = new SearchData();
        searchData.almostStack = instantiation;
        searchData.nodeCount = nodeCount;
        return searchData;
    }

    @Override
    public List<Variable> getInstantiationVariables() {
        return instantiation.variables();
    }

    @Override
    public int getDomainSize(Variable variable) {
        return variable.getDomain().size();
    }

    @Override
    public List<Integer> getDomainValues(Variable variable) {
        return variable.getDomain().getValues();
    }

    @Override
    public List<Variable> getUninstantiatedVariables() {
        /*List<Variable> variablesWithoutI = new ArrayList<>(csp.getVariables());
        variablesWithoutI.removeAll(getInstantiationVariables());
        return variablesWithoutI;*/
        return uninstantiatedVariables;
    }

    @Override
    public int getConstraintWeight(Constraint constraint) {
        return constraintWeightMap.getOrDefault(constraint, 1);
    }

    @Override
    public void onDomainWipeout(Variable variable, Constraint constraint) {
        /*List<Variable> uninstantiatedScope = constraintUninstantiatedScopeListMap.get(constraint);
        for (Variable v : uninstantiatedScope) {
            v.changeWeightBy(1);
            //if (priorityQueue.remove(v)) {
            //    priorityQueue.add(v);
                //System.out.println("After domain wipeout update: " + priorityQueue.size());
            //}
        }
        constraintWeightMap.put(constraint, constraintWeightMap.getOrDefault(constraint, 1)+1);*/
        variableScoring.onDomainWipeout(variable, constraint, this);
    }

    @Override
    public List<Variable> getConstraintUninstantiatedVariables(Constraint constraint) {
        return constraintUninstantiatedScopeListMap.get(constraint);
    }

}
