package de.mariushubatschek.is;

import de.mariushubatschek.is.algorithms.heuristics.values.RandomValueHeuristic;
import de.mariushubatschek.is.algorithms.heuristics.variables.scoring.DomDegVariableScoring;
import de.mariushubatschek.is.algorithms.search.GeneralSearch;
import de.mariushubatschek.is.algorithms.search.LookAhead;
import de.mariushubatschek.is.algorithms.search.LookBack;
import de.mariushubatschek.is.algorithms.search.SearchData;
import de.mariushubatschek.is.data.PuzzleInfo;
import de.mariushubatschek.is.importing.DataImporter;
import de.mariushubatschek.is.problems.assignment.TentsAndTrees;

import java.nio.file.Paths;
import java.util.Random;

public class MainTest {

    public static void main(String[] args) throws Exception {
        DataImporter dataImporter = new DataImporter();
        PuzzleInfo puzzleInfo = dataImporter.importFile(Paths.get(Main.class.getResource("/puzzleSamples/tents_trees_0.csv").toURI()));
        //TreesAndTentsMinimizeVariables TT = new TreesAndTentsMinimizeVariables(puzzleInfo);
        //TreesAndTents TT = new TreesAndTents(puzzleInfo);
        for (int i = 0; i < 10000; i++) {
            TentsAndTrees TT = new TentsAndTrees(puzzleInfo);

            GeneralSearch generalSearch = new GeneralSearch(new DomDegVariableScoring(), new RandomValueHeuristic(new Random()));
            generalSearch.setLookAhead(LookAhead.BC);
            generalSearch.setLookBack(LookBack.DBT);
            SearchData searchData = generalSearch.run(TT);
            System.out.println("Explored nodes: " + searchData.nodeCount);
            //TT.print();
        }

    }

}
