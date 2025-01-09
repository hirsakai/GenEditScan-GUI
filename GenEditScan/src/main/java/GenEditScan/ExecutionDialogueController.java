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
 * Execution dialog controller class.
 *
 * @author NARO
 */
public class ExecutionDialogueController {

    //========================================================================//
    // fx:id
    //========================================================================//
    @FXML
    private AnchorPane executionDialogueID;     // dialogue
    @FXML
    private Label executionLabelID;             // label

    //========================================================================//
    // Local data
    //========================================================================//
    /**
     * confirmation of the button click (true:OK、false:CANCEL)
     */
    private static boolean isOk;

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Execution dialog controller class constructor.
     */
    public ExecutionDialogueController() {
    }

    /**
     * Execution dialog controller class constructor.
     *
     * @param message message
     * @param color   red:error, green:warning, blue:message
     * @param node    base screen for dialog
     */
    public ExecutionDialogueController(String message, String color, Node node) {
        // Set message
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("ExecutionDialogue.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            String errorMessage = "Could not open the dialog.";
            new ErrorDialogueController(errorMessage, "red", node);
        }
        ExecutionDialogueController executionDialogueController = loader.getController();
        executionDialogueController.executionLabelID.setText(message);
        executionDialogueController.executionLabelID.setStyle("-fx-text-fill: " + color);

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

    /**
     * Check if the OK button has been clicked.
     *
     * @return true:OK, false:CANCEL
     */
    public boolean isOk() {
        return ExecutionDialogueController.isOk;
    }

    //========================================================================//
    // On Action
    //========================================================================//

    /**
     * Action when OK button is clicked.
     */
    @FXML
    private void okAction() {
        ExecutionDialogueController.isOk = true;
        this.executionDialogueID.getScene().getWindow().hide();
    }

    /**
     * Action when CANCEL button is clicked.
     */
    @FXML
    private void cancelAction() {
        ExecutionDialogueController.isOk = false;
        this.executionDialogueID.getScene().getWindow().hide();
    }
}
