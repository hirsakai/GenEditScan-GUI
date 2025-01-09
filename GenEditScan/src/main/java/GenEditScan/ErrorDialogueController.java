/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Optional;

/**
 * Error dialog controller class.
 *
 * @author NARO
 */
public class ErrorDialogueController {
    //========================================================================//
    // fx:id
    //========================================================================//
    @FXML
    private AnchorPane errorDialogueID;     // dialogue
    @FXML
    private Label errorLabelID;             // label

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Error dialog controller class constructor.
     */
    public ErrorDialogueController() {
    }

    /**
     * Error dialog controller class constructor.
     *
     * @param message Message
     * @param color   red:error, green:warning, blue:message
     * @param node    Base screen for dialog
     */
    public ErrorDialogueController(String message, String color, Node node) {
        // Set message
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("ErrorDialogue.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            String errorMessage = "Could not open the dialog.";
            new ErrorDialogueController(errorMessage, "red", node);
        }
        ErrorDialogueController errorDialogueController = loader.getController();

        errorDialogueController.errorLabelID.setText(message);
        errorDialogueController.errorLabelID.setStyle("-fx-text-fill: " + color);

        // Open dialogue
        Stage stage = new Stage(StageStyle.UTILITY);
        Scene scene = new Scene(root);
        Optional.ofNullable(getClass().getResource("GenEditScan.css"))
                .ifPresent(resource -> scene.getStylesheets().add(resource.toExternalForm()));
        stage.setScene(scene);

        // Check dialogue type
        if (color.equals("blue")) {
            stage.setTitle("Message");
        } else if (color.equals("red")) {
            stage.setTitle("Error");
        } else {
            stage.setTitle("Warning");
        }

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(node.getScene().getWindow());
        stage.showAndWait();
    }

    //========================================================================//
    // On Action
    //========================================================================//

    /**
     * Action of Close the dialog when OK button is clicked.
     */
    @FXML
    private void okAction() {       // OK button
        this.errorDialogueID.getScene().getWindow().hide();
    }
}
