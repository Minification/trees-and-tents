package de.mariushubatschek.is.benchmarking;

import de.mariushubatschek.is.algorithms.heuristics.values.LexicoValueHeuristic;
import de.mariushubatschek.is.algorithms.heuristics.variables.scoring.*;
import de.mariushubatschek.is.algorithms.search.*;
import de.mariushubatschek.is.algorithms.heuristics.values.RandomValueHeuristic;
import de.mariushubatschek.is.algorithms.heuristics.values.ValueHeuristic;
import de.mariushubatschek.is.data.PuzzleInfo;
import de.mariushubatschek.is.importing.DataImporter;
import de.mariushubatschek.is.problems.assignment.TentsAndTrees;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Evaluation {

    private Lock lock;

    public void run() throws Exception {
        lock = new ReentrantReadWriteLock().writeLock();
        long timeoutInMinutes = 2;
        int iterationCount = 5;
        DataImporter importer = new DataImporter();
        ExecutorService executorService = Executors.newCachedThreadPool();
        File file = new File(System.getProperty("user.dir") + File.separator + "nodeCountComparison" + File.separator + "output.csv");
        file.mkdirs();
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        try (DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get(Evaluation.class.getResource("/puzzleSamples/").toURI()))) {
            for (Path path : paths) {
                System.out.println("Puzzle: " + path.getFileName());
                PuzzleInfo puzzleInfo = importer.importFile(path);
                List<Instantiator<VariableScoring>> variableScoringInstantiators = Arrays.asList(
                        () -> new DomVariableScoring(),
                        () -> new DomDegVariableScoring(),
                        () -> new DomWdegVariableScoring(),
                        () -> new LexicoVariableScoring(),
                        () -> new RandomVariableScoring(new Random()),
                        () -> new MostConstrainedVariableScoring()
                );
                List<Instantiator<ValueHeuristic>> valueHeuristicInstantiators = Arrays.asList(
                        () -> new RandomValueHeuristic(new Random()),
                        () -> new LexicoValueHeuristic()
                );
                for (Instantiator<VariableScoring> variableHeuristicInstantiator : variableScoringInstantiators) {
                    for (Instantiator<ValueHeuristic> valueHeuristicInstantiator : valueHeuristicInstantiators) {
                        CountDownLatch countDownLatch = new CountDownLatch(iterationCount);
                        System.out.println(variableHeuristicInstantiator.getClass().getSimpleName() + ", " + valueHeuristicInstantiator.getClass().getSimpleName());
                        for (int iteration = 1; iteration <= iterationCount; iteration++) {
                            VariableScoring variableHeuristic = variableHeuristicInstantiator.create();
                            ValueHeuristic valueHeuristic = valueHeuristicInstantiator.create();
                            TentsAndTrees tt = new TentsAndTrees(puzzleInfo);
                            Backtracking backtracking = new Backtracking(variableHeuristic, valueHeuristic);
                            Future<?> future = executorService.submit(() -> {
                                SearchData searchData = backtracking.run(tt);
                                String algorithm = "Backtracking";
                                String variableHeuristicString = variableHeuristic.getClass().getSimpleName();
                                String valueHeuristicString = valueHeuristic.getClass().getSimpleName();
                                try {
                                    write(file, path.getFileName().toString(), algorithm, variableHeuristicString, valueHeuristicString, searchData.nodeCount, searchData.backtrackCount, searchData.timedOut);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                countDownLatch.countDown();
                            });
                            executorService.submit(() -> {
                                try {
                                    future.get(timeoutInMinutes, TimeUnit.MINUTES);
                                } catch (TimeoutException | InterruptedException e) {
                                    future.cancel(true);
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                    System.exit(-1);
                                }
                            });

                            for (LookAhead lookAhead : LookAhead.values()) {
                                for (LookBack lookBack : LookBack.values()) {
                                    System.out.println(lookBack.name() + ", " + lookAhead.name());
                                    VariableScoring variableHeuristic1 = variableHeuristicInstantiator.create();
                                    ValueHeuristic valueHeuristic1 = valueHeuristicInstantiator.create();
                                    TentsAndTrees ttt = new TentsAndTrees(puzzleInfo);
                                    GeneralSearch generalSearch = new GeneralSearch(variableHeuristic1, valueHeuristic1);
                                    generalSearch.setLookAhead(lookAhead);
                                    generalSearch.setLookBack(lookBack);
                                    Future<?> future1 = executorService.submit(() -> {
                                        SearchData searchData1 = generalSearch.run(ttt);
                                        String algorithm = generalSearch.getClass().getSimpleName() + " (" + lookBack.name() + ", " + lookAhead.name() + ")";
                                        String variableHeuristicString = variableHeuristic1.getClass().getSimpleName();
                                        String valueHeuristicString = valueHeuristic1.getClass().getSimpleName();
                                        try {
                                            write(file, path.getFileName().toString(), algorithm, variableHeuristicString, valueHeuristicString, searchData1.nodeCount, searchData1.backtrackCount, searchData1.timedOut);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        countDownLatch.countDown();
                                    });

                                    executorService.submit(() -> {
                                        try {
                                            future1.get(timeoutInMinutes, TimeUnit.MINUTES);
                                        } catch (TimeoutException | InterruptedException e) {
                                            future1.cancel(true);
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                            System.exit(-1);
                                        }
                                    });
                                }
                            }
                        }
                        countDownLatch.await();
                    }
                }
            }
        }
    }

    private synchronized void write(final File file, final String instance, final String algorithm, final String variableHeuristic, final String valueHeuristic, final long nodeCount, final long backtrackCount, boolean timeout) throws Exception {
        Logging.log(file, instance,  algorithm, variableHeuristic, valueHeuristic, nodeCount, backtrackCount, timeout);
    }

    public static void main(String[] args) throws Exception {
        new Evaluation().run();
    }

}
