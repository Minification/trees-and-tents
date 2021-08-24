package de.mariushubatschek.is;

import de.mariushubatschek.is.algorithms.heuristics.values.LexicoValueHeuristic;
import de.mariushubatschek.is.algorithms.heuristics.variables.scoring.DomWdegVariableScoring;
import de.mariushubatschek.is.algorithms.search.Backtracking;
import de.mariushubatschek.is.algorithms.search.SearchData;
import de.mariushubatschek.is.data.PuzzleInfo;
import de.mariushubatschek.is.importing.DataImporter;
import de.mariushubatschek.is.problems.assignment.TentsAndTrees;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        DataImporter dataImporter = new DataImporter();
        PuzzleInfo puzzleInfo = dataImporter.importFile(Paths.get(Main.class.getResource("/puzzleSamples/tents_trees_1.csv").toURI()));
        //TreesAndTentsMinimizeVariables TT = new TreesAndTentsMinimizeVariables(puzzleInfo);
        //TreesAndTents TT = new TreesAndTents(puzzleInfo);
        //TTTreesAreVariables TT = new TTTreesAreVariables(puzzleInfo);

        //PuzzleInfo puzzleInfo = dataImporter.importFile(Paths.get(System.getProperty("user.dir") + File.separator + "generatedPuzzles" + File.separator + "generated.csv"));
        TentsAndTrees TT = new TentsAndTrees(puzzleInfo);

        Backtracking backtracking = new Backtracking(new DomWdegVariableScoring(), new LexicoValueHeuristic());
        SearchData assignment = backtracking.run(TT);
        //System.out.println(assignment);
        TT.print();

        /*GeneralSearch generalSearch = new GeneralSearch(new DomVariableHeuristic(), new LexicoValueHeuristic());
        generalSearch.setLookAhead(LookAhead.MAC);
        generalSearch.setLookBack(LookBack.SBT);
        generalSearch.run(TT);
        TT.print();*/

        //GeneralSearch backtracking = new GeneralSearch(new DomHeuristic());
        //backtracking.run(TT);

        //SystematicSolver systematicSolver = new SystematicSolver(puzzleInfo);
        //systematicSolver.solve();
    }

}
