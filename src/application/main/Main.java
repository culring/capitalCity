package application.main;

import application.controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private Controller controller;
    private String args[];

    @Override
    public void start(Stage primaryStage) throws Exception{
        controller = new Controller(primaryStage, getParameters().getUnnamed());
    }

    public static void main(String[] args) {
        launch(args);
    }
}