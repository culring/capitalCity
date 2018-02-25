package application.model.testGenerator;

import java.util.*;

/**
 * Created by Culring on 2017-12-09.
 */
public class Test {
    public static List<List<Integer>> getTest(int treeSize){
        // generate tree
        Tree tree = new Tree(treeSize);
        // generate special connection
        // and add to tree connections
        List<List<Integer>> connections = tree.getConnections();
        ArrayList<Integer> specialConnection = new ArrayList<>();
        int root = tree.getRoot(), node;
        Random random = new Random();
        while((node = random.nextInt(treeSize)) == root);
        specialConnection.add(root);
        specialConnection.add(node);
        connections.add(specialConnection);

        connections = convertDataToListData(treeSize, new Random().nextInt(treeSize), connections, connections);

        return connections;
    }

    private static List<List<Integer>> convertDataToListData(int numberOfVertices, int capital, List<List<Integer>> connections,
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
}