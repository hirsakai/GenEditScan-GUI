/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.scene.Node;

import java.util.concurrent.Callable;

/**
 * Count mer callable class (for parallel process).
 */
public class CountMerCallable implements Callable<Void> {
    //========================================================================//
    // Local class
    //========================================================================//
    /**
     * K-mer analysis abstract class
     */
    private final KmerBaseAbstract kmerBaseAbstract;

    /**
     * main window
     */
    private final Node mainPane;

    //========================================================================//
    // Local data
    //========================================================================//
    /**
     * sample order
     */
    private final int isample;

    /**
     * mutant file identifier
     */
    private final boolean isMutant;

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Count mer callable class constructor.
     *
     * @param kmerBaseAbstract Abstract class of k-mer analysis
     * @param mutant           true : mutant, false : wild type
     * @param loop             sample order
     * @param node             base screen for dialog
     */
    public CountMerCallable(KmerBaseAbstract kmerBaseAbstract, boolean mutant, int loop, Node node) {
        this.kmerBaseAbstract = kmerBaseAbstract;
        this.isMutant = mutant;
        this.isample = loop;
        this.mainPane = node;
    }

    /**
     * Read mutant or wild type files.
     */
    @Override
    public Void call() {
        if (this.isMutant) {
            this.kmerBaseAbstract.read_mutantFiles(this.isample, this.mainPane);
        } else {
            this.kmerBaseAbstract.read_wildTypeFiles(this.isample, this.mainPane);
        }
        return null;
    }
}
