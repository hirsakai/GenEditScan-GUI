/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.scene.Node;

/**
 * K-mer analysis abstract class.
 */
abstract public class KmerBaseAbstract {
    //========================================================================//
    // Local class
    //========================================================================//
    /**
     * Execution options class
     */
    protected Options options;

    /**
     * Bitwise operation class
     */
    protected BitwiseOperation bitwiseOperation;

    /**
     * Statistics file class
     */
    protected StatisticsFile statisticsFile;

    //========================================================================//
    // Local parameters
    //========================================================================//
    /**
     * FASTQ file counter
     */
    protected int ifastq = 0;

    //========================================================================//
    // Public function
    //========================================================================//
    /**
     * K-mer analysis abstract class constructor.
     */
    public KmerBaseAbstract() {
    }

    //========================================================================//
    // Abstract function
    //========================================================================//

    /**
     * Abstract function that read mutant files.
     *
     * @param ifile File index
     * @param node Base screen for dialog
     */
    abstract void read_mutantFiles(int ifile, Node node);

    /**
     * Abstract function that read mutant files.
     *
     * @param ifile File index
     * @param node Base screen for dialog
     */
    abstract void read_wildTypeFiles(int ifile, Node node);
}
