package application.view;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by Culring on 2017-11-30.
 */
public class View {
    private Stage primaryStage;
    private GridPane root;
    private Pane nodesLayout, connectionsLayout;
    private Pane []nodeWrappers;
    CirclePattern circlePattern;
    private final double GRAPH_SECTION_SIZE = 600, UI_SECTION_WIDTH = 300, HEIGHT = GRAPH_SECTION_SIZE;
    Button buttonOne, buttonTwo, generateTestButton;
    ListView<ListCellWrapper> listView;

    public View(Stage primaryStage, List<List<Integer>> input){
        this.primaryStage = primaryStage;
        root = new GridPane();
        int numberOfConnections = input.get(2).get(0);
        List<List<Integer>> graphData = input.subList(0, 3+numberOfConnections);
        List<List<Integer>> erasingGraphData = input.subList(4+numberOfConnections, input.size());
        root.add(initGraphLayout(graphData), 0, 0);
        root.add(initUILayout(erasingGraphData), 1, 0);

        Scene scene = new Scene(root, GRAPH_SECTION_SIZE + UI_SECTION_WIDTH, HEIGHT);
        scene.getStylesheets().add("application/view/viewGrayWhite.css");
        this.primaryStage.setWidth(GRAPH_SECTION_SIZE + UI_SECTION_WIDTH);
        this.primaryStage.setHeight(HEIGHT + 30);
        this.primaryStage.setScene(scene);
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
    }

    private Node initGraphLayout(List<List<Integer>> graphData){
        StackPane graphPane = new StackPane();
        nodesLayout = new Pane();
        connectionsLayout = new Pane();
        nodeWrappers = new StackPane[Integer.valueOf(graphData.get(0).get(0))];
        graphPane.getChildren().addAll(connectionsLayout, nodesLayout);
        circlePattern = new CirclePattern(graphData);

        connectionsLayout.setMinHeight(GRAPH_SECTION_SIZE);
        connectionsLayout.setMaxHeight(GRAPH_SECTION_SIZE);
        connectionsLayout.setMinWidth(GRAPH_SECTION_SIZE);

        nodesLayout.setMinHeight(GRAPH_SECTION_SIZE);
        nodesLayout.setMaxHeight(GRAPH_SECTION_SIZE);
        nodesLayout.setMinWidth(GRAPH_SECTION_SIZE);

        return graphPane;
    }

    private Pane initUILayout(List<List<Integer>> erasingConnections){
        Pane aLayout = aLayout(),
                bLayout = bLayout(erasingConnections),
                cLayout = cLayout();
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(aLayout);
        borderPane.setCenter(bLayout);
        borderPane.setBottom(cLayout);
        return borderPane;
    }

    private Pane aLayout(){
        TilePane pane = new TilePane();
        pane.getStyleClass().addAll("UILayout", "buttonsSection");
        buttonOne = new Button("STEP\nBACK");
        buttonTwo = new Button("STEP\nFORWARD");
        pane.getChildren().addAll(buttonOne, buttonTwo);
        buttonTwo.getStyleClass().add("button");
        buttonOne.getStyleClass().add("button");

        return pane;
    }

    public class ListCellWrapper{
        private String name;
        private int index;
        public ListCellWrapper(String name, int index){
            this.name = name;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private Pane bLayout(List<List<Integer>> erasingConnections){
        listView = new ListView<ListCellWrapper>();
        ObservableList<ListCellWrapper> items = FXCollections.observableArrayList();
        for(int i = 0; i < erasingConnections.size(); ++i){
            ListCellWrapper wrapper = new ListCellWrapper("month " + i + ": " +
                    erasingConnections.get(i).toString(), i);
            items.add(wrapper);
        }
        items.add(new ListCellWrapper("end", erasingConnections.size()));
        listView.setItems(items);
        TilePane pane = new TilePane(listView);
        pane.getStyleClass().addAll("UILayout");
        pane.setMaxHeight(Double.MAX_VALUE);
        pane.setAlignment(Pos.CENTER);
        return pane;
    }

    private Pane cLayout(){
        TilePane pane = new TilePane();
        generateTestButton = new Button("GENERATE NEW TEST");
        pane.getChildren().add(generateTestButton);
        generateTestButton.getStyleClass().add("button");
        pane.getStyleClass().addAll("UILayout", "buttonsSection");
        return pane;
    }

    public void setButtonOne(EventHandler<MouseEvent> handler, String text){
        buttonOne.setOnMouseClicked(handler);
        buttonOne.setText(text);
    }

    public void setButtonTwo(EventHandler<MouseEvent> handler, String text){
        buttonTwo.setOnMouseClicked(handler);
        buttonTwo.setText(text);
    }

    public void scrollListToElement(int element){
        listView.scrollTo(element);
    }

    public void goToItem(int item){
        listView.getSelectionModel().select(item);
        //listView.getFocusModel().focus(item);
    }

    public void setChangeListenerForListView(ChangeListener<ListCellWrapper> changeListener){
        listView.getSelectionModel().selectedItemProperty().addListener(changeListener);
    }

    public void setBottomButton(EventHandler<MouseEvent> handler){
        generateTestButton.setOnMouseClicked(handler);
    }

    public void disableBottomButton(){
        generateTestButton.setDisable(true);
    }

    public void addEdges(List<List<Integer>> edges){
        circlePattern.addEdges(edges);
    }

    public void removeEdges(List<List<Integer>> edges){
        circlePattern.removeEdges(edges);
    }

    public void enableNodesWithEdges(List<Integer> nodes){
        circlePattern.changeColorOfNodes(nodes, 1);
        circlePattern.changeColorOfEdgesBetweenNodes(nodes, 1);
    }

    public void disableNodesWithEdges(List<Integer> controlNodes){
        circlePattern.changeColorOfNodes(controlNodes, 2);
        circlePattern.changeColorOfEdgesBetweenNodes(controlNodes, 2);
    }

    public void markEdge(int i, int j){
        circlePattern.changeColorOfEdge(i, j, 3);
    }

    public void revertMarkingEdge(int i, int j){
        circlePattern.changeColorOfEdgeByColorsOfNodes(i, j);
    }
    
    private class CirclePattern {
        private final double RADIUS_TO_GRAPH_FIELD_RATIO = 0.4;
        private final double SMALL_CIRCLE_RATIO = 0.05;
        // parameters of a big circle
        private final double X = 0.5f*GRAPH_SECTION_SIZE,
                Y = 0.5f*GRAPH_SECTION_SIZE,
                RADIUS = RADIUS_TO_GRAPH_FIELD_RATIO*GRAPH_SECTION_SIZE;
        private final double SMALL_CIRCLE_RADIUS = SMALL_CIRCLE_RATIO* RADIUS;
        // an angle used as a start point for
        // clockwise drawing points (may be be negative)
        private final double BEGIN_ANGLE = -90;
        // how far multiple edges are from
        // a center of a node (0.0 - start
        // of an edge is in a center,
        // 1.0 - start is on a perimeter
        private final double MULTIPLE_EDGE_OFFSET_FACTOR = 0.25;
        // how much Bezier curves are curved
        // towards a perimeter of a big circle
        private final double BEZIER_CURVATURE_FACTOR = 0.5;
        // a number of NUMBER_OF_COLORS that nodes and
        // edges can be painted with, which are
        // specified in a css file
        private final int NUMBER_OF_COLORS = 3;
        // a single angle step while
        // drawing nodes
        private final double deltaAngle;

        // a structure containing information
        // about a current state of connections
        private Connections connections;
        private Circle [] nodes;
        private Node [] wrappers;
        private int numberOfVertices;
        private Point2D [] points;
        private final int capital;
        private int [] membership;

        CirclePattern(List<List<Integer>> graphData){
            this.numberOfVertices = graphData.get(0).get(0);
            this.deltaAngle = (double)360/(double)(numberOfVertices-1);
            this.capital = graphData.get(1).get(0);
            nodes = new Circle[numberOfVertices];
            wrappers = new Node[numberOfVertices];
            points = new Point2D[numberOfVertices];
            membership = new int[numberOfVertices];
            connections = new Connections(numberOfVertices);

            createNodes();
            try {
                if(graphData.get(2).get(0) > 0) {
                    setConnections(graphData.subList(3, graphData.size()));
                }
            }
            catch(Exception e) {
                System.err.println("Tried to connect invalid nodes");
                e.printStackTrace();
            }
        }

        void setConnections(List<List<Integer>> edges) throws Exception {
            for(List<Integer> edge : edges){
                connect(edge.get(0), edge.get(1));
            }
        }

        void setCoordinates(){
            membership[capital] = numberOfVertices-1;
            points[membership[capital]] = new Point2D(X, Y);
            for(int i = 0, k = 0; i < numberOfVertices; ++i){
                if(i == capital) continue;
                membership[i] = k++;
                points[membership[i]] = new Point2D(
                        X + RADIUS * Math.cos(Math.toRadians(BEGIN_ANGLE + k * deltaAngle)),
                        Y + RADIUS * Math.sin(Math.toRadians(BEGIN_ANGLE + k * deltaAngle))
                );
            }
        }

        private Circle createCircle(){
            Circle circle = new Circle(SMALL_CIRCLE_RADIUS);
            circle.getStyleClass().add("node1");
            return circle;
        }

        // initial setup of all nodes
        private void createNodes(){
            setCoordinates();

            for(int i = 0; i<numberOfVertices; ++i){
                Circle circle = createCircle();
                nodes[i] = circle;
                StackPane stackPane = setCircle(circle, i);
                nodesLayout.getChildren().add(stackPane);
                wrappers[i] = stackPane;
            }
        }

        // set a passed circle in the position
        // denoted by the i-th circle on the polygon
        StackPane setCircle(Circle circle, int i){
            // initialize a stack pane with
            // a node and number of this node
            Text text = new Text(Integer.toString(i));
            text.setFont(new Font(16));
            StackPane stackPane = new StackPane(circle, text);

            // get height and width of stack pane
            // to calculate position of it later
            Bounds stackPaneBounds = stackPane.getBoundsInLocal();
            double stackPaneHeight = stackPaneBounds.getHeight(),
                    stackPaneWidth = stackPaneBounds.getWidth();

            stackPane.setLayoutX(points[membership[i]].getX() - stackPaneWidth/2);
            stackPane.setLayoutY(points[membership[i]].getY() - stackPaneHeight/2);
            //stackPane.setStyle("-fx-background-color: red");

            return stackPane;
        }

        void connect(int i, int j) throws Exception{
            if(i < 0 || j < 0 || i >= numberOfVertices || j >= numberOfVertices){
                throw new Exception("Wrong nodes tried to be connected");
            }

            if(j > i){
                int temp = i;
                i = j;
                j = temp;
            }

            // get membership values (real positions of nodes
            // on a polygon)
            int startNode = membership[i], endNode = membership[j];
            Point2D startPoint = points[startNode], endPoint = points[endNode];
            // if a new connection is a next, multiple
            // connection, refer to a second connection
            // in the connections structure
            int freeSide = connections.checkFreeSide(i, j);
            Shape line;
            int color = getColorOfNode((i == capital) ? j : i);

            // in case of a polygonal consisting of
            // even number of vertices
            // a straight connection between opposing vertices
            // goes through a middle vertex
            // which is undesirable
            // therefore it is drawn a quadratic Bezier curve
            // omitting a middle vertex
            if(checkIsOpposite(i, j)){
                // it is needed to compute a third vertex
                // which will be later used as a control point
                // for a three-vertices Bezier curve
                Point2D controlPoint = getControlPoint(startNode, endNode, freeSide);
                // draw a Bezier curve
                line = drawBezier(startPoint, controlPoint, endPoint, color);
            }
            // otherwise draw a straight line
            else {
                // not multiple edge
                if(!connections.checkIsConnected(i, j)){
                    line = drawOffsetLine(startPoint, endPoint, 0, 0, color);
                }
                // multiple edge
                else{
                    // remove a previous centered line
                    // and change it for a new line with the offset
                    connectionsLayout.getChildren().remove(connections.get(i, j, 0));
                    connections.set(i, j, 0, drawOffsetLine(startPoint, endPoint, 0, MULTIPLE_EDGE_OFFSET_FACTOR * SMALL_CIRCLE_RADIUS, color));
                    connectionsLayout.getChildren().add(connections.get(i, j, 0));
                    line = drawOffsetLine(startPoint, endPoint, 1, MULTIPLE_EDGE_OFFSET_FACTOR * SMALL_CIRCLE_RADIUS, color);
                }
            }

            connections.set(i, j, freeSide, line);
            //connections.get(i, j, freeSide).getStyleClass().addAll("edge1", "edge");
            // add the edge to the connection pane
            connectionsLayout.getChildren().add(connections.get(i, j, freeSide));
        }

        boolean checkIsOpposite(int i, int j){
            return (numberOfVertices%2 == 1 && i != capital && j != capital && Math.abs(membership[i]-membership[j]) == (numberOfVertices-1)/2);
        }

        private Shape drawBezier(Point2D startPoint, Point2D controlPoint, Point2D endPoint, int color){
            // draw a Bezier curve
            QuadCurve quadCurve = new QuadCurve(
                    startPoint.getX(),
                    startPoint.getY(),
                    controlPoint.getX(),
                    controlPoint.getY(),
                    endPoint.getX(),
                    endPoint.getY()
            );
            quadCurve.setFill(new Color(0.0f, 0.0f, 0.0f, 0.0f));
            quadCurve.getStyleClass().addAll("edge", "edge" + color);
            return quadCurve;
        }

        private Point2D getControlPoint(int startNode, int endNode, int side){
            Point2D controlPoint;

            // if there is an odd number of vertices
            // in each half of the big circle
            // than a control point can be just taken
            // from the middle of the big circle
            if((numberOfVertices-3)%4 != 0){
                int controlNode;

                // check whether it is a second connection
                // it is needed to change sides
                if(side >= 1) {
                    controlNode = ((startNode+endNode)/2 + (numberOfVertices-1)/2)%(numberOfVertices-1);
                }
                else{
                    controlNode = (startNode+endNode)/2;
                }
                int capitalMembershipNumber = membership[capital];
                controlPoint = new Point2D(
                        (1-BEZIER_CURVATURE_FACTOR)*points[capitalMembershipNumber].getX() + BEZIER_CURVATURE_FACTOR*points[controlNode].getX(),
                        (1-BEZIER_CURVATURE_FACTOR)*points[capitalMembershipNumber].getY() + BEZIER_CURVATURE_FACTOR*points[controlNode].getY()
                );
            }
            // even number of vertices in each half
            // of the big circle so there is need to
            // get the control point from a triangle
            // consisted of the capital and two middle
            // circles
            else {
                if(side >= 1) {
                    controlPoint = fromBarycentricCoordinates(
                            points[((startNode + endNode)/2 + (numberOfVertices-1)/2)%(numberOfVertices-1)], BEZIER_CURVATURE_FACTOR/2,
                            points[membership[capital]], 1-BEZIER_CURVATURE_FACTOR,
                            points[((startNode + endNode)/2 + 1 + (numberOfVertices-1)/2)%(numberOfVertices-1)], BEZIER_CURVATURE_FACTOR/2
                    );
                }
                else{
                    controlPoint = fromBarycentricCoordinates(
                            points[(startNode + endNode)/2], BEZIER_CURVATURE_FACTOR/2,
                            points[membership[capital]], 1-BEZIER_CURVATURE_FACTOR,
                            points[(startNode + endNode)/2 + 1], BEZIER_CURVATURE_FACTOR/2
                    );
                }
            }

            return controlPoint;
        }

        private Line drawOffsetLine(Point2D startPoint, Point2D endPoint, int side, double offset, int color){
            // get a vector perpendicular to a line
            // connecting startPoint and endPoint
            // used to compute right edges
            Point2D perpendicularVector = getPerpendicularUnitVector(startPoint, endPoint);
            // set how far a line will be drawn
            // from a center of a circle
            perpendicularVector = perpendicularVector.multiply(offset);

            // if it is a multiple edge
            // there is need to change a unit vector
            // to compute a new edge
            if(side >= 1){
                perpendicularVector = perpendicularVector.multiply(-1);
            }
            startPoint = startPoint.add(perpendicularVector);
            endPoint = endPoint.add(perpendicularVector);

            Line line = new Line(
                    startPoint.getX(),
                    startPoint.getY(),
                    endPoint.getX(),
                    endPoint.getY()
            );
            line.getStyleClass().addAll("edge", "edge" + color);

            return line;
        }

        // return a circle which coordinates
        // are computed based on given barycentric
        // coordinates of passed circles
        private Point2D fromBarycentricCoordinates(
                Point2D a, double aCoef,
                Point2D b, double bCoef,
                Point2D c, double cCoef){
            return new Point2D(
                    aCoef*a.getX()+bCoef*b.getX()+cCoef*c.getX(),
                    aCoef*a.getY()+bCoef*b.getY()+cCoef*c.getY()
            );
        }

        private Point2D getPerpendicularUnitVector(Point2D pointA, Point2D pointB){
            Point2D vector = pointB.subtract(pointA).normalize();
            return new Point2D(vector.getY(), -vector.getX());
        }

        void addEdges(List<List<Integer>> edges){
            try {
                for (List<Integer> edge : edges) {
                    connect(edge.get(0), edge.get(1));
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        void removeEdges(List<List<Integer>> edges){
            for(List<Integer> edge : edges){
                int i = edge.get(0), j = edge.get(1);
                if(j > i){
                    int temp = i;
                    i = j;
                    j = temp;
                }
                int latestEdgeOrder = connections.getLatestEdgeOrder(i, j);
                connectionsLayout.getChildren().remove(connections.get(i, j, latestEdgeOrder));
                connections.set(i, j, latestEdgeOrder, null);

                // if i-th and j-th nodes are not
                // opposite and there is still
                // one edge remaining, it is needed
                // to update it
                if(!checkIsOpposite(i, j) && latestEdgeOrder == 1){
                    connectionsLayout.getChildren().remove(connections.get(i, j, 0));
                    Line line = drawOffsetLine(
                            points[membership[i]],
                            points[membership[j]],
                            0,
                            0,
                            getColorOfNode((i == capital) ? j : i)
                    );
                    connectionsLayout.getChildren().add(line);
                    connections.set(i, j, 0, line);
                }
            }
        }

        void changeColorOfNodes(List<Integer> nodesToChange, int color){
            for(int node : nodesToChange){
                nodes[node].getStyleClass().clear();
                nodes[node].getStyleClass().add("node" + color);
            }
        }

        void changeColorOfEdge(int i, int j, int color){
            int latestEdgeOrder = connections.getLatestEdgeOrder(i, j);
            changeColorOfEdge(i, j, latestEdgeOrder, color);
        }

        void changeColorOfEdge(int i, int j, int k, int color){
            if(j < i){
                int temp = i;
                i = j;
                j = temp;
            }

            if(connections.isEmpty(i, j, k)) return;
            connections.get(i, j, k).getStyleClass().clear();
            connections.get(i, j, k).getStyleClass().addAll("edge" + color, "edge");
        }

        void changeColorOfEdgesBetweenNodes(List<Integer> controlNodes, int color){
            for(int i = 0; i<controlNodes.size(); ++i){
                for(int j = i+1; j<controlNodes.size(); ++j){
                    changeColorOfEdge(controlNodes.get(i), controlNodes.get(j), 0, color);
                    changeColorOfEdge(controlNodes.get(i), controlNodes.get(j), 1, color);
                }
            }
        }

        void changeColorOfEdgeByColorsOfNodes(int v, int w){
            changeColorOfEdge(v, w, getColorOfNode(v));
        }

        int getColorOfNode(int v){
            for(int i = 1; i<= NUMBER_OF_COLORS; ++i){
                if(nodes[v].getStyleClass().contains("node" + i)){
                    return i;
                }
            }
            return 1;
        }

        private class Connections{
            // if an i-th node is connected with a j-th
            // node, then there is a Shape reference
            // in this array in a [i, j, k] position
            // where k denotes a k-th connection
            // between nodes (multiple edges -
            // only 2 allowed)
            private Shape connections[][][];

            Connections(int numberOfVertices){
                connections = new Shape[numberOfVertices][numberOfVertices][2];
            }

            Shape get(int i, int j, int side){
                if(i > j){
                    int temp = i;
                    i = j;
                    j = temp;
                }
                return connections[i][j][side];
            }
            
            void set(int i, int j, int side, Shape shape){
                if(i > j){
                    int temp = i;
                    i = j;
                    j = temp;
                }
                connections[i][j][side] = shape;
            }

            boolean isEmpty(int i, int j, int k){
                return (connections[i][j][k] == null);
            }

            int checkFreeSide(int i, int j) {
                return (get(i, j, 0) == null) ? 0 : 1;
            }

            boolean checkIsConnected(int i, int j){
                return !(get(i, j, 0) == null);
            }

            int getLatestEdgeOrder(int i, int j){
                return (get(i, j, 1) != null) ? 1 : 0;
            }
        }
    }
}
