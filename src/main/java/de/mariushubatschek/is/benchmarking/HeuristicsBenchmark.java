package de.mariushubatschek.is.benchmarking;

import de.mariushubatschek.is.Main;
import de.mariushubatschek.is.algorithms.heuristics.variables.scoring.*;
import de.mariushubatschek.is.algorithms.search.Backtracking;
import de.mariushubatschek.is.algorithms.search.SearchData;
import de.mariushubatschek.is.algorithms.heuristics.values.RandomValueHeuristic;
import de.mariushubatschek.is.data.PuzzleInfo;
import de.mariushubatschek.is.importing.DataImporter;
import de.mariushubatschek.is.problems.assignment.TentsAndTrees;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HeuristicsBenchmark {

    @State(Scope.Benchmark)
    public static class RandomRandomHeuristic {

        public TentsAndTrees tt;

        public Backtracking backtracking;

        @Setup(Level.Invocation)
        @BenchmarkMode(Mode.AverageTime)
        @OutputTimeUnit(TimeUnit.NANOSECONDS)
        public void setup() throws Exception {
            DataImporter dataImporter = new DataImporter();
            PuzzleInfo puzzleInfo = dataImporter.importFile(Paths.get(Main.class.getResource("/puzzleSamples/tents_trees_0.csv").toURI()));
            //TreesAndTentsMinimizeVariables TT = new TreesAndTentsMinimizeVariables(puzzleInfo);
            tt = new TentsAndTrees(puzzleInfo);

            backtracking = new Backtracking(new RandomVariableScoring(new Random()), new RandomValueHeuristic(new Random()));
        }
    }

    @State(Scope.Benchmark)
    public static class DomRandomHeuristic {

        public TentsAndTrees tt;

        public Backtracking backtracking;

        @Setup(Level.Invocation)
        @BenchmarkMode(Mode.AverageTime)
        @OutputTimeUnit(TimeUnit.NANOSECONDS)
        public void setup() throws Exception {
            DataImporter dataImporter = new DataImporter();
            PuzzleInfo puzzleInfo = dataImporter.importFile(Paths.get(Main.class.getResource("/puzzleSamples/tents_trees_0.csv").toURI()));
            //TreesAndTentsMinimizeVariables TT = new TreesAndTentsMinimizeVariables(puzzleInfo);
            tt = new TentsAndTrees(puzzleInfo);

            backtracking = new Backtracking(new DomVariableScoring(), new RandomValueHeuristic(new Random()));
        }
    }

    @State(Scope.Benchmark)
    public static class DomDdegRandomHeuristic {

        public TentsAndTrees tt;

        public Backtracking backtracking;

        @Setup(Level.Invocation)
        @BenchmarkMode(Mode.AverageTime)
        @OutputTimeUnit(TimeUnit.NANOSECONDS)
        public void setup() throws Exception {
            DataImporter dataImporter = new DataImporter();
            PuzzleInfo puzzleInfo = dataImporter.importFile(Paths.get(Main.class.getResource("/puzzleSamples/tents_trees_0.csv").toURI()));
            //TreesAndTentsMinimizeVariables TT = new TreesAndTentsMinimizeVariables(puzzleInfo);
            tt = new TentsAndTrees(puzzleInfo);

            backtracking = new Backtracking(new DomDegVariableScoring(), new RandomValueHeuristic(new Random()));
        }
    }

    @State(Scope.Benchmark)
    public static class MostConstrainedVariableRandomHeuristic {

        public TentsAndTrees tt;

        public Backtracking backtracking;

        @Setup(Level.Invocation)
        @BenchmarkMode(Mode.AverageTime)
        @OutputTimeUnit(TimeUnit.NANOSECONDS)
        public void setup() throws Exception {
            DataImporter dataImporter = new DataImporter();
            PuzzleInfo puzzleInfo = dataImporter.importFile(Paths.get(Main.class.getResource("/puzzleSamples/tents_trees_0.csv").toURI()));
            //TreesAndTentsMinimizeVariables TT = new TreesAndTentsMinimizeVariables(puzzleInfo);
            tt = new TentsAndTrees(puzzleInfo);

            backtracking = new Backtracking(new MostConstrainedVariableScoring(), new RandomValueHeuristic(new Random()));
        }
    }

    @State(Scope.Benchmark)
    public static class LexicoRandomHeuristic {

        public TentsAndTrees tt;

        public Backtracking backtracking;

        @Setup(Level.Invocation)
        @BenchmarkMode(Mode.AverageTime)
        @OutputTimeUnit(TimeUnit.NANOSECONDS)
        public void setup() throws Exception {
            DataImporter dataImporter = new DataImporter();
            PuzzleInfo puzzleInfo = dataImporter.importFile(Paths.get(Main.class.getResource("/puzzleSamples/tents_trees_0.csv").toURI()));
            //TreesAndTentsMinimizeVariables TT = new TreesAndTentsMinimizeVariables(puzzleInfo);
            tt = new TentsAndTrees(puzzleInfo);

            backtracking = new Backtracking(new LexicoVariableScoring(), new RandomValueHeuristic(new Random()));
        }
    }

    @State(Scope.Benchmark)
    public static class DomWdegRandomHeuristic {

        public TentsAndTrees tt;

        public Backtracking backtracking;

        @Setup(Level.Invocation)
        @BenchmarkMode(Mode.AverageTime)
        @OutputTimeUnit(TimeUnit.NANOSECONDS)
        public void setup() throws Exception {
            DataImporter dataImporter = new DataImporter();
            PuzzleInfo puzzleInfo = dataImporter.importFile(Paths.get(Main.class.getResource("/puzzleSamples/tents_trees_0.csv").toURI()));
            //TreesAndTentsMinimizeVariables TT = new TreesAndTentsMinimizeVariables(puzzleInfo);
            tt = new TentsAndTrees(puzzleInfo);

            backtracking = new Backtracking(new DomWdegVariableScoring(), new RandomValueHeuristic(new Random()));
        }
    }

    public static void main(String[] args) throws IOException, RunnerException {
        Options opt = new OptionsBuilder()
                .include(HeuristicsBenchmark.class.getSimpleName())
                .timeUnit(TimeUnit.MILLISECONDS)
                .forks(1)
                .measurementTime(TimeValue.seconds(20))
                .mode(Mode.AverageTime)
                .measurementIterations(5)
                .warmupIterations(5)
                .build();
        Runner runner =  new Runner(opt);
        try {
            runner.run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    public SearchData measureRandomRandomHeuristic(RandomRandomHeuristic s) {
        return s.backtracking.run(s.tt);
    }

    @Benchmark
    public SearchData measureDomRandomHeuristic(DomRandomHeuristic s) {
        return s.backtracking.run(s.tt);
    }

    @Benchmark
    public SearchData measureDomDdegRandomHeuristic(DomDdegRandomHeuristic s) {
        return s.backtracking.run(s.tt);
    }

    @Benchmark
    public SearchData measureMostConstrainedVariableRandomHeuristic(MostConstrainedVariableRandomHeuristic s) {
        return s.backtracking.run(s.tt);
    }

    @Benchmark
    public SearchData measureLexicoRandomHeuristic(LexicoRandomHeuristic s) {
        return s.backtracking.run(s.tt);
    }

    @Benchmark
    public SearchData measureDomWdegRandomHeuristic(DomWdegRandomHeuristic s) {
        return s.backtracking.run(s.tt);
    }

}
