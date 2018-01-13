package application.model.solver;

import javafx.util.Pair;

import java.util.*;

/**
 * Created by Culring on 2017-12-05.
 */
public class BFSSolver implements Solver {
    private int numberOfVertices, capital;
    private List<Integer> neighbours[];

    public BFSSolver(int numberOfVertices, int capital, List<Pair<Integer, Integer>> connections){
        this.numberOfVertices = numberOfVertices;
        this.capital = capital;
        neighbours = new LinkedList[numberOfVertices];
        for(int i = 0; i<numberOfVertices; ++i){
            neighbours[i] = new LinkedList<>();
        }
        for(Pair<Integer, Integer> p : connections){
            connect(p.getKey(), p.getValue());
        }
    }

    public void connect(int i, int j){
        neighbours[i].add(j);
        neighbours[j].add(i);
    }

    @Override
    public List<Integer> removeConnections(List<Pair<Integer, Integer>> connectionsToRemove){
        // an output array
        ArrayList<Integer> removalTime = new ArrayList<>(numberOfVertices);
        for(int i = 0; i<numberOfVertices; ++i){
            removalTime.add(-1);
        }
        // mark differently a capital
        removalTime.set(capital, -2);

        // get all nodes accessible from a capital
        Vector<Integer> nodesConnectedToCapital = getSearchTreeIfNotConnected(capital, -1);
        boolean isConnectedToCapital[] = new boolean[numberOfVertices];
        for(int node : nodesConnectedToCapital){
            isConnectedToCapital[node] = true;
        }

        // remove each edge that is to be removed
        for(int i = 0; i<connectionsToRemove.size(); ++i){
            Pair<Integer, Integer> connectionToRemove = connectionsToRemove.get(i);
            int edge[] = new int[2];
            edge[0] = connectionToRemove.getKey();
            edge[1] = connectionToRemove.getValue();
            removeConnection(edge[0], edge[1]);
            // repeat that for each end of an edge
            for(int j = 0; j<2; ++j){
                // all nodes that became disconnected
                // during this iteration of removing edges
                Vector<Integer> disconnectedNodes;
                // check if lost its connection
                // to a capital during this iteration
                if(edge[j] != capital && isConnectedToCapital[edge[j]] &&
                        (disconnectedNodes = getSearchTreeIfNotConnected(edge[j], capital)) != null){
                    isConnectedToCapital[edge[j]] = false;
                    // mark that all nodes accessible from
                    // this node lost their connection to a capital
                    for(int disconnectedNode : disconnectedNodes){
                        removalTime.set(disconnectedNode, i);
                        isConnectedToCapital[disconnectedNode] = false;
                    }
                    break;
                }
            }
        }

        return removalTime;
    }

    private void removeConnection(int i, int j){
        neighbours[i].remove(new Integer(j));
        neighbours[j].remove(new Integer(i));
    }

    private Vector<Integer> getSearchTreeIfNotConnected(int startNode, int nodeToCheck){
        Queue<Integer> queue = new LinkedList<>();
        Vector<Integer> searchTree = new Vector<>();
        boolean isFound = false;
        boolean isVisited[] = new boolean[numberOfVertices];
        queue.add(startNode);
        while(!queue.isEmpty()){
            int node = queue.remove();
            searchTree.add(node);
            if(node == nodeToCheck){
                isFound = true;
                break;
            }
            isVisited[node] = true;
            for(int neighbour : neighbours[node]){
                if(!isVisited[neighbour]){
                    queue.add(neighbour);
                }
            }
        }

        if(!isFound){
            return searchTree;
        }
        else{
            return null;
        }
    }
}
