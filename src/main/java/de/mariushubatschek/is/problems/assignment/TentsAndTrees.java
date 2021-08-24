package de.mariushubatschek.is.problems.assignment;

import de.mariushubatschek.is.data.PuzzleInfo;
import de.mariushubatschek.is.modeling.*;
import de.mariushubatschek.is.output.CellsOutput;
import de.mariushubatschek.is.problems.Cells;
import de.mariushubatschek.is.problems.CombinedConstraint;
import de.mariushubatschek.is.problems.assignment.constraints.TentColumnCountConstraint2;
import de.mariushubatschek.is.problems.assignment.constraints.TentRowCountConstraint2;
import de.mariushubatschek.is.problems.assignment.constraints.diagonals.*;
import de.mariushubatschek.is.problems.assignment.constraints.hiddens.AddCountConstraint;
import de.mariushubatschek.is.problems.assignment.constraints.hiddens.ValidAssignmentsCountSumConstraint;
import de.mariushubatschek.is.problems.assignment.constraints.hv.HVBottom;
import de.mariushubatschek.is.problems.assignment.constraints.hv.HVLeft;
import de.mariushubatschek.is.problems.assignment.constraints.hv.HVRight;
import de.mariushubatschek.is.problems.assignment.constraints.hv.HVTop;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class TentsAndTrees implements ConstraintSatisfactionProblem {

    private List<Variable> variables;

    private List<Constraint> constraints;

    private int[] rowHints;

    private int[] colHints;

    private Variable[][] variables1;

    public TentsAndTrees(final PuzzleInfo puzzleInfo) {
        this.variables = new ArrayList<>();
        this.constraints = new ArrayList<>();
        variables1= new Variable[puzzleInfo.rows.size()][];
        rowHints = new int[puzzleInfo.rows.size()];
        colHints = puzzleInfo.maxColumnTentCounts;
        //Create one variable for each tree, with valid domain values
        for(int i=0;i<puzzleInfo.rows.size();i++){
            rowHints[i] = puzzleInfo.rows.get(i).tentCount;
            variables1[i]=new Variable[puzzleInfo.rows.get(i).columns.size()];
            for(int j=0; j<puzzleInfo.rows.get(i).columns.size();j++){
                if(puzzleInfo.rows.get(i).columns.get(j).equals("t")){
                    List<Integer> values = new ArrayList<>();
                    //Add values only if there is something assignable in the direction
                    if (j-1 >= 0 && !puzzleInfo.rows.get(i).columns.get(j-1).equals("t")) {
                        values.add(-1);
                    }
                    if (j+1 < puzzleInfo.rows.get(i).columns.size() && !puzzleInfo.rows.get(i).columns.get(j+1).equals("t")) {
                        values.add(1);
                    }
                    if (i-1 >= 0 && !puzzleInfo.rows.get(i-1).columns.get(j).equals("t")) {
                        values.add(-2);
                    }
                    if (i+1 < puzzleInfo.rows.size() && !puzzleInfo.rows.get(i+1).columns.get(j).equals("t")) {
                        values.add(2);
                    }
                    Integer[] integers = values.toArray(new Integer[0]);
                    variables1[i][j] = new Variable(i+","+j,new Domain(integers));
                    variables.add(variables1[i][j]);
                }
            }
        }

        //System.out.println(variables);

        for (int i = 0; i < variables1.length; i++) {
            for (int j = 0; j < variables1[i].length; j++) {
                Variable current = variables1[i][j];
                if (current == null) {
                    continue;
                }

                //Direct diagonals
                for (int k : Arrays.asList(-1, 1)) {
                    for (int l : Arrays.asList(-1, 1)) {
                        int i2 = i+k;
                        int j2 = j+l;
                        if (i2 >= 0 && i2 < variables1.length && j2 >= 0 && j2 < variables1[i2].length) {
                            Variable other = variables1[i2][j2];
                            if (other != null) {
                                if (k < 0) {
                                    if (l > 0) {
                                        constraints.add(new DirectDiagonalTR(current, other));
                                    } else {
                                        constraints.add(new DirectDiagonalTopLeft(current, other));
                                    }
                                } else {
                                    if (l > 0) {
                                        constraints.add(new DirectDiagonalBR(current, other));
                                    } else {
                                        constraints.add(new DirectDiagonalBL(current, other));
                                    }
                                }
                            }
                        }
                    }
                }

                for (int k : Arrays.asList(-1, 1)) {
                    //Direct horizontal
                    int j2 = j+k;
                    if (j2 >= 0 && j2 < variables1[i].length) {
                        Variable other = variables1[i][j2];
                        if (other != null) {
                            if (k > 0) {
                                constraints.add(new DirectRight(current, other));
                            } else {
                                constraints.add(new DirectLeft(current, other));
                            }
                        }
                    }

                    //Direct vertical
                    int i2 = i+k;
                    if (i2 >= 0 && i2 < variables1.length) {
                        Variable other = variables1[i2][j];
                        if (other != null) {
                            if (k > 0) {
                                constraints.add(new DirectBottom(current, other));
                            } else {
                                constraints.add(new DirectTop(current, other));
                            }
                        }
                    }
                }

                //Horizontal and vertical direction, leaving 2, and 1 free spaces
                for (int k : Arrays.asList(-3, -2, 2, 3)) {
                    int i2 = i+k;
                    int j2 = j+k;
                    //Vertical
                    if (i2 >= 0 && i2 < variables1.length) {
                        Variable other = variables1[i2][j];
                        if (other != null) {
                            if (k > 0) {
                                constraints.add(new HVBottom(current, other));
                            } else {
                                constraints.add(new HVTop(current, other));
                            }
                        }
                    }
                    //Horizontal
                    if (j2 >= 0 && j2 < variables1[i].length) {
                        Variable other = variables1[i][j2];
                        if (other != null) {
                            if (k > 0) {
                                constraints.add(new HVRight(current, other));
                            } else {
                                constraints.add(new HVLeft(current, other));
                            }
                        }
                    }
                }

                //Ranges first ones
                for (int k : Arrays.asList(-2, 2)) {
                    for (int l : Arrays.asList(-1, 1)) {
                        int i2 = i+k;
                        int j2 = j+l;
                        if (!(i2 >= 0 && i2 < variables1.length && j2 >= 0 && j2 < variables1[i2].length)) {
                            continue;
                        }
                        Variable other = variables1[i2][j2];
                        if (other == null) {
                            continue;
                        }
                        if (k > 0) {
                            if (l < 0) {
                                constraints.add(new Range1ShiftedBottomMinus(current, other));
                            } else {
                                constraints.add(new Range1ShiftedBottomPlus(current, other));
                            }
                        } else {
                            if (l < 0) {
                                constraints.add(new Range1ShiftedTopMinus(current, other));
                            } else {
                                constraints.add(new Range1ShiftedTopPlus(current, other));
                            }
                        }
                    }
                }
                //Ranges swapped
                for (int k : Arrays.asList(-2, 2)) {
                    for (int l : Arrays.asList(-1, 1)) {
                        int i2 = i+l;
                        int j2 = j+k;
                        if (!(i2 >= 0 && i2 < variables1.length && j2 >= 0 && j2 < variables1[i2].length)) {
                            continue;
                        }
                        Variable other = variables1[i2][j2];
                        if (other == null) {
                            continue;
                        }
                        if (k > 0) {
                            if (l < 0) {
                                constraints.add(new Range1ShiftedRightPlus(current, other));
                            } else {
                                constraints.add(new Range1ShiftedRightMinus(current, other));
                            }
                        } else {
                            if (l < 0) {
                                constraints.add(new Range1ShiftedLeftPlus(current, other));
                            } else {
                                constraints.add(new Range1ShiftedLeftMinus(current, other));
                            }
                        }
                    }
                }

                //Diagonals range 1
                for (int k : Arrays.asList(-2, 2)) {
                    int i2 = i+k;
                    int j2 = j+k;
                    //Vertical
                    if (i2 >= 0 && i2 < variables1.length) {
                        Variable other = variables1[i2][j];
                        if (other != null) {
                            if (k > 0) {
                                constraints.add(new D1Bottom(current, other));
                            } else {
                                constraints.add(new D1Top(current, other));
                            }
                        }
                    }
                    //Horizontal
                    if (j2 >= 0 && j2 < variables1[i].length) {
                        Variable other = variables1[i][j2];
                        if (other != null) {
                            if (k > 0) {
                                constraints.add(new D1Right(current, other));
                            } else {
                                constraints.add(new D1Left(current, other));
                            }
                        }
                    }
                }

                //Diagonals range 2 with 1 different, first row
                for (int k : Arrays.asList(-2, 2)) {
                    for (int l : Arrays.asList(-1, 1)) {
                        int i2 = i+k;
                        int j2 = j+l;
                        if (i2 >= 0 && i2 < variables1.length && j2 >= 0 && j2 < variables1[i2].length) {
                            Variable other = variables1[i2][j2];
                            if (other != null) {
                                if (k < 0) {
                                    constraints.add(new D2Diff1Top(current, other));
                                } else {
                                    constraints.add(new D2Diff1Bottom(current, other));
                                }
                            }
                        }
                    }
                }
                //Second row
                for (int k : Arrays.asList(-2, 2)) {
                    for (int l : Arrays.asList(-1, 1)) {
                        int i2 = i+l;
                        int j2 = j+k;
                        if (i2 >= 0 && i2 < variables1.length && j2 >= 0 && j2 < variables1[i2].length) {
                            Variable other = variables1[i2][j2];
                            if (other != null) {
                                if (k < 0) {
                                    constraints.add(new D2Diff1Left(current, other));
                                } else {
                                    constraints.add(new D2Diff1Right(current, other));
                                }
                            }
                        }
                    }
                }

                //Diagonals range 2 both
                for (int k : Arrays.asList(-2, 2)) {
                    for (int l : Arrays.asList(-2, 2)) {
                        int i2 = i+k;
                        int j2 = j+l;
                        if (i2 >= 0 && i2 < variables1.length && j2 >= 0 && j2 < variables1[i2].length) {
                            Variable other = variables1[i2][j2];
                            if (other != null) {
                                if (k == -2 && l == -2) {
                                    constraints.add(new D2TopLeft(current, other));
                                }
                                if (k == 2 && l == -2) {
                                    constraints.add(new D2BottomLeft(current, other));
                                }
                                if (k == -2 && l == 2) {
                                    constraints.add(new D2TopRight(current, other));
                                }
                                if (k == 2 && l == 2) {
                                    constraints.add(new D2BottomRight(current, other));
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < variables1.length; i++) {
            List<Variable> variablesOnRow = new ArrayList<>();
            List<Variable> variablesBelowRow = new ArrayList<>();
            List<Variable> variablesAboveRow = new ArrayList<>();
            for (int j = 0; j < variables1[i].length; j++) {
                if (variables1[i][j] != null) {
                    variablesOnRow.add(variables1[i][j]);
                } else {
                    if (i-1 >= 0 && variables1[i-1][j] != null) {
                        variablesAboveRow.add(variables1[i-1][j]);
                    }
                    if (i+1 < variables1.length && variables1[i+1][j] != null) {
                        variablesBelowRow.add(variables1[i+1][j]);
                    }
                }
            }
            constraints.add(new TentRowCountConstraint2(rowHints[i], variablesOnRow, variablesAboveRow, variablesBelowRow));
        }
        for (int j = 0; j < variables1[0].length; j++) {
            List<Variable> variablesOnColumn = new ArrayList<>();
            List<Variable> variablesLeftFromColumn = new ArrayList<>();
            List<Variable> variablesRightFromColumn = new ArrayList<>();
            for (int i = 0; i < variables1.length; i++) {
                if (variables1[i][j] != null) {
                    variablesOnColumn.add(variables1[i][j]);
                } else {
                    if (j-1 >= 0 && variables1[i][j-1] != null) {
                        variablesLeftFromColumn.add(variables1[i][j-1]);
                    }
                    if (j+1 < variables1[i].length && variables1[i][j+1] != null) {
                        variablesRightFromColumn.add(variables1[i][j+1]);
                    }
                }
            }
            constraints.add(new TentColumnCountConstraint2(colHints[j], variablesOnColumn, variablesLeftFromColumn, variablesRightFromColumn));
        }

        translateNonBinaries();

        /*for (Constraint constraint : constraints) {
            System.out.println(constraint);
        }

        VariableAssignment variableAssignment = new VariableAssignment();
        variableAssignment.assignValueTo(variables1[0][3], 0);
        variableAssignment.assignValueTo(variables1[0][5], 0);
        variableAssignment.assignValueTo(variables1[1][2], 3);
        variableAssignment.assignValueTo(variables1[1][6], 1);
        variableAssignment.assignValueTo(variables1[2][0], 1);
        variableAssignment.assignValueTo(variables1[3][1], 0);
        variableAssignment.assignValueTo(variables1[3][7], 0);
        variableAssignment.assignValueTo(variables1[4][2], 1);
        variableAssignment.assignValueTo(variables1[5][0], 0);
        variableAssignment.assignValueTo(variables1[5][6], 0);
        variableAssignment.assignValueTo(variables1[6][4], 0);
        variableAssignment.assignValueTo(variables1[6][7], 2);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);
        variableAssignment.assignValueTo(variables1[7][2], 0);

        for (Constraint constraint : constraints) {
            if (!constraint.isSatisfied(variableAssignment)) {
                System.out.println("Violated constraint: " + constraint);
            }
        }*/

        Map<Set<Variable>, Set<Constraint>> map = new HashMap<>();

        for (Constraint constraint : constraints) {
            Set<Constraint> constraintsList = new HashSet<>();
            constraintsList.add(constraint);
            for (Constraint other : constraints) {
                if (other.equals(constraint) || other.getVariables().size() != constraint.getVariables().size()) {
                    continue;
                }
                //The constraints have exactly the same scope
                if (other.getVariables().containsAll(constraint.getVariables())) {
                    constraintsList.add(other);
                }
            }
            Set<Variable> variableSet = Set.copyOf(constraint.getVariables());
            if (!map.containsKey(variableSet)) {
                map.put(variableSet, constraintsList);
            }
        }

        // Normalize problem, i.e. combine constraints which have the same scope
        for (Set<Constraint> constraintSet : map.values()) {
            if (constraintSet.size() == 1) {
                continue;
            }
            constraints.removeAll(constraintSet);
            constraints.add(new CombinedConstraint(new ArrayList<>(constraintSet)));
        }


        int k = Integer.MIN_VALUE;
        for (Constraint c : constraints) {
            if (c.getVariables().size() > k) {
                k = c.getVariables().size();
            }
        }
        //System.out.println("greatest arity: " + k);

    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }

    @Override
    public List<Constraint> getConstraints() {
        return constraints;
    }

    private Map<Variable, List<Constraint>> variableListMap = new HashMap<>();

    @Override
    public List<Constraint> getConstraintsFor(Variable variable) {
        return variableListMap.computeIfAbsent(variable, v -> {
            List<Constraint> cs = new ArrayList<>();
            for (Constraint c : constraints) {
                if (c.getVariables().contains(v)) {
                    cs.add(c);
                }
            }
            return cs;
        });
    }

    @Override
    public List<Object> getDomainFor(Variable variable) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void print() throws IOException {
        CellsOutput output = new CellsOutput();
        Cells[][] cells = new Cells[variables1.length][variables1[0].length];
        for (Cells[] row : cells) {
            Arrays.fill(row, Cells.BLANK);
        }
        for (int i = 0; i < variables1.length; i++) {
            for (int j = 0; j < variables1[i].length; j++) {
                if (variables1[i][j] == null) {
                    continue;
                }
                cells[i][j] = Cells.TREE;
                List<Integer> values = variables1[i][j].getDomain().getValues();
                int value = (Integer) variables1[i][j].getDomain().getValue(values.get(0));
                if (value == -2) {
                    cells[i-1][j] = Cells.TENT;
                }
                if (value == 2) {
                    cells[i+1][j] = Cells.TENT;
                }
                if (value == -1) {
                    cells[i][j-1] = Cells.TENT;
                }
                if (value == 1) {
                    cells[i][j+1] = Cells.TENT;
                }
            }
        }
        BufferedImage image = output.output(cells, rowHints, colHints);
        ImageIO.write(image, "png", getOutputFile());
    }

    private File getOutputFile() {
        File file = new File(System.getProperty("user.dir") + File.separator + "outputImages" + File.separator +  "output.png");
        if (file.exists()) {
            file.delete();
        }
        file.mkdirs();
        return file;
    }

    private void translateNonBinaries() {
        List<Variable> newVariables = new ArrayList<>();
        List<Constraint> newConstraints = new ArrayList<>();
        List<Constraint> removedConstraints = new ArrayList<>();
        for (Constraint constraint : constraints) {
            if (constraint.getVariables().size() > 3) {

                //ColumnConstraint
                if (constraint instanceof TentColumnCountConstraint2) {
                    List<Variable> hiddenVariables = new ArrayList<>();
                    var c = (TentColumnCountConstraint2) constraint;
                    //System.out.println("Looking at: " + c);
                    List<Variable> ls = c.getVariablesLeftFromColumn();
                    List<Variable> os = c.getVariablesOnColumn();
                    List<Variable> rs = c.getVariablesRightFromColumn();
                    List<Variable> all = c.getVariables();
                    int columnCount = c.getColumnCount();
                    Integer[] domainValues = new Integer[columnCount+1];
                    for (int i = 0; i < columnCount+1; i++) {
                        domainValues[i] = i;
                    }
                    for (int i = 0; i < all.size(); i++) {
                        if (i == 0) {
                            List<Integer> vals1 = null;
                            if (ls.contains(all.get(0))) {
                                vals1 = Arrays.asList(1);
                            }
                            if (os.contains(all.get(0))) {
                                vals1 = Arrays.asList(-2, 2);
                            }
                            if (rs.contains(all.get(0))) {
                                vals1 = Arrays.asList(-1);
                            }
                            List<Integer> vals2 = null;
                            if (ls.contains(all.get(1))) {
                                vals2 = Arrays.asList(1);
                            }
                            if (os.contains(all.get(1))) {
                                vals2 = Arrays.asList(-2, 2);
                            }
                            if (rs.contains(all.get(1))) {
                                vals2 = Arrays.asList(-1);
                            }
                            Variable hiddenVariable = new Variable("hidden"+newVariables.size(), new Domain(domainValues));
                            hiddenVariables.add(hiddenVariable);
                            newVariables.add(hiddenVariable);
                            newConstraints.add(new ValidAssignmentsCountSumConstraint(all.get(0), all.get(1), hiddenVariable, vals1, vals2));
                            //System.out.println("Created: " + newConstraints.get(newConstraints.size() - 1));
                            i++;
                        } else if (i == all.size() - 1) {
                            List<Integer> vals1 = null;
                            if (ls.contains(all.get(all.size() - 1))) {
                                vals1 = Arrays.asList(1);
                            }
                            if (os.contains(all.get(all.size() - 1))) {
                                vals1 = Arrays.asList(-2, 2);
                            }
                            if (rs.contains(all.get(all.size() - 1))) {
                                vals1 = Arrays.asList(-1);
                            }
                            Variable h = hiddenVariables.get(hiddenVariables.size() - 1);
                            newConstraints.add(new AddCountConstraint(h, all.get(all.size() - 1), columnCount, vals1));
                            //System.out.println("Created: " + newConstraints.get(newConstraints.size() - 1));
                        } else {
                            List<Integer> vals1 = null;
                            if (ls.contains(all.get(i))) {
                                vals1 = Arrays.asList(1);
                            }
                            if (os.contains(all.get(i))) {
                                vals1 = Arrays.asList(-2, 2);
                            }
                            if (rs.contains(all.get(i))) {
                                vals1 = Arrays.asList(-1);
                            }
                            Variable h = hiddenVariables.get(hiddenVariables.size() - 1);
                            Variable hiddenVariable = new Variable("hidden"+newVariables.size(), new Domain(domainValues));
                            hiddenVariables.add(hiddenVariable);
                            newVariables.add(hiddenVariable);
                            newConstraints.add(new AddCountConstraint(h, all.get(i), hiddenVariable, vals1));
                            //System.out.println("Created: " + newConstraints.get(newConstraints.size() - 1));
                        }
                        //newVariables.addAll(hiddenVariables);
                        removedConstraints.add(constraint);
                    }
                }



                //RowConstraint
                if (constraint instanceof TentRowCountConstraint2) {
                    List<Variable> hiddenVariables = new ArrayList<>();
                    var c = (TentRowCountConstraint2) constraint;
                    //System.out.println("Looking at: " + c);
                    List<Variable> as = c.getVariablesAboveRow();
                    List<Variable> os = c.getVariablesOnRow();
                    List<Variable> bs = c.getVariablesBelowRow();
                    List<Variable> all = c.getVariables();
                    int rowCount = c.getRowCount();
                    Integer[] domainValues = new Integer[rowCount+1];
                    for (int i = 0; i < rowCount+1; i++) {
                        domainValues[i] = i;
                    }
                    for (int i = 0; i < all.size(); i++) {
                        if (i == 0) {
                            List<Integer> vals1 = null;
                            if (as.contains(all.get(0))) {
                                vals1 = Arrays.asList(2);
                            }
                            if (os.contains(all.get(0))) {
                                vals1 = Arrays.asList(-1, 1);
                            }
                            if (bs.contains(all.get(0))) {
                                vals1 = Arrays.asList(-2);
                            }
                            List<Integer> vals2 = null;
                            if (as.contains(all.get(1))) {
                                vals2 = Arrays.asList(2);
                            }
                            if (os.contains(all.get(1))) {
                                vals2 = Arrays.asList(-1, 1);
                            }
                            if (bs.contains(all.get(1))) {
                                vals2 = Arrays.asList(-2);
                            }
                            Variable hiddenVariable = new Variable("hidden"+newVariables.size(), new Domain(domainValues));
                            hiddenVariables.add(hiddenVariable);
                            newVariables.add(hiddenVariable);
                            newConstraints.add(new ValidAssignmentsCountSumConstraint(all.get(0), all.get(1), hiddenVariable, vals1, vals2));
                            //System.out.println("Created: " + newConstraints.get(newConstraints.size() - 1));
                            i++;
                        } else if (i == all.size() - 1) {
                            List<Integer> vals1 = null;
                            if (as.contains(all.get(all.size() - 1))) {
                                vals1 = Arrays.asList(2);
                            }
                            if (os.contains(all.get(all.size() - 1))) {
                                vals1 = Arrays.asList(-1, 1);
                            }
                            if (bs.contains(all.get(all.size() - 1))) {
                                vals1 = Arrays.asList(-2);
                            }
                            Variable h = hiddenVariables.get(hiddenVariables.size() - 1);
                            newConstraints.add(new AddCountConstraint(h, all.get(all.size() - 1), rowCount, vals1));
                            //System.out.println("Created: " + newConstraints.get(newConstraints.size() - 1));
                        } else {
                            List<Integer> vals1 = null;
                            if (as.contains(all.get(i))) {
                                vals1 = Arrays.asList(2);
                            }
                            if (os.contains(all.get(i))) {
                                vals1 = Arrays.asList(-1, 1);
                            }
                            if (bs.contains(all.get(i))) {
                                vals1 = Arrays.asList(-2);
                            }
                            Variable h = hiddenVariables.get(hiddenVariables.size() - 1);
                            Variable hiddenVariable = new Variable("hidden"+newVariables.size(), new Domain(domainValues));
                            hiddenVariables.add(hiddenVariable);
                            newVariables.add(hiddenVariable);
                            newConstraints.add(new AddCountConstraint(h, all.get(i), hiddenVariable, vals1));
                            //System.out.println("Created: " + newConstraints.get(newConstraints.size() - 1));
                        }
                    }
                    //newVariables.addAll(hiddenVariables);
                    removedConstraints.add(constraint);
                }



            }
        }

        constraints.removeAll(removedConstraints);
        constraints.addAll(newConstraints);
        variables.addAll(newVariables);
        //System.out.println("New variables: " + newVariables);
        //System.out.println("Variables: " + variables);
    }

}
