/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Execution options class.
 *
 * @author NARO
 */
public class Options {
    //========================================================================//
    // Constructor's parameters
    //========================================================================//
    /**
     * Vector file
     */
    private final String vector_file;

    /**
     * Mutant files
     */
    private final List<String> mutant_files;

    /**
     * Wild type files
     */
    private final List<String> wildType_files;

    /**
     * K-mer
     */
    private final int kmer;

    /**
     * Threshold by FDR
     */
    private final double threshold_fdr;

    /**
     * Number of bases on each side
     */
    private final int bases_on_each_side;

    /**
     * Output prefix
     */
    private final String out_prefix;

    /**
     * Output directory
     */
    private final File outDirectory;

    /**
     * Calculation of outside the k-mer sequences option
     */
    private final boolean checkOutsideKmer;

    /**
     * Number of threads
     */
    private final int threads;

    /**
     * message label
     */
    private final Label message;

    /**
     * progress bar
     */
    private final ProgressBar pbar;

    /**
     * start time
     */
    private final Instant startTime;

    //========================================================================//
    // Local parameters
    //========================================================================//
    /**
     * int(32 bit) / (2 bit/base) - 1 (because of signed value) = 15 bases
     */
    private final int MAX_CHUNKLENGTH = 15;

    /**
     * chunk length
     */
    private final int chunk_length;

    /**
     * array length required for specified chunk length
     */
    private final int max_chunk_array;

    //========================================================================//
    // Public function
    //========================================================================//
    /**
     * Execution options class constructor.
     */
    public Options(String vector_file,
                   List<String> mutant_files,
                   List<String> wildType_files,
                   int kmer,
                   double threshold_fdr,
                   int bases_on_each_side,
                   String out_prefix,
                   File outDirectory,
                   boolean checkOutsideKmer,
                   int threads,
                   Label message,
                   ProgressBar pbar) {
        this.vector_file = vector_file;
        this.mutant_files = mutant_files;
        this.wildType_files = wildType_files;
        this.kmer = kmer;
        this.threshold_fdr = threshold_fdr;
        this.bases_on_each_side = bases_on_each_side;
        this.out_prefix = out_prefix;
        this.outDirectory = outDirectory;
        this.checkOutsideKmer = checkOutsideKmer;
        this.threads = threads;
        this.message = message;
        this.pbar = pbar;
        this.startTime = Instant.now();
        this.chunk_length = Math.min(this.kmer, this.MAX_CHUNKLENGTH);
        this.max_chunk_array = (int) (Math.pow(2, this.chunk_length * 2) - 1);
    }

    /**
     * Number of mutant and wild type files.
     *
     * @return mutant + wildType files
     */
    public int number_of_samples() {
        return this.mutant_files.size() + this.wildType_files.size();
    }

    /**
     * Set text message.
     *
     * @param message message
     */
    public void setMessage(String message) {
        Instant currentTime = Instant.now();
        Duration elapsedTime = Duration.between(this.startTime, currentTime);
        String elapsedTimeStr = String.format("%d seconds elapsed, ", elapsedTime.getSeconds());
        this.message.setText(elapsedTimeStr + message);
    }

    /**
     * Set text message (background process).
     *
     * @param message message
     */
    public void setRuntimeMessage(String message) {
        try {
            Instant currentTime = Instant.now();
            Duration elapsedTime = Duration.between(this.startTime, currentTime);
            String elapsedTimeStr = String.format("%d seconds elapsed, ", elapsedTime.getSeconds());
            CommonTools.FxUtils.updateUI(() ->
                    this.message.setText(elapsedTimeStr + message)
            );
        } catch (Exception ex) {
            Logger.getLogger(CommonTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Getter

    public String getVector_file() {
        return this.vector_file;
    }

    public List<String> getMutant_files() {
        return this.mutant_files;
    }

    public List<String> getWildType_files() {
        return this.wildType_files;
    }

    public int getKmer() {
        return this.kmer;
    }

    public double getThreshold_fdr() {
        return this.threshold_fdr;
    }

    public int getBases_on_each_side() {
        return this.bases_on_each_side;
    }

    public String getOut_prefix() {
        return this.out_prefix;
    }

    public File getOutDirectory() {
        return this.outDirectory;
    }

    public boolean getCheckOutsideKmer() {
        return this.checkOutsideKmer;
    }

    public int getThreads() {
        return this.threads;
    }

    public ProgressBar getPbar() {
        return this.pbar;
    }

    public int getChunk_length() {
        return this.chunk_length;
    }

    public int getMax_chunk_array() {
        return this.max_chunk_array;
    }
}
