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

        return connections;
    }
}