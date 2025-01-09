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
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Optional;

/**
 * About dialog controller class.
 *
 * @author NARO
 */
public class AboutDialogueController {

    //========================================================================//
    // fx:id
    //========================================================================//
    @FXML
    private AnchorPane aboutDialogueID;     // dialogue
    @FXML
    private Label aboutLabelID;             // label
    @FXML
    private ListView<String> aboutListID;   // listView

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * About dialog controller class constructor.
     */
    public AboutDialogueController() {
    }

    /**
     * About dialog controller class constructor.
     *
     * @param information information
     * @param node        base screen for dialog
     */
    public AboutDialogueController(String information, Node node) {
        // Set information
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("AboutDialogue.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            String errorMessage = "Could not open the dialog.";
            new ErrorDialogueController(errorMessage, "red", node);
        }

        AboutDialogueController aboutDialogueController = loader.getController();
        aboutDialogueController.aboutLabelID.setText(information);
        aboutDialogueController.aboutLabelID.setStyle("-fx-text-fill: blue");

        // Batik
        aboutDialogueController.aboutListID.getItems().add(this.getBatik());
        aboutDialogueController.aboutListID.getItems().add(CommonTools.BR);

        // FOP
        aboutDialogueController.aboutListID.getItems().add(this.getFOP());
        aboutDialogueController.aboutListID.getItems().add(CommonTools.BR);

        // JFXConverter
        aboutDialogueController.aboutListID.getItems().add(this.getJFXConverter());
        aboutDialogueController.aboutListID.getItems().add(CommonTools.BR);

        // Colt
        aboutDialogueController.aboutListID.getItems().add(this.getColt());

        // Open dialogue
        Stage stage = new Stage(StageStyle.UTILITY);
        Scene scene = new Scene(root);
        Optional.ofNullable(getClass().getResource("GenEditScan.css"))
                .ifPresent(resource -> scene.getStylesheets().add(resource.toExternalForm()));
        stage.setScene(scene);

        stage.setTitle("About");
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
        this.aboutDialogueID.getScene().getWindow().hide();
    }

    //========================================================================//
    // Private function
    //========================================================================//

    /**
     * Get the Apache Batik license information.
     *
     * @return Apache Batik license information
     */
    private String getBatik() {
        return """
                Apache Batik
                Copyright 1999-2022 The Apache Software Foundation

                This product includes software developed at
                The Apache Software Foundation (http://www.apache.org/).

                This software contains code from the World Wide Web Consortium (W3C) for the
                Document Object Model API (DOM API) and SVG Document Type Definition (DTD).

                This software contains code from the International Organisation for
                Standardization for the definition of character entities used in the software's
                documentation.

                This product includes images from the Tango Desktop Project
                (http://tango.freedesktop.org/).

                This product includes images from the Pasodoble Icon Theme
                (http://www.jesusda.com/projects/pasodoble).
                """;
    }

    /**
     * Get the Apache FOP license information.
     *
     * @return Apache FOP license information
     */
    private String getFOP() {
        return """
                Apache FOP
                Copyright 1999-2024 The Apache Software Foundation

                This product includes software developed at
                The Apache Software Foundation (http://www.apache.org/).
                """;
    }

    /**
     * Get the JFXConverter license information.
     *
     * @return JFXConverter license information
     */
    private String getJFXConverter() {
        return """
                JFXConverter
                Copyright (c) 2016, 2020 Herve Girod
                All rights reserved.

                Redistribution and use in source and binary forms, with or without
                modification, are permitted provided that the following conditions are met:

                1. Redistributions of source code must retain the above copyright notice, this
                   list of conditions and the following disclaimer.
                2. Redistributions in binary form must reproduce the above copyright notice,
                   this list of conditions and the following disclaimer in the documentation
                   and/or other materials provided with the distribution.

                THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
                ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
                WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
                DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
                ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
                (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
                LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
                ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
                (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
                SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

                The views and conclusions contained in the software and documentation are those
                of the authors and should not be interpreted as representing official policies,
                either expressed or implied, of the FreeBSD Project.

                Alternatively if you have any questions about this project, you can visit
                the project website at the project page on https://sourceforge.net/projects/jfxconverter/
                """;
    }

    /**
     * Get the CERN Colt license information.
     *
     * @return CERN Colt license information
     */
    private String getColt() {
        return """
                Colt
                Copyright (c) 1999 CERN - European Organization for Nuclear Research.

                Permission to use, copy, modify, distribute and sell this software and its
                documentation for any purpose is hereby granted without fee, provided that the
                above copyright notice appear in all copies and that both that copyright notice
                and this permission notice appear in supporting documentation.

                CERN makes no representations about the suitability of this software for any purpose.
                It is provided "as is" without expressed or implied warranty.
                """;
    }
}
