/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Common tools class.
 *
 * @author NARO
 */
public class CommonTools {
    //========================================================================//
    // Private constructor
    //========================================================================//

    /**
     * Common tools class constructor.
     * Prohibit the creation of an instance.
     */
    private CommonTools() {
    }

    //========================================================================//
    // Public data
    //========================================================================//
    /**
     * Search directory.
     */
    public static File searchDir = new File(System.getProperty("user.home"));

    /**
     * Newline code.
     */
    public static final String BR = System.lineSeparator();

    /**
     * Return code for Split function.
     */
    public static final String BR2 = "(\\r\\n|\\r|\\n)";

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Select a Fasta file.
     *
     * @param target text field for a Fasta file
     * @param node   base screen for dialog
     */
    public static void selectFastaFile(TextField target, Node node) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select vector sequence");
        fc.setInitialDirectory(searchDir);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("FASTA file", "*.fa", "*.fasta", "*.fas"));

        File file = fc.showOpenDialog(node.getScene().getWindow());
        if (file != null) {
            target.setText(file.getPath());
            searchDir = file.getParentFile();
        }
    }

    /**
     * Select Fastq files.
     *
     * @param target text area for Fastq files
     * @param node   base screen for dialog
     */
    public static void selectFastqFiles(TextArea target, Node node) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(searchDir);
        fc.setTitle("Select FASTQ files");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("FASTQ files", "*.fastq.gz", "*.fastq"));

        List<File> files = fc.showOpenMultipleDialog(node.getScene().getWindow());
        if (files != null) {
            for (File file : files) {
                CommonTools.setTextArea(target, file.getPath());
            }
            searchDir = files.getFirst().getParentFile();
        }
    }

    /**
     * Stores one file in the text area.
     *
     * @param target text area for files
     * @param name   file name
     */
    public static void setTextArea(TextArea target, String name) {
        if (checkInputText(target)) {
            target.setText(target.getText() + BR + name);
        } else {
            target.setText(name);
        }
    }

    /**
     * Stores the file list in the text area.
     *
     * @param target text area for files
     * @param names  file list
     */
    public static void setTextArea(TextArea target, List<String> names) {
        target.clear();
        for (String name : names) {
            setTextArea(target, name);
        }
    }

    /**
     * Get a list of files in the text area.
     *
     * @param target text area for files
     * @return file list
     */
    public static List<String> getTextArea(TextArea target) {
        return checkInputText(target) ? Arrays.asList(target.getText().split(CommonTools.BR2))
                : new ArrayList<>();
    }

    /**
     * Select a directory.
     *
     * @param target text area for a directory
     * @param node   base screen for dialog
     */
    public static void selectDirectory(TextField target, Node node) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Select output directory");
        dc.setInitialDirectory(searchDir);
        File dir = dc.showDialog(node.getScene().getWindow());

        if (dir != null) {
            target.setText(dir.getPath());
            searchDir = dir.getParentFile();
        }
    }

    /**
     * Import user configuration file.
     *
     * @param node base screen for dialog
     * @return user configuration file (.conf)
     */
    public static File selectConfigurationFile(Node node) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import configuration file");
        fc.setInitialDirectory(searchDir);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration file", "*.conf"));

        File file = fc.showOpenDialog(node.getScene().getWindow());
        if (file != null) {
            searchDir = file.getParentFile();
        }
        return file;
    }

    /**
     * Export user configuration file.
     *
     * @param node base screen for dialog
     * @return user configuration file (.conf)
     */
    public static File saveConfigurationFile(Node node) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export configuration file");
        fc.setInitialDirectory(searchDir);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration file", "*.conf"));
        File file = fc.showSaveDialog(node.getScene().getWindow());
        if (file != null) {
            searchDir = file.getParentFile();
        }
        return file;
    }

    /**
     * Select PDF or PNG format to save the graph to a file.
     *
     * @param node base screen for dialog
     * @return PDF or PNG file name
     */
    public static File saveImage(Node node) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save image file");
        fc.setInitialDirectory(searchDir);
        String timeStamp = new SimpleDateFormat("MMddHHmm", Locale.ENGLISH).format(new Date());
        fc.setInitialFileName("kmer_" + timeStamp);
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF file", "*.pdf"),
                new FileChooser.ExtensionFilter("PNG file", "*.png"));

        File file = fc.showSaveDialog(node.getScene().getWindow());
        if (file != null) {
            searchDir = file.getParentFile();
        }
        return file;
    }

    /**
     * Select the statistics.txt file.
     *
     * @param target text field for the statistics.txt file
     * @param node   base screen for dialog
     */
    public static void selectStatistics(TextField target, Node node) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select statistics file");
        fc.setInitialDirectory(searchDir);
        //fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("statistics file", "*.statistics.txt"));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("statistics file", "*.statistics.txt"));

        File file = fc.showOpenDialog(node.getScene().getWindow());
        if (file != null) {
            target.setText(file.getPath());
            searchDir = file.getParentFile();
        }
    }

    /**
     * select the outside.txt file.
     *
     * @param target text field for the outside.txt file.
     * @param node   base screen for dialog
     */
    public static void selectOutside(TextField target, Node node) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select outside file");
        fc.setInitialDirectory(searchDir);
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("outside file", "*.outside.txt"));

        File file = fc.showOpenDialog(node.getScene().getWindow());
        if (file != null) {
            target.setText(file.getPath());
            searchDir = file.getParentFile();
        }
    }

    /**
     * Obtain the complementary sequence of k-mer.
     *
     * @param mer k-mer sequence
     * @return complementary sequence of k-mer
     */
    public static String complementaryMer(String mer) {
        StringBuilder revMer = new StringBuilder();
        for (int j = mer.length() - 1; j >= 0; j--) {
            if (mer.charAt(j) == 'A') {
                revMer.append("T");
            } else if (mer.charAt(j) == 'T') {
                revMer.append("A");
            } else if (mer.charAt(j) == 'C') {
                revMer.append("G");
            } else if (mer.charAt(j) == 'G') {
                revMer.append("C");
            }
        }
        return revMer.toString();
    }

    /**
     * Verify that the file is stored in the text field.
     *
     * @param inputText text field to be checked
     * @return true:file is stored, false:file is not stored
     */
    public static boolean checkInputText(TextField inputText) {
        return inputText != null && !inputText.getText().isEmpty();
    }

    /**
     * Verify that the file is stored in the text area.
     *
     * @param inputText text field to be checked
     * @return true:file is stored, false:file is not stored
     */
    public static boolean checkInputText(TextArea inputText) {
        return inputText != null && !inputText.getText().isEmpty();
    }

    /**
     * Increments the counter exclusively.
     *
     * @param counter counter
     * @return incremented counter
     */
    public static synchronized int increment(int counter) {
        return ++counter;
    }

    /**
     * Progress bar for kmer match process.
     *
     * @param options execution options class
     * @param ifastq  i-th fastq file
     */
    public static void kmerMatchProgress(Options options, int ifastq) {
        double barStep = options.getCheckOutsideKmer()
                ? 1.0 / (2 * options.number_of_samples() + 3)
                : 1.0 / (options.number_of_samples() + 2);

        double pstat = barStep * (ifastq + 1);
        options.getPbar().setProgress(pstat);
    }

    /**
     * Progress bar for kmer extension process.
     *
     * @param options execution options class.
     * @param ifastq  i-th fastq file
     */
    public static void kmerExtensionProgress(Options options, int ifastq) {
        double barStep = 1.0 / (2 * options.number_of_samples() + 3);
        double pstat = barStep * (options.number_of_samples() + ifastq + 2);
        options.getPbar().setProgress(pstat);
    }

    /**
     * Displays an error dialog in the submitted thread.
     *
     * @param message error message
     * @param color   red:error, green:warning, blue:message
     * @param node    base screen for dialog
     */
    public static void runTimeErrorMessage(String message, String color, Node node) {
        try {
            CommonTools.FxUtils.updateUI(() ->
                new ErrorDialogueController(message, color, node)
            );
        } catch (Exception ex) {
            Logger.getLogger(CommonTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Asynchronous processing.
     */
    public static class FxUtils {
        public static void updateUI(Runnable action) {
            // JavaFXのアプリケーションスレッドで実行する
            Platform.runLater(action);
        }
    }
}
