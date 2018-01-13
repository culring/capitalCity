package application.model.solver;

import javafx.util.Pair;

import java.util.*;

/**
 * Created by Culring on 2018-01-09.
 */
public class FindAndUnionSolver implements Solver{
    private int numberOfVertices, capital;
    // all connections in graph
    private List<Pair<Integer, Integer>> connections;
    // find and union structure variables
    private int representative[], groupHeight[];
    // algorithm variables
    private Connection connectionDataArray[];
    private int time;

    public FindAndUnionSolver(int numberOfVertices, int capital, List<Pair<Integer, Integer>> connections){
        this.numberOfVertices = numberOfVertices;
        this.capital = capital;
        this.connections = new ArrayList<>();
        this.connections.addAll(connections);

        representative = new int[numberOfVertices];
        groupHeight = new int[numberOfVertices];
        for(int i = 0; i<numberOfVertices; ++i){
            representative[i] = i;
            groupHeight[i] = 1;
        }

        connectionDataArray = new Connection[numberOfVertices];
        for(int i = 0; i<numberOfVertices; ++i){
            connectionDataArray[i] = new Connection();
        }
        connectionDataArray[capital].isConnected = true;
    }

    @Override
    public List<Integer> removeConnections(List<Pair<Integer, Integer>> connectionsToRemove) {
        // get all connections that won't be
        // removed and connect nodes with them
        List<Pair<Integer, Integer>> connectionsNotRemoved = new ArrayList<>();
        Set<Pair<Integer, Integer>> connectionsToRemoveSet = new TreeSet<>((o1, o2) -> {
            if(o1.getKey() < o2.getKey() || (o1.getKey().equals(o2.getKey()) && o1.getValue() < o2.getValue())){
                return -1;
            }
            if(o1.getKey().equals(o2.getKey()) && o1.getValue().equals(o2.getValue())){
                return 0;
            }
            return 1;
        });
        connectionsToRemoveSet.addAll(connectionsToRemove);
        for(Pair<Integer, Integer> connection : connections){
            if(!connectionsToRemoveSet.contains(connection)){
                connectionsNotRemoved.add(connection);
            }
        }

        // first iteration of algorithm
        addNodesAndComputeConnectionToCapitalTimes(connectionsNotRemoved);
        // set data array adequately
        for(int i = 0; i<numberOfVertices; ++i){
            connectionDataArray[i].capitalTime = (connectionDataArray[i].isConnected) ? numberOfVertices : -1;
            if(connectionDataArray[i].parentTime != -1){
                connectionDataArray[i].parentTime = numberOfVertices;
            }
        }

        // second iteration of algorithm
        List<Pair<Integer, Integer>> connectionsToRemoveReversed = new ArrayList<>();
        for(int i = connectionsToRemove.size()-1; i>=0; --i){
            connectionsToRemoveReversed.add(connectionsToRemove.get(i));
        }
        List<Integer> connectionTimes = addNodesAndComputeConnectionToCapitalTimes(connectionsToRemoveReversed);
        connectionTimes.set(capital, -2);

        return connectionTimes;
    }

    private List<Integer> addNodesAndComputeConnectionToCapitalTimes(List<Pair<Integer, Integer>> connectionsToAdd){
        connectionDataArray[capital].capitalTime = numberOfVertices;

        List<Integer> connectionTimes = new ArrayList<>(numberOfVertices);
        for(int i = 0; i<numberOfVertices; ++i){
            connectionTimes.add(-2);
        }

        // compute all union data
        time = connectionsToAdd.size()-1;
        for(Pair<Integer, Integer> connection : connectionsToAdd){
            union(connection.getKey(), connection.getValue());
            --time;
        }

        // compute times when nodes got connected
        // to the capital
        for(int i = 0; i<numberOfVertices; ++i){
            computeConnectionTimeFromConnectionDataArray(i, connectionTimes);
        }

        return connectionTimes;
    }

    private int computeConnectionTimeFromConnectionDataArray(int node, List<Integer> connectionTimes){
        // if node is already visited
        if(connectionTimes.get(node) != -2){
            return connectionTimes.get(node);
        }
        // if we know from the algorithm that this node
        // is connected
        if(connectionDataArray[node].isConnected) {
            connectionTimes.set(node, connectionDataArray[node].capitalTime);
            return connectionDataArray[node].capitalTime;
        }
        // node has no parent, no need to ask
        // anybody upper in the tree,
        // for sure not connected
        if(representative[node] == node) {
            connectionTimes.set(node, -1);
            return -1;
        }
        // node not visited yet and need to
        // go upper in the tree to seek for the truth
        else{
            int parentConnectionTimeToCapital = computeConnectionTimeFromConnectionDataArray(representative[node], connectionTimes);
            // parent connected
            if(parentConnectionTimeToCapital >= 0){
                connectionDataArray[node].isConnected = true;
                connectionTimes.set(node, Math.min(parentConnectionTimeToCapital, connectionDataArray[node].parentTime));
                return Math.min(parentConnectionTimeToCapital, connectionDataArray[node].parentTime);
            }
            // parent not connected
            else{
                connectionTimes.set(node, -1);
                return -1;
            }
        }
    }

    private void union(int node1, int node2){
        // find representatives of nodes
        int parent1 = find(node1),
                parent2 = find(node2);

        // nodes are in the same tree
        if(parent1 == parent2){
            return;
        }

        // relation between those parents
        int parent, child;
        // attach smaller tree to bigger one
        if(groupHeight[parent1] >= groupHeight[parent2]){
            parent = parent1;
            child = parent2;

            if(groupHeight[parent1] == groupHeight[parent2]){
                ++groupHeight[parent1];
            }
        }
        else{
            parent = parent2;
            child = parent1;
        }
        representative[child] = parent;

        // set information about this union
        connectionDataArray[child].parentTime = time;
        if(connectionDataArray[child].isConnected){
            connectionDataArray[parent].isConnected = true;
            connectionDataArray[parent].capitalTime = time;
        }
    }

    private int find(int node){
        while(representative[node] != node){
            node = representative[node];
        }

        return node;
    }

    private class Connection{
        boolean isConnected = false;
        // time when node gets parent
        int parentTime = -1;
        // time when node gets connected
        // to capital
        int capitalTime = -1;
    }
}
