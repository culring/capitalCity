package application.model.timeMeasurement;

import application.model.solver.BFSSolver;
import application.model.solver.FindAndUnionSolver;
import application.model.solver.Solver;
import application.model.testGenerator.Test;
import javafx.util.Pair;

import java.io.DataOutput;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Culring on 2018-01-19.
 */
public class TimeMeasurement {
    private final String FILE_NAME = "results.txt";

    private int startAmount;
    private int maxAmount;
    private List<Integer> amounts;
    private int loopsForAmount;

    private double averageFindAndUnionTime;
    private double averageBFSTime;
    List<Pair<Integer, Double>> averageFindAndUnionTimes;
    List<Pair<Integer, Double>> averageBFSTimes;

    {
        averageFindAndUnionTime = 0;
        averageBFSTime = 0;
        averageBFSTimes = new LinkedList<>();
        averageFindAndUnionTimes = new LinkedList<>();
    }

    public TimeMeasurement(int startAmount, int maxAmount, int loopsForAmount) {
        this.startAmount = startAmount;
        this.maxAmount = maxAmount;
        this.loopsForAmount = loopsForAmount;
    }

    public TimeMeasurement(List<Integer> amounts, int loopsForAmount){
        this.amounts = amounts;
        this.loopsForAmount = loopsForAmount;
    }

    public void doMeasurements(){
        System.out.println("Starting measurement...");

        if(amounts != null){
            for(Integer i : amounts){
                measureTimesForAmount(i);
                averageBFSTimes.add(new Pair<>(i, averageBFSTime));
                averageFindAndUnionTimes.add(new Pair<>(i, averageFindAndUnionTime));
            }
        }
        else{
            for (int i = startAmount; i <= maxAmount; i++){
                measureTimesForAmount(i);
                averageBFSTimes.add(new Pair<>(i, averageBFSTime));
                averageFindAndUnionTimes.add(new Pair<>(i, averageFindAndUnionTime));
            }
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(FILE_NAME, "UTF-8");
            writer.println("amount\t\tfind and union\t\tq\t\tbfs\t\tq");
            System.out.println("amount\t\tfind and union\t\tq\t\tbfs\t\tq");
            DecimalFormat decimalFormat = new DecimalFormat("0.000");

            int medianIndex = averageBFSTimes.size()/2;
            int medianAmount = averageFindAndUnionTimes.get(medianIndex).getKey();
            double complexity, time;
            complexity = computeFindAndUnionEvaluation(medianAmount);
            time = averageFindAndUnionTimes.get(medianIndex).getValue();
            double findAndUnionCoefficient = complexity/time;
            complexity = computeBFSEvaluation(medianAmount);
            time = averageBFSTimes.get(medianIndex).getValue();
            double bfsCoefficient = complexity/time;

            Iterator<Pair<Integer, Double>> bfsIterator = averageBFSTimes.iterator();
            Iterator<Pair<Integer, Double>> findAndUnionIterator = averageFindAndUnionTimes.iterator();
            while(bfsIterator.hasNext()){
                Pair<Integer, Double> findAndUnionPair = findAndUnionIterator.next();
                Pair<Integer, Double> bfsPair = bfsIterator.next();
                averageFindAndUnionTime = findAndUnionPair.getValue();
                averageBFSTime = bfsPair.getValue();
                int amount = findAndUnionPair.getKey();
                double findAndUnionQ = (averageFindAndUnionTime/computeFindAndUnionEvaluation(amount))*findAndUnionCoefficient;
                double bfsQ = (averageBFSTime/computeBFSEvaluation(amount))*bfsCoefficient;

                String output = amount + "\t\t" + decimalFormat.format(averageFindAndUnionTime) + "\t\t\t" +
                        decimalFormat.format(findAndUnionQ) + "\t\t" +
                        decimalFormat.format(averageBFSTime) + "\t\t"
                        + decimalFormat.format(bfsQ);
                writer.println(output);
                System.out.println(output);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (writer != null){
            writer.close();
        }
        System.out.println("Measurements finished! Check out results in results.txt");
    }

    private double computeFindAndUnionEvaluation(int i){
        return i*Math.log(i)/Math.log(2);
    }

    private double computeBFSEvaluation(int i){
        return i*i;
    }

    private void measureTimesForAmount(int currentNumberOfVertices){
        for(int j = 0; j<loopsForAmount; ++j){
            averageBFSTime += measureSolvingTime2(currentNumberOfVertices);
            averageFindAndUnionTime += measureSolvingTime(currentNumberOfVertices);
        }
        averageFindAndUnionTime /= loopsForAmount;
        averageBFSTime /= loopsForAmount;
    }

    private long measureSolvingTime(int currentNumberOfVertices){
        List<List<Integer>> test = Test.getTest(currentNumberOfVertices);
        int numberOfVertices = test.get(0).get(0),
                capital = test.get(1).get(0),
                numberOfConnections = test.get(2).get(0),
                numberOfEdgesToRemove = test.get(3 + numberOfConnections).get(0);
        List<Pair<Integer, Integer>> connectionsToRemove =
                convertListOfListsToListOfPairs(test.subList(4 + numberOfConnections, test.size()));

        Solver solver = new FindAndUnionSolver(
                numberOfVertices,
                capital,
                convertListOfListsToListOfPairs(test.subList(3, 3 + numberOfConnections))
        );

        long start = System.currentTimeMillis();
        solver.removeConnections(connectionsToRemove);
        long stop = System.currentTimeMillis();

        return (stop-start);
    }

    private long measureSolvingTime2(int currentNumberOfVertices){
        List<List<Integer>> test = Test.getTest(currentNumberOfVertices);
        int numberOfVertices = test.get(0).get(0),
                capital = test.get(1).get(0),
                numberOfConnections = test.get(2).get(0),
                numberOfEdgesToRemove = test.get(3 + numberOfConnections).get(0);
        List<Pair<Integer, Integer>> connectionsToRemove =
                convertListOfListsToListOfPairs(test.subList(4 + numberOfConnections, test.size()));

        Solver solver = new BFSSolver(
                numberOfVertices,
                capital,
                convertListOfListsToListOfPairs(test.subList(3, 3 + numberOfConnections))
        );

        long start = System.currentTimeMillis();
        solver.removeConnections(connectionsToRemove);
        long stop = System.currentTimeMillis();

        return (stop-start);
    }

    private List<Pair<Integer, Integer>> convertListOfListsToListOfPairs(List<List<Integer>> list){
        Vector<Pair<Integer, Integer>> listOfPairs = new Vector<>(list.size());

        for (List<Integer> aList : list) {
            listOfPairs.add(new Pair<>(aList.get(0), aList.get(1)));
        }

        return listOfPairs;
    }

    public void warmup(){
        System.out.println("Warming up JVM...");
        for (int i = 3; i < 10; i++){
            for (int j = 0; j < 10; j++){
                measureSolvingTime(i);
                measureSolvingTime2(i);
            }
        }

        averageBFSTime = 0;
        averageFindAndUnionTime = 0;
    }
}
