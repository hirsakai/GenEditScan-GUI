/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

/**
 * Alignment dialog controller class.
 * Alignment display of the sequences before and after including the k-mer.
 *
 * @author NARO
 */
public class AlignmentDialogueController implements Initializable {
    @FXML
    private AnchorPane mainPaneID;          // Main window

    //========================================================================//
    // fx:id
    //========================================================================//
    @FXML
    private Label vectorPositionID; // Vector position
    @FXML
    private Label kmerID;           // K-mer
    @FXML
    private Label thresholdID;      // threshold
    @FXML
    private Label basesID;          // bases
    @FXML
    private TableView<AlignmentDialogueItems> tableViewID;
    @FXML
    private TableColumn<AlignmentDialogueItems, Integer> positionID;  // Position
    @FXML
    private TableColumn<AlignmentDialogueItems, Integer> idID;        // ID
    @FXML
    private TableColumn<AlignmentDialogueItems, String> sequenceID;   // Sequence
    @FXML
    private TableColumn<AlignmentDialogueItems, Integer> mutantID;    // Mutant
    @FXML
    private TableColumn<AlignmentDialogueItems, Integer> wildTypeID;  // Wild type
    @FXML
    private TableColumn<AlignmentDialogueItems, Float> pvalueID;      // P-value
    @FXML
    private TableColumn<AlignmentDialogueItems, Float> fdrID;         // FDR
    @FXML
    private TableColumn<AlignmentDialogueItems, String> bonferroniID; // Bonferroni

    //========================================================================//
    // Static parameters
    //========================================================================//
    static String outsideFile;

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Alignment dialog controller class constructor.
     */
    public AlignmentDialogueController() {
    }

    /**
     * Alignment dialog controller class constructor.
     *
     * @param node     Base screen for dialog
     * @param startEnd Start and end positions on the vector sequence
     */
    public AlignmentDialogueController(Node node, int... startEnd) {
        // TableView items
        ObservableList<AlignmentDialogueItems> alignmentDialogueItems = FXCollections.observableArrayList();

        // Open screen
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("AlignmentDialogue.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            String errorMessage = "Could not open the dialog.";
            new ErrorDialogueController(errorMessage, "red", node);
        }

        AlignmentDialogueController alignmentDialogueController = loader.getController();

        int pos = 0;
        String vectorStart = null;
        File file = new File(outsideFile);
        try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
            // pos line
            int iPos = 0;
            int iCount = 1;
            int iKmer = 2;

            // seq line
            int iLeft = 0;
            int iRight = 1;
            int iMutant = 2;
            int iWildType = 3;
            int iPval = 6;
            int iFDR = 7;
            int iBonferroni = 8;

            String str = br.readLine();
            if (str.startsWith("#K-mer")) {
                String[] header = str.split("\t");
                alignmentDialogueController.kmerID.setText("K-mer: " + header[1]);
                alignmentDialogueController.thresholdID.setText("Threshold: " + header[3]);
                alignmentDialogueController.basesID.setText("Add " + header[5] + " bases");
            } else {
                String message = "Outside file (" + outsideFile + ") was formatted incorrectly, starting with (" + str + ").";
                new ErrorDialogueController(message, "red", node);
                return;
            }

            while ((str = br.readLine()) != null) {
                String[] posLine = str.split("\t");
                pos = Integer.parseInt(posLine[iPos]);

                if (startEnd.length > 1) {
                    if (pos < startEnd[0]) {
                        for (int i = 0; i < Integer.parseInt(posLine[iCount]); i++) {
                            br.readLine();
                        }
                        continue;
                    } else if (pos > startEnd[1]) {
                        break;
                    }
                }

                if (Objects.isNull(vectorStart)) {
                    vectorStart = String.valueOf(pos);
                }

                for (int i = 0; i < Integer.parseInt(posLine[iCount]); i++) {
                    String[] seqLine = br.readLine().split("\t");
                    int mutant = Integer.parseInt(seqLine[iMutant]);
                    int wildType = Integer.parseInt(seqLine[iWildType]);
                    String sequence = seqLine[iLeft] + "-" + posLine[iKmer] + "-" + seqLine[iRight];
                    float pval = Float.parseFloat(seqLine[iPval]);
                    float fdr = Float.parseFloat(seqLine[iFDR]);
                    float bonferroni = Float.parseFloat(seqLine[iBonferroni]);

                    AlignmentDialogueItems item = new AlignmentDialogueItems(pos, i + 1, sequence, mutant, wildType,
                            pval, fdr, bonferroni);
                    alignmentDialogueItems.add(item);
                }
            }
        } catch (IOException e) {
            String message = "Please select outside.txt first.";
            new ErrorDialogueController(message, "red", node);
            return;
        }

        alignmentDialogueController.vectorPositionID.setText("Vector position: " + vectorStart + " - " + pos);
        alignmentDialogueController.tableViewID.setItems(alignmentDialogueItems);
        alignmentDialogueController.tableViewID.getSelectionModel().setCellSelectionEnabled(true);

        // Open FXML
        Stage stage = new Stage(StageStyle.UTILITY);
        Scene scene = new Scene(root);
        Optional.ofNullable(getClass().getResource("GenEditScan.css"))
                .ifPresent(resource -> scene.getStylesheets().add(resource.toExternalForm()));
        stage.setScene(scene);

        stage.setTitle(ProgramVersionImpl.PROGRAM);
        stage.setResizable(true);

        stage.initModality(Modality.NONE);
        stage.initOwner(node.getScene().getWindow());
        stage.show();
    }

    /**
     * Initialize.
     *
     * @param url The location used to resolve relative paths for the root object, or
     *            {@code null} if the location is not known.
     * @param rb  The resources used to localize the root object, or {@code null} if
     *            the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.positionID.setCellValueFactory(new PropertyValueFactory<>("position"));
        this.idID.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.sequenceID.setCellValueFactory(new PropertyValueFactory<>("sequence"));
        this.mutantID.setCellValueFactory(new PropertyValueFactory<>("mutant"));
        this.wildTypeID.setCellValueFactory(new PropertyValueFactory<>("wildType"));
        this.pvalueID.setCellValueFactory(new PropertyValueFactory<>("pvalue"));
        this.fdrID.setCellValueFactory(new PropertyValueFactory<>("fdr"));
        this.bonferroniID.setCellValueFactory(new PropertyValueFactory<>("bonferroni"));

        // Context menu settings
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyMenuItem = new MenuItem("Copy");
        contextMenu.getItems().add(copyMenuItem);

        // Customize cell factory to add context menu
        this.sequenceID.setCellFactory(tc -> {
            TableCell<AlignmentDialogueItems, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    //setText(empty ? null : item);

                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        TextFlow textFlow = new TextFlow();
                        for (String word : item.split("")) {
                            Text text = new Text(word);
                            switch (word) {
                                case "A" -> text.setFill(Color.BLUE);
                                case "T" -> text.setFill(Color.ORANGE);
                                case "G" -> text.setFill(Color.GREEN);
                                case "C" -> text.setFill(Color.RED);
                                default -> text.setFill(Color.BLACK);
                            }
                            textFlow.getChildren().add(text);
                        }
                        setGraphic(new Group(textFlow));
                    }
                }
            };

            // Implementation of copy function
            copyMenuItem.setOnAction(event -> {
                if (cell.getItem() != null) {
                    final Clipboard clipboard = Clipboard.getSystemClipboard();
                    final ClipboardContent content = new ClipboardContent();
                    content.putString(cell.getItem());
                    clipboard.setContent(content);
                }
            });

            cell.setContextMenu(contextMenu);
            return cell;
        });
    }

    //========================================================================//
    // MenuBar in Files [On Action]
    //========================================================================//

    /**
     * Processing when the OK button is clicked.
     */
    @FXML
    private void okAction() {
        this.mainPaneID.getScene().getWindow().hide();
    }
}
