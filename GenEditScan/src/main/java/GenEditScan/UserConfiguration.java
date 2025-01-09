/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * User configuration class.
 * Save and reproduce the user's calculation conditions.
 *
 * @author NARO
 */
public class UserConfiguration {
    //========================================================================//
    // Local parameter
    //========================================================================//
    // Count mer
    String vector_file;             // Vector sequence
    List<String> mutant_files;      // (Mutant) FASTQ files
    List<String> wildType_files;    // (Wild type) FASTQ files
    int kmer;                       // K-mer
    double threshold_fdr;           // Threshold by FDR
    int bases_on_each_side;         // Number of bases on each side
    String out_prefix;              // Output prefix count mer
    String outDirectory;            // Output directory count mer
    boolean checkOutsideKmer;       // Outside the k-mer sequences
    int threads;                    // Maximum number of threads

    // Draw graph
    String yupperAxisTitle;         // Y-axis (upper) title
    boolean yupperAxisAuto;         // Y-axis (upper) auto
    boolean yupperAxisSpecify;      // Y-axis (upper) specify
    int yupperAxisFrom;             // Y-axis (upper) from
    int yupperAxisTo;               // Y-axis (upper) to
    String mutantColor;             // Mutant color
    String wildTypeColor;           // Wild type color
    String ylowerAxisTitle;         // Y-axis (lower) title
    boolean ylowerAxisAuto;         // Y-axis (lower) auto
    boolean ylowerAxisSpecify;      // Y-axis (lower) specify
    int ylowerAxisFrom;             // Y-axis (lower) from
    int ylowerAxisTo;               // Y-axis (lower) to
    String significantColor;        // Significant color
    String notSignificantColor;     // Not significant color
    double thresholdValue;          // Threshold value
    String thresholdColor;          // Threshold color
    String xAxisTitle;              // X-axis title
    boolean xAxisAuto;              // X-axis auto
    boolean xAxisSpecify;           // X-axis specify
    int xAxisFrom;                  // X-axis from
    int xAxisTo;                    // X-axis to
    boolean rotateYticks;           // Rotate yticks
    String statisticsFile;          // Statistics file
    String outsideFile;             // Outside file

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Constructor of User configuration class.
     */
    public UserConfiguration() {
        this.mutant_files = new ArrayList<>();
        this.wildType_files = new ArrayList<>();
    }

    /**
     * Reads a user configuration file.
     *
     * @param file user configuration file (.conf)
     * @param node base screen for dialog
     * @return 0:input error, 1:normal termination, 2:interruption of reading
     */
    public int readConfigurationFile(File file, Node node) {
        this.clearConfiguration();
        List<String> readList = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
            String str;
            while ((str = br.readLine()) != null) {
                readList.add(str);
            }

            for (String s : readList) {
                String[] data = s.split("\t");
                switch (data[0]) {
                    // Program version
                    case "PROGRAM_VERSION":
                        if (!data[1].equals(ProgramVersionImpl.VERSION)) {
                            String warningMessage = "Version of configuration file does not match.";
                            ExecutionDialogueController execution = new ExecutionDialogueController(warningMessage, "green", node);
                            if (!execution.isOk()) {
                                return 2;
                            }
                        }
                        break;
                    // Search directory
                    case "SEARCH_DIRECTORY":
                        CommonTools.searchDir = new File(data[1]);
                        break;

                    // Count mer
                    case "VECTOR_FILE":
                        this.vector_file = data[1];
                        break;
                    case "MUTANT_FILES":
                        this.mutant_files.add(data[1]);
                        break;
                    case "WILDTYPE_FILES":
                        this.wildType_files.add(data[1]);
                        break;
                    case "KMER":
                        this.kmer = Integer.parseInt(data[1]);
                        break;
                    case "THRESHOLD_FDR":
                        this.threshold_fdr = Double.parseDouble(data[1]);
                        break;
                    case "BASES_ON_EACH_SIDE":
                        this.bases_on_each_side = Integer.parseInt(data[1]);
                        break;
                    case "OUTPUT_PREFIX":
                        this.out_prefix = data[1];
                        break;
                    case "OUTPUT_DIRECTORY":
                        this.outDirectory = data[1];
                        break;
                    case "OUTSIDE_THE_KMER_SEQUENCES":
                        this.checkOutsideKmer = data[1].equals("true");
                        break;
                    case "THREADS":
                        this.threads = Integer.parseInt(data[1]);
                        break;

                    // Draw graph
                    case "YUPPER_AXIS_TITLE":
                        this.yupperAxisTitle = data[1];
                        break;
                    case "YUPPER_AXIS_AUTO":
                        this.yupperAxisAuto = Boolean.parseBoolean(data[1]);
                        break;
                    case "YUPPER_AXIS_SPECIFY":
                        this.yupperAxisSpecify = Boolean.parseBoolean(data[1]);
                        break;
                    case "YUPPER_AXIS_FROM":
                        this.yupperAxisFrom = Integer.parseInt(data[1]);
                        break;
                    case "YUPPER_AXIS_TO":
                        this.yupperAxisTo = Integer.parseInt(data[1]);
                        break;
                    case "MUTANT_COLOR":
                        this.mutantColor = data[1];
                        break;
                    case "WILDTYPE_COLOR":
                        this.wildTypeColor = data[1];
                        break;
                    case "YLOWER_AXIS_TITLE":
                        this.ylowerAxisTitle = data[1];
                        break;
                    case "YLOWER_AXIS_AUTO":
                        this.ylowerAxisAuto = Boolean.parseBoolean(data[1]);
                        break;
                    case "YLOWER_AXIS_SPECIFY":
                        this.ylowerAxisSpecify = Boolean.parseBoolean(data[1]);
                        break;
                    case "YLOWER_AXIS_FROM":
                        this.ylowerAxisFrom = Integer.parseInt(data[1]);
                        break;
                    case "YLOWER_AXIS_TO":
                        this.ylowerAxisTo = Integer.parseInt(data[1]);
                        break;
                    case "SIGNIFICANT_COLOR":
                        this.significantColor = data[1];
                        break;
                    case "NOT_SIGNIFICANT_COLOR":
                        this.notSignificantColor = data[1];
                        break;
                    case "THRESHOLD_VALUE":
                        this.thresholdValue = Double.parseDouble(data[1]);
                        break;
                    case "THRESHOLD_COLOR":
                        this.thresholdColor = data[1];
                        break;
                    case "X_AXIS_TITLE":
                        this.xAxisTitle = data[1];
                        break;
                    case "X_AXIS_AUTO":
                        this.xAxisAuto = Boolean.parseBoolean(data[1]);
                        break;
                    case "X_AXIS_SPECIFY":
                        this.xAxisSpecify = Boolean.parseBoolean(data[1]);
                        break;
                    case "X_AXIS_FROM":
                        this.xAxisFrom = Integer.parseInt(data[1]);
                        break;
                    case "X_AXIS_TO":
                        this.xAxisTo = Integer.parseInt(data[1]);
                        break;
                    case "ROTATE_YTICKS":
                        this.rotateYticks = Boolean.parseBoolean(data[1]);
                        break;
                    case "STATISTICS_FILE":
                        this.statisticsFile = data[1];
                        break;
                    case "OUTSIDE_FILE":
                        this.outsideFile = data[1];
                        break;
                    default:
                        String warningMessage = "Tag of configuration file (" + data[0] + ") is illegal.";
                        ExecutionDialogueController execution = new ExecutionDialogueController(warningMessage, "green", node);
                        if (!execution.isOk()) {
                            return 2;
                        }
                        break;
                }
            }
        } catch (IOException e) {
            return 0;
        }
        return 1;
    }

    /**
     * Save in the user configuration file.
     *
     * @param file user configuration file (.conf).
     * @return true:write success, false:write failure
     */
    public boolean writeConfigurationFile(File file) {
        try (BufferedWriter bw = Files.newBufferedWriter(file.toPath())) {
            PrintWriter pw = new PrintWriter(bw);
            // Program version
            pw.println("PROGRAM_VERSION\t" + ProgramVersionImpl.VERSION);
            // Search directory
            pw.println("SEARCH_DIRECTORY\t" + CommonTools.searchDir);

            // Count mer
            if (this.vector_file != null && !this.vector_file.isEmpty()) {
                pw.println("VECTOR_FILE\t" + this.vector_file);
            }
            for (String s : this.mutant_files) {
                pw.println("MUTANT_FILES\t" + s);
            }
            for (String s : this.wildType_files) {
                pw.println("WILDTYPE_FILES\t" + s);
            }
            pw.println("KMER\t" + this.kmer);
            if (this.threshold_fdr >= 0.0) {
                pw.println("THRESHOLD_FDR\t" + this.threshold_fdr);
            }
            if (this.bases_on_each_side > 0) {
                pw.println("BASES_ON_EACH_SIDE\t" + this.bases_on_each_side);
            }
            if (this.out_prefix != null && !this.out_prefix.isEmpty()) {
                pw.println("OUTPUT_PREFIX\t" + this.out_prefix);
            }
            if (this.outDirectory != null && !this.outDirectory.isEmpty()) {
                pw.println("OUTPUT_DIRECTORY\t" + this.outDirectory);
            }
            if (this.checkOutsideKmer) {
                pw.println("OUTSIDE_THE_KMER_SEQUENCES\ttrue");
            } else {
                pw.println("OUTSIDE_THE_KMER_SEQUENCES\tfalse");
            }
            if (this.threads > 0) {
                pw.println("THREADS\t" + this.threads);
            }

            // Draw graph
            if (this.yupperAxisTitle != null && !this.yupperAxisTitle.isEmpty()) {
                pw.println("YUPPER_AXIS_TITLE\t" + this.yupperAxisTitle);
            }
            pw.println("YUPPER_AXIS_AUTO\t" + this.yupperAxisAuto);
            pw.println("YUPPER_AXIS_SPECIFY\t" + this.yupperAxisSpecify);
            pw.println("YUPPER_AXIS_FROM\t" + this.yupperAxisFrom);
            if (this.yupperAxisTo > 0) {
                pw.println("YUPPER_AXIS_TO\t" + this.yupperAxisTo);
            }
            if (this.mutantColor != null && !this.mutantColor.isEmpty()) {
                pw.println("MUTANT_COLOR\t" + this.mutantColor);
            }
            if (this.wildTypeColor != null && !this.wildTypeColor.isEmpty()) {
                pw.println("WILDTYPE_COLOR\t" + this.wildTypeColor);
            }
            if (this.ylowerAxisTitle != null && !this.ylowerAxisTitle.isEmpty()) {
                pw.println("YLOWER_AXIS_TITLE\t" + this.ylowerAxisTitle);
            }
            pw.println("YLOWER_AXIS_AUTO\t" + this.ylowerAxisAuto);
            pw.println("YLOWER_AXIS_SPECIFY\t" + this.ylowerAxisSpecify);
            pw.println("YLOWER_AXIS_FROM\t" + this.ylowerAxisFrom);
            if (this.ylowerAxisTo > 0) {
                pw.println("YLOWER_AXIS_TO\t" + this.ylowerAxisTo);
            }
            if (this.significantColor != null && !this.significantColor.isEmpty()) {
                pw.println("SIGNIFICANT_COLOR\t" + this.significantColor);
            }
            if (this.notSignificantColor != null && !this.notSignificantColor.isEmpty()) {
                pw.println("NOT_SIGNIFICANT_COLOR\t" + this.notSignificantColor);
            }
            if (this.thresholdValue > 0.0) {
                pw.println("THRESHOLD_VALUE\t" + this.thresholdValue);
            }
            if (this.thresholdColor != null && !this.thresholdColor.isEmpty()) {
                pw.println("THRESHOLD_COLOR\t" + this.thresholdColor);
            }
            if (this.xAxisTitle != null && !this.xAxisTitle.isEmpty()) {
                pw.println("X_AXIS_TITLE\t" + this.xAxisTitle);
            }
            pw.println("X_AXIS_AUTO\t" + this.xAxisAuto);
            pw.println("X_AXIS_SPECIFY\t" + this.xAxisSpecify);
            pw.println("X_AXIS_FROM\t" + this.xAxisFrom);
            if (this.xAxisTo > 0) {
                pw.println("X_AXIS_TO\t" + this.xAxisTo);
            }
            pw.println("ROTATE_YTICKS\t" + this.rotateYticks);
            if (this.statisticsFile != null && !this.statisticsFile.isEmpty()) {
                pw.println("STATISTICS_FILE\t" + this.statisticsFile);
                if (this.outsideFile != null && !this.outsideFile.isEmpty()) {
                    pw.println("OUTSIDE_FILE\t" + this.outsideFile);
                }
            }
            pw.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    //========================================================================//
    // Setter / Getter
    //========================================================================//
    //========== Count mer
    // Vector file
    public void setVector_file(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.vector_file = tf.getText();
        }
    }

    public String getVector_file() {
        return this.vector_file;
    }

    // Mutant files
    public void setMutant_files(TextArea ta) {
        this.mutant_files = CommonTools.getTextArea(ta);
    }

    public List<String> getMutant_files() {
        return this.mutant_files;
    }

    // Wild type files
    public void setWildType_files(TextArea ta) {
        this.wildType_files = CommonTools.getTextArea(ta);
    }

    public List<String> getWildType_files() {
        return this.wildType_files;
    }

    // K-mer
    public void setKmer(Spinner<Integer> sp) {
        this.kmer = this.spinnerValue(sp);
    }

    public int getKmer() {
        return this.kmer;
    }

    // Threshold by FDR
    public void setThresholdFdr(Spinner<Double> sp) {
        this.threshold_fdr = this.spinnerValueDouble(sp);
    }

    public double getThresholdFdr() {
        return this.threshold_fdr;
    }

    // Number of bases on each side
    public void setNumberOfBasesOnEachSide(Spinner<Integer> sp) {
        this.bases_on_each_side = this.spinnerValue(sp);
    }

    public int getNumberOfBasesOnEachSide() {
        return this.bases_on_each_side;
    }

    // Output prefix count mer
    public void setOut_prefix(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.out_prefix = tf.getText();
        }
    }

    public String getOut_prefix() {
        return this.out_prefix;
    }

    // Output directory count mer
    public void setOutDirectory(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.outDirectory = tf.getText();
        }
    }

    public String getOutDirectory() {
        return this.outDirectory;
    }

    // Outside the k-mer sequences
    public void setOutsideKmerSequences(CheckBox cb) {
        this.checkOutsideKmer = cb.isSelected();
    }

    public boolean getOutsideKmerSequences() {
        return this.checkOutsideKmer;
    }

    // Maximum number of threads
    public void setThreads(Spinner<Integer> sp) {
        this.threads = this.spinnerValue(sp);
    }

    public int getThreads() {
        return this.threads;
    }

    //========== Draw graph
    // Y-axis (upper) title
    public void setYupperAxisTitle(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.yupperAxisTitle = tf.getText();
        }
    }

    public String getYupperAxisTitle() {
        return this.yupperAxisTitle;
    }

    // Y-axis (upper) auto
    public void setYupperAxisAuto(RadioButton rb) {
        this.yupperAxisAuto = rb.isSelected();
    }

    public boolean getYupperAxisAuto() {
        return this.yupperAxisAuto;
    }

    // Y-axis (upper) specify
    public void setYupperAxisSpecify(RadioButton rb) {
        this.yupperAxisSpecify = rb.isSelected();
    }

    public boolean getYupperAxisSpecify() {
        return this.yupperAxisSpecify;
    }

    // Y-axis (upper) from
    public void setYupperAxisFrom(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.yupperAxisFrom = Integer.parseInt(tf.getText());
        }
    }

    public int getYupperAxisFrom() {
        return this.yupperAxisFrom;
    }

    // Y-axis (upper) to
    public void setYupperAxisTo(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.yupperAxisTo = Integer.parseInt(tf.getText());
        }
    }

    public int getYupperAxisTo() {
        return this.yupperAxisTo;
    }

    // Mutant color
    public void setMutantColor(ColorPicker cp) {
        this.mutantColor = cp.getValue().toString();
    }

    public String getMutantColor() {
        return this.mutantColor;
    }

    // Wild type color
    public void setWildTypeColor(ColorPicker cp) {
        this.wildTypeColor = cp.getValue().toString();
    }

    public String getWildTypeColor() {
        return this.wildTypeColor;
    }

    // Y-axis (lower) title
    public void setYlowerAxisTitle(ComboBox<String> tf) {
        this.ylowerAxisTitle = tf.getValue();
    }

    public String getYlowerAxisTitle() {
        return this.ylowerAxisTitle;
    }

    // Y-axis (lower) auto
    public void setYlowerAxisAuto(RadioButton rb) {
        this.ylowerAxisAuto = rb.isSelected();
    }

    public boolean getYlowerAxisAuto() {
        return this.ylowerAxisAuto;
    }

    // Y-axis (lower) specify
    public void setYlowerAxisSpecify(RadioButton rb) {
        this.ylowerAxisSpecify = rb.isSelected();
    }

    public boolean getYlowerAxisSpecify() {
        return this.ylowerAxisSpecify;
    }

    // Y-axis (lower) from
    public void setYlowerAxisFrom(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.ylowerAxisFrom = Integer.parseInt(tf.getText());
        }
    }

    public int getYlowerAxisFrom() {
        return this.ylowerAxisFrom;
    }

    // Y-axis (lower) to
    public void setYlowerAxisTo(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.ylowerAxisTo = Integer.parseInt(tf.getText());
        }
    }

    public int getYlowerAxisTo() {
        return this.ylowerAxisTo;
    }

    // Significant color
    public void setSignificantColor(ColorPicker cp) {
        this.significantColor = cp.getValue().toString();
    }

    public String getSignificantColor() {
        return this.significantColor;
    }

    // Not significant color
    public void setNotSignificantColor(ColorPicker cp) {
        this.notSignificantColor = cp.getValue().toString();
    }

    public String getNotSignificantColor() {
        return this.notSignificantColor;
    }

    // Threshold value
    public void setThresholdValue(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.thresholdValue = Double.parseDouble(tf.getText());
        }
    }

    public double getThresholdValue() {
        return this.thresholdValue;
    }

    // Threshold color
    public void setThresholdColor(ColorPicker cp) {
        this.thresholdColor = cp.getValue().toString();
    }

    public String getThresholdColor() {
        return this.thresholdColor;
    }

    // X-axis title
    public void setXaxisTitle(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.xAxisTitle = tf.getText();
        }
    }

    public String getXaxisTitle() {
        return this.xAxisTitle;
    }

    // X-axis auto
    public void setXaxisAuto(RadioButton rb) {
        this.xAxisAuto = rb.isSelected();
    }

    public boolean getXaxisAuto() {
        return this.xAxisAuto;
    }

    // X-axis specify
    public void setXaxisSpecify(RadioButton rb) {
        this.xAxisSpecify = rb.isSelected();
    }

    public boolean getXaxisSpecify() {
        return this.xAxisSpecify;
    }

    // X-axis from
    public void setXaxisFrom(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.xAxisFrom = Integer.parseInt(tf.getText());
        }
    }

    public int getXaxisFrom() {
        return this.xAxisFrom;
    }

    // X-axis to
    public void setXaxisTo(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.xAxisTo = Integer.parseInt(tf.getText());
        }
    }

    public int getXaxisTo() {
        return this.xAxisTo;
    }

    // Rotate yticks
    public void setRotateYticks(CheckBox cb) {
        this.rotateYticks = cb.isSelected();
    }

    public boolean getRotateYticks() {
        return this.rotateYticks;
    }

    // Statistics file
    public void setStatisticsFile(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.statisticsFile = tf.getText();
        }
    }

    public String getStatisticsFile() {
        return this.statisticsFile;
    }

    // Outside file
    public void setOutsideFile(TextField tf) {
        if (!tf.getText().isEmpty()) {
            this.outsideFile = tf.getText();
        }
    }

    public String getOutsideFile() {
        return this.outsideFile;
    }

    //========================================================================//
    // Private function
    //========================================================================//

    /**
     * Clear the contents of the calculation conditions.
     */
    private void clearConfiguration() {
        // Count mer
        this.vector_file = null;
        this.mutant_files.clear();
        this.wildType_files.clear();
        this.kmer = 0;
        this.threshold_fdr = 0.0;
        this.bases_on_each_side = 0;
        this.out_prefix = null;
        this.outDirectory = null;
        this.checkOutsideKmer = false;
        this.threads = 0;

        // Draw graph
        this.yupperAxisTitle = null;
        this.yupperAxisAuto = true;
        this.yupperAxisFrom = 0;
        this.yupperAxisTo = 0;
        this.mutantColor = null;
        this.wildTypeColor = null;
        this.ylowerAxisTitle = null;
        this.ylowerAxisAuto = true;
        this.ylowerAxisFrom = 0;
        this.ylowerAxisTo = 0;
        this.significantColor = null;
        this.notSignificantColor = null;
        this.thresholdValue = 0.0;
        this.thresholdColor = null;
        this.xAxisTitle = null;
        this.xAxisAuto = true;
        this.xAxisFrom = 0;
        this.xAxisTo = 0;
        this.rotateYticks = false;
        this.statisticsFile = null;
        this.outsideFile = null;
    }

    /**
     * Get the value considering the range of the spinner values (int).
     *
     * @param sp spinner
     * @return spinner value
     */
    private int spinnerValue(Spinner<Integer> sp) {
        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                (SpinnerValueFactory.IntegerSpinnerValueFactory) sp.getValueFactory();
        return min(max(sp.getValue(), factory.getMin()), factory.getMax());
    }

    /**
     * Get the value considering the range of the spinner values (double).
     *
     * @param sp spinner
     * @return spinner value
     */
    private double spinnerValueDouble(Spinner<Double> sp) {
        SpinnerValueFactory.DoubleSpinnerValueFactory factory =
                (SpinnerValueFactory.DoubleSpinnerValueFactory) sp.getValueFactory();
        return min(max(sp.getValue(), factory.getMin()), factory.getMax());
    }
}
