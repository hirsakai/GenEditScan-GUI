/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Main screen controller class.
 *
 * @author NARO
 */
public class MainScreenController {
    @FXML
    private AnchorPane mainPaneID;          // Main window

    //========================================================================//
    // Tab menu fx:id
    //========================================================================//
    @FXML
    private TabPane mainTabPaneID;          // Main tab
    @FXML
    private Tab countMerTabID;              // Count mer tab
    @FXML
    private Tab drawGraphTabID;             // Draw graph tab

    //========================================================================//
    // Analysis [fx:include]
    //========================================================================//
    @FXML
    private CountMerController countMerController;    // Count mer controller

    @FXML
    private DrawGraphController drawGraphController;  // Drag graph controller

    //========================================================================//
    // Local class
    //========================================================================//
    /**
     * User configuration class.
     */
    private final UserConfiguration userConfiguration = new UserConfiguration();

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Constructor of Man screen controller class.
     */
    public MainScreenController() {
    }

    /**
     * Constructor of Man screen controller class.
     *
     * @param primaryStage primary stage
     */
    public MainScreenController(Stage primaryStage) {
        // Open screen
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("MainScreen.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error");
            errorAlert.setContentText("Could not open the main screen.");
            errorAlert.showAndWait();
            return;
        }
        MainScreenController mainScreenController = loader.getController();

        Scene scene = new Scene(root);
        Optional.ofNullable(getClass().getResource("GenEditScan.css"))
                .ifPresent(resource -> scene.getStylesheets().add(resource.toExternalForm()));
        primaryStage.setScene(scene);
        primaryStage.setTitle(ProgramVersionImpl.PROGRAM);
        primaryStage.setResizable(true);

        // Set UserConfiguration class.
        mainScreenController.setConfiguration();
        mainScreenController.countMerController.setMaxThreads();
        mainScreenController.drawGraphController.setRotateYticks(true);
        mainScreenController.drawGraphController.setLabelsColor();
        mainScreenController.drawGraphController.removeLegends();

        primaryStage.show();
    }

    //========================================================================//
    // MenuBar in Files [On Action]
    //========================================================================//

    /**
     * Action of Import configuration file.
     */
    @FXML
    private void importConfigAction() {
        File file = CommonTools.selectConfigurationFile(this.mainPaneID);
        if (file == null) {
            return;
        }

        int ret = this.userConfiguration.readConfigurationFile(file, this.mainPaneID);
        if (ret == 1) {
            this.importConfiguration();
        } else if (ret == 0) {
            String errorMessage = "Configuration file read error.";
            new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
        }
    }

    /**
     * Action of Export configuration file.
     */
    @FXML
    private void exportConfigAction() {
        File file = CommonTools.saveConfigurationFile(this.mainPaneID);
        if (file == null) {
            return;
        }

        this.exportConfiguration();
        boolean ret = this.userConfiguration.writeConfigurationFile(file);
        if (!ret) {
            String errorMessage = "Configuration file write error.";
            new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
        }
    }

    /**
     * Action of Close the main screen and exit the GUI.
     */
    @FXML
    private void closeAction() {
        Platform.exit();
    }

    //========================================================================//
    // MenuBar in Help [On Action]
    //========================================================================//

    /**
     * Displays the User guide.
     */
    @FXML
    private void userGuideAction() {
        File file = new File(System.getProperty("user.dir"));
        String pdfPath = "file://" + file.getPath() + File.separator + "GenEditScan-GUI_UserGuide.pdf";
        Application app = new Application() {
            @Override
            public void start(Stage primaryStage) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        app.getHostServices().showDocument(pdfPath);
    }

    /**
     * Displays program information in a dialog.
     */
    @FXML
    private void aboutAction() {
        String information = ProgramVersionImpl.PROGRAM + " " + ProgramVersionImpl.VERSION;
        new AboutDialogueController(information, this.mainPaneID);
    }

    //========================================================================//
    // Private function
    //========================================================================//

    /**
     * Set configuration file.
     */
    private void setConfiguration() {
        // Count mer
        this.countMerController.setConfiguration(this.userConfiguration);

        // Draw graph
        this.drawGraphController.setConfiguration(this.userConfiguration);

        this.countMerController.setDrawGraphController(this.drawGraphController, this.mainTabPaneID, this.drawGraphTabID);
    }

    /**
     * Import configuration file.
     */
    private void importConfiguration() {
        // Count mer
        this.countMerController.importConfiguration();

        // Draw graph
        this.drawGraphController.importConfiguration();
    }

    /**
     * Export configuration file.
     */
    private void exportConfiguration() {
        // Count mer
        this.countMerController.exportConfiguration();

        // Draw graph
        this.drawGraphController.exportConfiguration();
    }
}
