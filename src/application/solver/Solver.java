package application.solver;

import javafx.util.Pair;
import java.util.List;

/**
 * Created by Culring on 2017-11-30.
 */
public interface Solver {
    // removes edges from the graph
    // and returns a list of time stamps
    // when a particular node lost its connection
    // to the capital (-1 denotes a case
    // when a node was disconnected even before
    // a call of this function; -2 denotes a capital)
    List<Integer> removeConnections(List<Pair<Integer, Integer>> connectionsToRemove);
}