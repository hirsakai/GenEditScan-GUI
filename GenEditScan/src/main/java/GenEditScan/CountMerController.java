/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.*;

import static java.lang.Math.min;

/**
 * Count mer controller class.
 *
 * @author NARO
 */
public class CountMerController extends ControllerBase implements Initializable {
    @FXML
    private AnchorPane mainPaneID;              // Main window

    //========================================================================//
    // fx:id
    //========================================================================//
    @FXML
    private TextField vectorFileID;             // Vector sequence
    @FXML
    private Label countMerMutantLabelID;        // Mutant
    @FXML
    private Label countMerWildTypeLabelID;      // Wild type
    @FXML
    private TextArea mutantFilesID;             // (Mutant) FASTQ files
    @FXML
    private TextArea wildTypeFilesID;           // (Wild type) FASTQ files
    @FXML
    private Spinner<Integer> spinnerKmerID;     // K-mer
    @FXML
    private TextField outPrefixCountMerID;      // Output prefix
    @FXML
    private TextField outDirectoryCountMerID;   // Output directory
    @FXML
    private CheckBox checkOutsideKmerID;        // Outside the k-mer sequences
    @FXML
    private Spinner<Double> spinnerFdrID;       // Threshold by FDR
    @FXML
    private Spinner<Integer> spinnerBasesID;    // Number of bases on each side
    @FXML
    private Spinner<Integer> spinnerThreadsID;  // Maximum number of threads
    @FXML
    private Button stopCountMerID;              // Stop
    @FXML
    private Button executeCountMerID;           // Execute
    @FXML
    private ProgressBar progressID;             // Progress bar
    @FXML
    private Label messageID;

    //========================================================================//
    // Static data
    //========================================================================//
    // Mutant files
    private List<String> mutant_files = new ArrayList<>();

    // Wild type fils
    private List<String> wildType_files = new ArrayList<>();

    // Number of threads
    private int threads = 1;

    //========================================================================//
    // Local class
    //========================================================================//
    /**
     * Match analysis of k-mer
     */
    KmerMatch kmerMatch;

    /**
     * Extension analysis of k-mer
     */
    KmerExtension kmerExtension;

    /**
     * Graph drawing process controller
     */
    DrawGraphController drawGraphController;

    //========================================================================//
    // Local data
    //========================================================================//
    /**
     * service
     */
    private ExecutorService service;

    /**
     * future list
     */
    private final List<Future<?>> futureList = new ArrayList<>();

    //========================================================================//
    // Local parameter
    //========================================================================//
    /**
     * Main window's tab
     */
    private TabPane mainTabPane;

    /**
     * Draw graph window's tab
     */
    private Tab drawGraphTab;

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Count mer controller constructor.
     */
    public CountMerController() {
    }

    /**
     * Set Draw graph constructor.
     *
     * @param dgc     Graph drawing process class
     * @param mainTab main tab
     * @param dgTab   Draw graph tab
     */
    public void setDrawGraphController(DrawGraphController dgc, TabPane mainTab, Tab dgTab) {
        this.drawGraphController = dgc;
        this.mainTabPane = mainTab;
        this.drawGraphTab = dgTab;
    }

    /**
     * Get the data from the Configuration file.
     */
    public void importConfiguration() {
        super.setTextField(this.vectorFileID, super.userConfiguration.getVector_file());
        CommonTools.setTextArea(this.mutantFilesID, super.userConfiguration.getMutant_files());
        CommonTools.setTextArea(this.wildTypeFilesID, super.userConfiguration.getWildType_files());
        super.setSpinner(this.spinnerKmerID, super.userConfiguration.getKmer());
        super.setTextField(this.outPrefixCountMerID, super.userConfiguration.getOut_prefix());
        super.setTextField(this.outDirectoryCountMerID, super.userConfiguration.getOutDirectory());
        super.setCheckBox(this.checkOutsideKmerID, super.userConfiguration.getOutsideKmerSequences());
        super.setSpinnerDouble(this.spinnerFdrID, super.userConfiguration.getThresholdFdr());
        super.setSpinner(this.spinnerBasesID, super.userConfiguration.getNumberOfBasesOnEachSide());
        super.setSpinner(this.spinnerThreadsID, super.userConfiguration.getThreads());
        this.mutant_files = CommonTools.getTextArea(this.mutantFilesID);
        this.wildType_files = CommonTools.getTextArea(this.wildTypeFilesID);
        this.threads = this.spinnerThreadsID.getValue();
    }

    /**
     * Set the data in the Configuration file.
     */
    public void exportConfiguration() {
        super.userConfiguration.setVector_file(this.vectorFileID);
        super.userConfiguration.setMutant_files(this.mutantFilesID);
        super.userConfiguration.setWildType_files(this.wildTypeFilesID);
        super.userConfiguration.setKmer(this.spinnerKmerID);
        super.userConfiguration.setOut_prefix(this.outPrefixCountMerID);
        super.userConfiguration.setOutDirectory(this.outDirectoryCountMerID);
        super.userConfiguration.setOutsideKmerSequences(this.checkOutsideKmerID);
        super.userConfiguration.setThresholdFdr(this.spinnerFdrID);
        super.userConfiguration.setNumberOfBasesOnEachSide(this.spinnerBasesID);
        super.userConfiguration.setThreads(this.spinnerThreadsID);
    }

    /**
     * Set an initial value for the maximum number of threads used.
     */
    public void setMaxThreads() {
        int maxThreads = min(Runtime.getRuntime().availableProcessors(), 8);
        this.setSpinner(this.spinnerThreadsID, maxThreads);
    }

    /**
     * Initialize.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Outside th k-mer sequences CheckBox operation
        this.checkOutsideKmerID.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.spinnerFdrID.setDisable(false);
                this.spinnerBasesID.setDisable(false);
            } else {
                this.spinnerFdrID.setDisable(true);
                this.spinnerBasesID.setDisable(true);
            }
        });

        // Maximum number of threads
        this.spinnerThreadsID.valueProperty().addListener((observe, oldVal, newVal) -> this.threads = newVal);
    }

    //========================================================================//
    // Count mer [On Action]
    //========================================================================//

    /**
     * Select a vector file.
     */
    @FXML
    private void selectVectorAction() {
        CommonTools.selectFastaFile(this.vectorFileID, this.mainPaneID);
    }

    /**
     * Select mutant's read files.
     */
    @FXML
    private void selectMutantReadAction() {
        CommonTools.selectFastqFiles(this.mutantFilesID, this.mainPaneID);
        this.mutant_files = CommonTools.getTextArea(this.mutantFilesID);
    }

    /**
     * Clear the list of mutant read files.
     */
    @FXML
    private void clearMutantReadAction() {
        this.mutantFilesID.clear();
    }

    /**
     * Select wild type's read files.
     */
    @FXML
    private void selectWildTypeReadAction() {
        CommonTools.selectFastqFiles(this.wildTypeFilesID, this.mainPaneID);
        this.wildType_files = CommonTools.getTextArea(this.wildTypeFilesID);
    }

    /**
     * Clear the list of wild type read files.
     */
    @FXML
    private void clearWildTypeReadAction() {
        this.wildTypeFilesID.clear();
    }

    /**
     * Select the output directory
     */
    @FXML
    private void selectOutDirectoryCountMerAction() {
        CommonTools.selectDirectory(this.outDirectoryCountMerID, this.mainPaneID);
    }

    /**
     * Count the mer
     */
    @FXML
    private void executeCountMerAction() {
        this.messageID.setDisable(false);
        this.messageID.setText(ProgramVersionImpl.PROGRAM + " " + ProgramVersionImpl.VERSION + " start...");

        if (this.checkCountMer()) {     // check input files
            this.executeCountMerID.setDisable(true);
            this.stopCountMerID.setDisable(false);

            // Asynchronous thread start
            Callable<Void> task = () -> {
                // Execution options
                Options options = new Options(
                        this.vectorFileID.getText(),
                        this.mutant_files,
                        this.wildType_files,
                        this.spinnerKmerID.getValue(),
                        this.spinnerFdrID.getValue(),
                        this.spinnerBasesID.getValue(),
                        this.outPrefixCountMerID.getText(),
                        new File(this.outDirectoryCountMerID.getText()),
                        this.checkOutsideKmerID.isSelected(),
                        this.spinnerThreadsID.getValue(),
                        this.messageID,
                        this.progressID);

                // Bitwise operation
                BitwiseOperation bitwiseOperation = new BitwiseOperation(options);

                // Create statistics files
                StatisticsFile statisticsFile = new StatisticsFile(options);

                //  Match analysis of k-mer
                this.kmerMatch = new KmerMatch(options, bitwiseOperation, statisticsFile);

                if (!kmerMatch.read_vectorFile(this.mainPaneID) || !this.kmerMatchControl()) {
                    this.executeCountMerID.setDisable(false);
                    this.stopCountMerID.setDisable(true);
                    return null;
                }

                TextField statisticsTextField = this.kmerMatch.create_statisticsFile(this.mainPaneID);

                if (Objects.nonNull(statisticsTextField)) {
                    TextField outsideTextField;
                    if (options.getCheckOutsideKmer()) {
                        // Extension analysis of k-mer
                        this.kmerExtension = new KmerExtension(options, bitwiseOperation, statisticsFile);
                        if (this.kmerExtension.set_merCounter()) {
                            if (!this.kmerExtensionControl()) {
                                this.executeCountMerID.setDisable(false);
                                this.stopCountMerID.setDisable(false);
                                return null;
                            }
                            outsideTextField = this.kmerExtension.create_outsideFile(this.mainPaneID);
                        } else {
                            outsideTextField = null;
                        }
                    } else {
                        outsideTextField = null;
                    }

                    Platform.runLater(() -> {
                        this.executeCountMerID.setDisable(false);
                        this.stopCountMerID.setDisable(true);
                        options.getPbar().setProgress(1.0);

                        this.drawGraphController.setStatisticsFile(statisticsTextField.getText());
                        if (Objects.nonNull(outsideTextField)) {
                            this.drawGraphController.setOutsideFile(outsideTextField.getText());
                        }

                        this.mainTabPane.getSelectionModel().select(this.drawGraphTab);
                        this.drawGraphController.redrawAction();
                    });
                } else {
                    return null;
                }

                Platform.runLater(() -> options.setRuntimeMessage("Completed."));
                return null;
            };
            ExecutorService task_service = Executors.newSingleThreadExecutor();
            task_service.submit(task);
            task_service.shutdown();
        }
    }

    /**
     * Stop counting mer.
     */
    @FXML
    private void stopCountMerAction() {
        boolean flag = this.service != null && (!this.service.isShutdown());

        if (flag) { // running
            // Open dialogue
            String executionMessage = "Stop count mer execution.";
            ExecutionDialogueController execution = new ExecutionDialogueController(executionMessage, "blue", this.mainPaneID);
            if (execution.isOk()) {
                this.cancelFutures();
                // Clear progress bar
                this.progressID.setProgress(0.0);
                this.executeCountMerID.setDisable(false);
                this.stopCountMerID.setDisable(true);
                this.messageID.setText("Interrupted.");
            }
        } else {
            String warningMessage = "Count mer is not running.";
            // Open error message dialogue
            new ErrorDialogueController(warningMessage, "green", this.mainPaneID);
            this.executeCountMerID.setDisable(false);
            this.stopCountMerID.setDisable(true);
            this.messageID.setText("Interrupted.");
        }
    }

    //========================================================================//
    // Private function
    //========================================================================//

    /**
     * Check settings before counting Mer.
     *
     * @return true:no problems, false:problems occurrence
     */
    private boolean checkCountMer() {
        if (!CommonTools.checkInputText(this.vectorFileID)) {
            String errorMessage = "Vector sequence file was not specified.";
            new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
            return false;
        }

        if (!CommonTools.checkInputText(this.mutantFilesID)) {
            String errorMessage = "Mutant read files were not specified.";
            new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
            return false;
        }

        if (!CommonTools.checkInputText(this.wildTypeFilesID)) {
            String errorMessage = "Wild type read files were not specified.";
            new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
            return false;
        }

        if (!CommonTools.checkInputText(this.outPrefixCountMerID)) {
            String errorMessage = "Output prefix was not specified.";
            new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
            return false;
        }

        if (!CommonTools.checkInputText(this.outDirectoryCountMerID)) {
            String errorMessage = "Output directory was not specified.";
            new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
            return false;
        }
        return true;
    }

    /**
     * Control k-mer match analysis.
     *
     * @return true:successful, false:failure
     */
    private boolean kmerMatchControl() {
        this.futureList.clear();
        this.threads = this.spinnerThreadsID.getValue();
        final int npool = min(this.threads, this.mutant_files.size() + this.wildType_files.size());

        // Execute count mer
        this.service = Executors.newFixedThreadPool(npool);
        for (int i = 0; i < this.mutant_files.size(); i++) {
            // start asynchronous threads that create annotation reports
            Future<?> future = this.service.submit(new CountMerCallable(this.kmerMatch, true, i, this.mainPaneID));
            this.futureList.add(future);
        }

        for (int i = 0; i < this.wildType_files.size(); i++) {
            // start asynchronous threads that create annotation reports
            Future<?> future = this.service.submit(new CountMerCallable(this.kmerMatch, false, i, this.mainPaneID));
            this.futureList.add(future);
        }

        // Future for synchronization
        for (Future<?> future : this.futureList) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                String errorMessage = "Asynchronous threads of annotation report processing has stopped.";
                new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
                return false;
            }
        }
        this.service.shutdown();
        return true;
    }

    /**
     * Control k-mer extension analysis.
     *
     * @return true:successful, false:failure
     */
    private boolean kmerExtensionControl() {
        this.futureList.clear();
        final int npool = min(this.threads, this.mutant_files.size() + this.wildType_files.size());

        // execute count mer
        this.service = Executors.newFixedThreadPool(npool);
        for (int i = 0; i < this.mutant_files.size(); i++) {
            // start asynchronous threads that create annotation reports
            Future<?> future = this.service.submit(new CountMerCallable(this.kmerExtension, true, i, this.mainPaneID));
            this.futureList.add(future);
        }

        for (int i = 0; i < this.wildType_files.size(); i++) {
            // start asynchronous threads that create annotation reports
            Future<?> future = this.service.submit(new CountMerCallable(this.kmerExtension, false, i, this.mainPaneID));
            this.futureList.add(future);
        }

        // Future for synchronization
        for (Future<?> future : this.futureList) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                String errorMessage = "Asynchronous threads of annotation report processing has stopped.";
                new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
                return false;
            }
        }
        this.service.shutdown();
        return true;
    }

    /**
     * Terminate the thread.
     */
    private void cancelFutures() {
        for (Future<?> future : this.futureList) {
            if (future != null) {
                future.cancel(true);
            }
        }
        this.futureList.clear();
        this.service.shutdownNow();
    }
}
