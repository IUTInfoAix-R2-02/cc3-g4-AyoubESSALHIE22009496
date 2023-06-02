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

    private ObservableList<Circle> circlesList = FXCollections.observableArrayList();


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
            circlesList.get(i).visibleProperty().bind(noteList.get(i).isNotEqualTo(-1));
            toile.getChildren().add(circlesList.get(i));
        }
    }
    @FXML
    private void handleFieldAction(ActionEvent event) {
        TextField sourceOfEvent = (TextField) event.getSource();
        int axe = Integer.parseInt((String) sourceOfEvent.getUserData());
        try {
            noteList.get(axe-1).setValue(Double.parseDouble(sourceOfEvent.getText()));
            wrongGradesTyped.setValue(noteList.get(axe-1).get() < 0 || noteList.get(axe-1).get()  > noteMaximale);
            if (wrongGradesTyped.get()) { return; }
        } catch (NumberFormatException nfe) {
            return;
        }
        circlesList.get(axe-1).setCenterX(getXRadarChart(noteList.get(axe-1).get(),axe));
        circlesList.get(axe-1).setCenterY(getYRadarChart(noteList.get(axe-1).get() ,axe));
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

    int getXRadarChart(double value, int axe ){
        return (int) (rayonCercleExterieur + Math.cos(Math.toRadians(angleDepart - (axe-1)  * angleEnDegre)) * rayonCercleExterieur
                *  (value / noteMaximale));
    }

    int getYRadarChart(double value, int axe ){
        return (int) (rayonCercleExterieur - Math.sin(Math.toRadians(angleDepart - (axe-1)  * angleEnDegre)) * rayonCercleExterieur
                *  (value / noteMaximale));
    }

}
