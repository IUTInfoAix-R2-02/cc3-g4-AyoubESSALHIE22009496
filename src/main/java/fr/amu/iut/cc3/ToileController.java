package fr.amu.iut.cc3;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ToileController implements Initializable {
    @FXML
    private Pane toile;

    @FXML
    private TextField comp1;
    @FXML
    private TextField comp2;
    @FXML
    private TextField comp3;
    @FXML
    private TextField comp4;
    @FXML
    private TextField comp5;
    @FXML
    private TextField comp6;

    @FXML
    private Label errorLabel;

    private BooleanProperty wrongGradesTyped = new SimpleBooleanProperty(false);

    private ObservableList<SimpleDoubleProperty> noteList = FXCollections.observableArrayList();

    private ObservableList<SimpleIntegerProperty> xValues = FXCollections.observableArrayList();

    private ObservableList<SimpleIntegerProperty> YValues = FXCollections.observableArrayList();

    private ObservableList<Circle> circlesList = FXCollections.observableArrayList();
    private ObservableList<Line>   linesList = FXCollections.observableArrayList();
    private boolean linesTraced = false;


    private static int rayonCercleExterieur = 200;
    private static int angleEnDegre = 60;
    private static int angleDepart = 90;
    private static int noteMaximale = 20;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLabel.textProperty().bind(Bindings.when(wrongGradesTyped).then("Erreur de Saisie :\nLes valeurs doivent être entre 0 et 20").otherwise(""));
        for (int i = 0; i < 6; ++i){
            noteList.add(new SimpleDoubleProperty(-1));
            circlesList.add(new Circle(5));
            linesList.add(new Line());
            xValues.add(new SimpleIntegerProperty());
            YValues.add(new SimpleIntegerProperty());
            setObservablexValues(noteList.get(i),i);
            setObservableYValues(noteList.get(i),i);
            circlesList.get(i).visibleProperty().bind(noteList.get(i).isNotEqualTo(-1));
            circlesList.get(i).centerXProperty().bind(xValues.get(i));
            circlesList.get(i).centerYProperty().bind(YValues.get(i));
            linesList.get(i).visibleProperty().bind(noteList.get(i).isNotEqualTo(-1));
            toile.getChildren().add(circlesList.get(i));
            toile.getChildren().add(linesList.get(i));
        }

    }
    @FXML
    private void handleFieldAction(ActionEvent event) {
        TextField sourceOfEvent = (TextField) event.getSource();
        int axe = Integer.parseInt((String) sourceOfEvent.getUserData());
        double note = 0;
        try {
            note = Double.parseDouble(sourceOfEvent.getText());
            wrongGradesTyped.setValue(note < 0 || note  > noteMaximale);
            if (wrongGradesTyped.get()) { return; }
        } catch (NumberFormatException nfe) {
            return;
        }
        noteList.get(axe-1).setValue(note);
    }

    @FXML
    private void handleEmptyButton(ActionEvent event){
        comp1.setText("");
        comp2.setText("");
        comp3.setText("");
        comp4.setText("");
        comp5.setText("");
        comp6.setText("");
        errorLabel.setText("");
        for (SimpleDoubleProperty note : noteList){
            note.setValue(-1);
        }
    }

    @FXML
    private void traceLines(){
        for (SimpleDoubleProperty notes : noteList) {
            if (notes.get() == -1)
                return;
        }
        for (int i = 0, competencesMax = 6; i < competencesMax; ++i) {
            if (i == competencesMax-1){
                linesList.get(i).startXProperty().bind(xValues.get(i));
                linesList.get(i).endXProperty().bind(xValues.get(0));
                linesList.get(i).startYProperty().bind(YValues.get(i));
                linesList.get(i).endYProperty().bind(YValues.get(0));
            }
            else {
                linesList.get(i).startXProperty().bind(xValues.get(i));
                linesList.get(i).endXProperty().bind(xValues.get(i+1));
                linesList.get(i).startYProperty().bind(YValues.get(i));
                linesList.get(i).endYProperty().bind(YValues.get(i+1));
            }
        }
        linesTraced = true;
        System.out.println(xValues.get(0).getValue());
    }

    int getXRadarChart(double value, int axe ){
        return (int) (rayonCercleExterieur + Math.cos(Math.toRadians(angleDepart - (axe-1)  * angleEnDegre)) * rayonCercleExterieur
                *  (value / noteMaximale));
    }

    int getYRadarChart(double value, int axe ){
        return (int) (rayonCercleExterieur - Math.sin(Math.toRadians(angleDepart - (axe-1)  * angleEnDegre)) * rayonCercleExterieur
                *  (value / noteMaximale));
    }

    public void setObservablexValues(SimpleDoubleProperty note,int i) {
        IntegerBinding observableXValue = new IntegerBinding() {
            {
                super.bind(note);
            }
            @Override
            protected int computeValue() {
                return getXRadarChart(note.get(),i+1);
            }
        };
        xValues.get(i).bind(observableXValue);
        System.out.println("bind fait");
    }

    public void setObservableYValues(SimpleDoubleProperty note,int i) {
        IntegerBinding observableXValue = new IntegerBinding() {
            {
                super.bind(note);
            }
            @Override
            protected int computeValue() {
                return getYRadarChart(note.get(),i+1);
            }
        };
        YValues.get(i).bind(observableXValue);
        System.out.println("bind fait");
    }
}
