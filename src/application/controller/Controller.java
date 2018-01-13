package application.controller;

import application.exceptions.WrongInputException;
import application.parser.Parser;
import application.solver.BFSSolver;
import application.solver.FindAndUnionSolver;
import application.solver.Solver;
import application.testGenerator.Test;
import application.view.View;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

// TODO: run with different arguments

public class Controller {
    private Stage primaryStage;
    private View view;
    // number of connections to be removed
    private int numberOfEdgesToRemove;
    private StatesOfGraph statesOfGraph;
    private List<Integer> removedEdge = null;
    private Solver solver;
    private int numberOfVerticesInTests = 20;
    private boolean isTestMode;
    private enum SolverType{
        BFS,
        FIND_AND_UNION
    };
    SolverType solverType = SolverType.FIND_AND_UNION;

    public Controller(Stage primaryStage, List<String> args){
        this.primaryStage = primaryStage;
        String INVALID_OPTION_MESSAGE = "Invalid command. Run programme with option --help to see all options";

        try{
            Map<String, Map<String, Object>> options = Parser.parse(args);

            if(options.containsKey("--file")){
                isTestMode = false;
                String filename = (String)options.get("--file").get("filename");
                primaryStage.setTitle("Capital city - " + filename);
                try{
                    initView(loadDataFromFile(filename));
                }
                catch(IOException e){
                    System.err.println("Couldn't open a file");
                }
                catch(WrongInputException e){
                    System.err.println(
                            "File contains errors. Required format file:\n" +
                            "-> a number of vertices\n" +
                            "-> a label of a capital city\n" +
                            "-> a number of connections\n" +
                            "-> connections one per line, eg. 1 2\n" +
                            "-> a number of connections to remove\n" +
                            "-> connections to remove one per line."
                    );
                }
                if(options.containsKey("--bfs")){
                    solverType = SolverType.BFS;
                }
                else if(options.containsKey("--find_and_union")){
                    solverType = SolverType.FIND_AND_UNION;
                }
            }
            else if(options.containsKey("--generate")){
                isTestMode = true;
                primaryStage.setTitle("Capital city - " + "test mode");
                numberOfVerticesInTests = (Integer)options.get("--generate").get("size");
                if(options.containsKey("--bfs")){
                    solverType = SolverType.BFS;
                }
                else if(options.containsKey("--find_and_union")){
                    solverType = SolverType.FIND_AND_UNION;
                }
                initView(generateData(numberOfVerticesInTests));
            }
            else if(options.containsKey("--help")){
                System.err.println(
                        "--file <filename> [<solver_type>] - load test from file\n" +
                        "--generate <size> [<solver_type>] - generate tests with given size\n" +
                        "--help - display all oommands\n" +
                        "<solver_type> := [--bfs|--find_and_union]\n" +
                        "by default solver_type = --find_and_union"
                );
            }
            else{
                System.err.println(INVALID_OPTION_MESSAGE);
            }
        }
        catch(WrongInputException e){
            System.err.println(INVALID_OPTION_MESSAGE);
        }
    }

    private void initView(List<List<Integer>> data) {
        if(data == null){
            return;
        }
        int numberOfVertices = data.get(0).get(0),
                capital = data.get(1).get(0),
                connectionsNumber = data.get(2).get(0);
        numberOfEdgesToRemove = data.get(3 + connectionsNumber).get(0);
        this.view = new View(primaryStage, data);
        if(isTestMode) {
            view.setBottomButton(event -> initView(generateData(numberOfVerticesInTests)));
        }
        else{
            view.disableBottomButton();
        }
        view.setButtonOne(event -> {
            view.scrollListToElement(0);
            view.goToItem(0);
        }, "\nBEGIN\n ");
        view.setButtonTwo(event -> {
            view.scrollListToElement(numberOfEdgesToRemove);
            view.goToItem(numberOfEdgesToRemove);
        }, "\nEND\n ");
        view.setChangeListenerForListView((observable, oldValue, newValue) -> {
            int newIteration = newValue.getIndex(),
                    oldIteration;
            if(oldValue == null){
                oldIteration = 0;
            }
            else{
                oldIteration = oldValue.getIndex();
            }
            // don't unmark any edge if there is
            // starting point when no edge
            // is marked or a step just after
            // a selection of an end step
            if(removedEdge != null){
                view.revertMarkingEdge(removedEdge.get(0), removedEdge.get(1));
            }
            List<List<Integer>> edges;
            List<Integer> nodes;
            if(newIteration >= oldIteration){
                edges = statesOfGraph.getRemovedEdges(oldIteration, newIteration);
                nodes = statesOfGraph.getRemovedNodes(oldIteration, newIteration);
                view.removeEdges(edges);
                view.disableNodesWithEdges(nodes);
            }
            else{
                edges = statesOfGraph.getRemovedEdges(newIteration, oldIteration);
                nodes = statesOfGraph.getRemovedNodes(newIteration, oldIteration);
                view.enableNodesWithEdges(nodes);
                view.addEdges(edges);
            }
            // check for end step
            // then don't mark any edge
            if(newIteration < numberOfEdgesToRemove) {
                edges = statesOfGraph.getRemovedEdges(newIteration, newIteration + 1);
                view.markEdge(edges.get(0).get(0), edges.get(0).get(1));
                removedEdge = edges.get(0);
                nodes = statesOfGraph.getRemovedNodes(newIteration, newIteration+1);
                view.enableNodesWithEdges(nodes);
            }
            else{
                removedEdge = null;
            }
        });
        if(solverType == SolverType.FIND_AND_UNION){
            this.solver = new FindAndUnionSolver(
                    numberOfVertices,
                    capital,
                    convertListOfListsToListOfPairs(data.subList(3, 3 + connectionsNumber))
            );
        }
        else if(solverType == SolverType.BFS){
            this.solver = new BFSSolver(
                    numberOfVertices,
                    capital,
                    convertListOfListsToListOfPairs(data.subList(3, 3 + connectionsNumber))
            );
        }
        List<Integer> removalTimesData = solver.removeConnections(
                convertListOfListsToListOfPairs(data.subList(4 + connectionsNumber, data.size()))
        );
        List<List<Integer>> modifiedRemovalTimesData = modifyRemovalTimeData(removalTimesData,numberOfEdgesToRemove);
        view.disableNodesWithEdges(modifiedRemovalTimesData.get(0));
        statesOfGraph = new StatesOfGraph(
                data.subList(4 + connectionsNumber, data.size()),
                modifiedRemovalTimesData.subList(1, modifiedRemovalTimesData.size())
        );
    }

    private List<List<Integer>> loadDataFromFile(String path) throws IOException, WrongInputException{
        List<List<Integer>> output = loadFileAsListsOfIntegers(path);
        checkData(output);
        return output;
    }

    private void checkData(List<List<Integer>> data) throws WrongInputException{
        // file should contain at least number of vertices,
        // capital city, connections and connections to remove
        if(data.size() < 4){
            throw new WrongInputException();
        }
        assertLine(data.get(0), 1);
        assertLine(data.get(1), 1);
        assertLine(data.get(2), 1);
        int numberOfVertices = data.get(0).get(0);
        int numberOfConnections = data.get(2).get(0);
        if(data.size() < 4+numberOfConnections){
            throw new WrongInputException();
        }

        assertLine(data.get(3 + numberOfConnections), 1);
        int numberOfConnectionsToRemove = data.get(3 + numberOfConnections).get(0);
        if(data.size() != 4+numberOfConnections+numberOfConnectionsToRemove){
            throw new WrongInputException();
        }

        for(int i = 3; i<3+numberOfConnections; ++i){
            assertLine(data.get(i), 2);
            assertConnection(data.get(i), numberOfVertices);
        }
        for(int i = 4+numberOfConnections; i<data.size(); ++i){
            assertLine(data.get(i), 2);
            assertConnection(data.get(i), numberOfVertices);
        }
    }

    private void assertLine(List<Integer> line, int expectedSize) throws WrongInputException{
        if(line.size() != expectedSize){
            throw new WrongInputException();
        }
        for(int i : line){
            if(i < 0){
                throw new WrongInputException();
            }
        }
    }

    private void assertConnection(List<Integer> connection, int numberOfVertices) throws WrongInputException{
        if(connection.get(0) >= numberOfVertices || connection.get(1) >= numberOfVertices ||
                Objects.equals(connection.get(0), connection.get(1))){
            throw new WrongInputException();
        }
    }

    private List<List<Integer>> generateData(int size){
        List<List<Integer>> connections = Test.getTest(size);
        return convertDataToListData(size, new Random().nextInt(size), connections, connections);
    }

    private List<List<Integer>> convertDataToListData(int numberOfVertices, int capital, List<List<Integer>> connections,
                                                      List<List<Integer>> connectionsToRemove){
        List<List<Integer>> output = new LinkedList<>();
        List<Integer> list = new LinkedList<>();
        list.add(numberOfVertices);
        output.add(list);
        list = new LinkedList<>();
        list.add(capital);
        output.add(list);
        list = new LinkedList<>();
        list.add(connections.size());
        output.add(list);
        output.addAll(connections);
        list = new LinkedList<>();
        list.add(connectionsToRemove.size());
        output.add(list);
        output.addAll(connectionsToRemove);

        return output;
    }

    private List<List<Integer>> loadFileAsListsOfIntegers(String path) throws IOException, WrongInputException{
            List<String> lines;
            lines = Files.readAllLines(Paths.get(path));
            return parseListOfStringsToListOfIntegers(lines);
    }

    private List<List<Integer>> parseListOfStringsToListOfIntegers(List<String> input) throws WrongInputException{
        List<List<Integer>> array = new ArrayList<>();
        for(String line : input){
            String stringNumbers[] = line.split("\\s+");
            List<Integer> numbers = new ArrayList<>();
            for(String stringNumber : stringNumbers){
                try {
                    numbers.add(Integer.valueOf(stringNumber));
                }
                catch(java.lang.NumberFormatException e){
                    throw new WrongInputException();
                }
            }
            array.add(numbers);
        }
        return array;
    }

    private List<Pair<Integer, Integer>> convertListOfListsToListOfPairs(List<List<Integer>> list){
        Vector<Pair<Integer, Integer>> listOfPairs = new Vector<>(list.size());

        for (List<Integer> aList : list) {
            listOfPairs.add(new Pair<>(aList.get(0), aList.get(1)));
        }

        return listOfPairs;
    }

    // restructures data so that
    // i-th element of returned
    // list contains all nodes which
    // loose connection during i-th month
    private List<List<Integer>> modifyRemovalTimeData(List<Integer> removalTime, int removingStepsNumber){
        List<List<Integer>> modifiedRemovalTime = new ArrayList<>();
        for(int i = 0; i<removingStepsNumber+1; ++i){
            modifiedRemovalTime.add(new ArrayList<>());
        }
        for(int i = 0; i<removalTime.size(); ++i){
            // a capital
            if(removalTime.get(i) == -2) continue;
            modifiedRemovalTime.get(removalTime.get(i)+1).add(i);
        }
        return modifiedRemovalTime;
    }
}
