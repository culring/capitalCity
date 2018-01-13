package application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Culring on 2017-12-02.
 */
public class StatesOfGraph {
    private List<List<Integer>> connections;
    private List<List<Integer>> vertices;

    public StatesOfGraph(List<List<Integer>> connections, List<List<Integer>> nodes){
        this.connections = new ArrayList<>(connections);
        this.vertices = new ArrayList<>(nodes);
    }

    public List<List<Integer>> getRemovedEdges(int startIteration, int endIteration){
        return connections.subList(startIteration, endIteration);
    }

    public List<Integer> getRemovedNodes(int startIteration, int endIteration){
        List<Integer> removedNodes = new ArrayList<>();
        for(int i = startIteration; i<endIteration; ++i){
            removedNodes.addAll(vertices.get(i));
        }
        return removedNodes;
    }
}
