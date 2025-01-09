/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Match analysis of the k-mer class.
 *
 * @author NARO
 */
public class KmerMatch extends KmerBaseAbstract {
    //========================================================================//
    // Local class
    //========================================================================//
    /**
     * Match analysis of the fastq files class
     */
    private final FastqMatch fastqMatch;

    //========================================================================//
    // Local parameters
    //========================================================================//
    private File outMutantMerFile;
    private File outWildTypeMerFile;
    private final Map<Integer, Pair<String, String>> vectorPosPair = new HashMap<>();
    private final Map<String, Integer> mutantMerCounter = new HashMap<>();
    private Map<String, Integer> wildTypeMerCounter = new HashMap<>();
    private final List<Map<String, Integer>> merCounter = new ArrayList<>();
    private long mutantMerTotalCounter;
    private long wildTypeMerTotalCounter;
    private long[] merTotalCounter;

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Match analysis of the k-mer class constructor.
     *
     * @param options          Execution options class
     * @param bitwiseOperation Bitwise operation class
     * @param statisticsFile   Statistics file class
     */
    public KmerMatch(Options options, BitwiseOperation bitwiseOperation, StatisticsFile statisticsFile) {
        super.options = options;
        super.bitwiseOperation = bitwiseOperation;
        super.statisticsFile = statisticsFile;

        // FASTQ match
        this.fastqMatch = new FastqMatch(super.options, super.bitwiseOperation);
    }

    /**
     * Read the vector file.
     *
     * @param node base screen for dialog
     * @return true:read success, false:read failure
     */
    public boolean read_vectorFile(Node node) {
        File file = new File(super.options.getVector_file());
        super.options.setRuntimeMessage("Now vector sequence process (" + file.getName() + ").");

        this.initialize_countMer();

        // Read vector file.
        VectorSequence vectorSequence = new VectorSequence(super.options, super.bitwiseOperation);
        if (!vectorSequence.read_vectorFile(this.mutantMerCounter, this.vectorPosPair)) {
            String errorMessage = "Vector's fasta file (" + super.options.getVector_file() + ") read error.";
            CommonTools.runTimeErrorMessage(errorMessage, "red", node);
            return false;
        }

        this.statisticsFile.setVectorArray(vectorSequence.getVectorArray());
        this.statisticsFile.setVectorPosPair(this.vectorPosPair);

        this.wildTypeMerCounter = new HashMap<>(this.mutantMerCounter);

        for (int i = 0; i < super.options.number_of_samples(); i++) {
            this.merCounter.add(new HashMap<>(this.mutantMerCounter));
        }

        this.merTotalCounter = new long[super.options.number_of_samples()];

        // Progress bar start
        CommonTools.kmerMatchProgress(super.options, 0);
        return true;
    }

    /**
     * Read the mutant files.
     *
     * @param ifile file index
     * @param node  base screen for dialog
     */
    @Override
    public void read_mutantFiles(int ifile, Node node) {
        File file = new File(super.options.getMutant_files().get(ifile));
        super.options.setRuntimeMessage("Now k-mer search process [Mutant] "
                + (ifile + 1) + "/" + super.options.getMutant_files().size() + " (" + file.getName() + ").");

        // mutant
        boolean ret = this.fastqMatch.read_fastqFile(super.options.getMutant_files().get(ifile),
                this.merCounter, this.merTotalCounter, ifile);

        if (ret) {
            // update progress bar
            super.ifastq = CommonTools.increment(super.ifastq);
            CommonTools.kmerMatchProgress(super.options, super.ifastq);
        } else {
            String message = "Fastq file of mutant (" + super.options.getMutant_files().get(ifile) + ") could not be read.";
            CommonTools.runTimeErrorMessage(message, "red", node);
        }
    }

    /**
     * Read the wild type files.
     *
     * @param ifile file index
     * @param node  base screen for dialog
     */
    @Override
    public void read_wildTypeFiles(int ifile, Node node) {
        File file = new File(super.options.getWildType_files().get(ifile));
        super.options.setRuntimeMessage("Now k-mer search process [Wild type] "
                + (ifile + 1) + "/" + super.options.getWildType_files().size() + " (" + file.getName() + ").");

        int nMutant = super.options.getMutant_files().size();

        // wild type
        boolean ret = this.fastqMatch.read_fastqFile(super.options.getWildType_files().get(ifile),
                this.merCounter, this.merTotalCounter, ifile + nMutant);

        if (ret) {
            // update progress bar
            super.ifastq = CommonTools.increment(super.ifastq);
            CommonTools.kmerMatchProgress(super.options, super.ifastq);
        } else {
            String message = "Fastq file of wild type (" + super.options.getWildType_files().get(ifile) + ") could not be read.";
            CommonTools.runTimeErrorMessage(message, "red", node);
        }
    }

    /**
     * Create the statistics file.
     *
     * @param node base screen for dialog
     * @return statistics file.
     */
    public TextField create_statisticsFile(Node node) {
        this.sum_merCounter();
        this.control_freqFile(node);
        this.statisticsFile.set_merCount(this.mutantMerTotalCounter, this.wildTypeMerTotalCounter);
        return this.statisticsFile.create_statisticsFile(node);
    }

    //============================================================================//
    // Private function
    //============================================================================//

    /**
     * Initialize the arrays that count the mer.
     */
    private void initialize_countMer() {
        String prefix = this.options.getOutDirectory().getPath()
                + File.separator + this.options.getOut_prefix();

        // Mutant merFreq file
        this.outMutantMerFile = new File(prefix + ".mutant.merFreq.txt");

        // Wild type merFreq file
        this.outWildTypeMerFile = new File(prefix + ".wildtype.merFreq.txt");

        // clear
        this.vectorPosPair.clear();
        this.mutantMerCounter.clear();
        this.wildTypeMerCounter.clear();
        this.merCounter.clear();
        this.mutantMerTotalCounter = 0L;
        this.wildTypeMerTotalCounter = 0L;
        this.merTotalCounter = null;

        // Progress bar
        super.ifastq = 0;
    }

    /**
     * Output the results of the mer counts to files.
     */
    private void control_freqFile(Node node) {
        // Write mutant file.
        if (!super.options.getMutant_files().isEmpty()) {
            List<Integer> mutantPosFreq = this.set_posFreq(this.mutantMerCounter);
            this.statisticsFile.setMutantPosFreq(mutantPosFreq);
            this.create_merFreqFile(this.outMutantMerFile, this.mutantMerCounter, node);
        }

        // Write wild type file.
        if (!super.options.getWildType_files().isEmpty()) {
            List<Integer> wildTypePosFreq = this.set_posFreq(this.wildTypeMerCounter);
            this.statisticsFile.setWildTypePosFreq(wildTypePosFreq);
            this.create_merFreqFile(this.outWildTypeMerFile, this.wildTypeMerCounter, node);
        }
    }

    /**
     * Set the position frequency information.
     *
     * @param merCounter mer and its counts
     */
    private List<Integer> set_posFreq(Map<String, Integer> merCounter) {
        List<Integer> posBothFreq = new ArrayList<>();
        for (Map.Entry<Integer, Pair<String, String>> entry : this.vectorPosPair.entrySet()) {
            int merPlusCounter = merCounter.get(entry.getValue().getKey());
            int merMinusCounter = merCounter.get(entry.getValue().getValue());
            int merBothCounter = merPlusCounter + merMinusCounter;
            posBothFreq.add(merBothCounter);
        }
        return posBothFreq;
    }

    /**
     * Output the merFreq.txt file.
     *
     * @param outMer     merFreq.txt file
     * @param merCounter mer and its counts
     */
    private void create_merFreqFile(File outMer, Map<String, Integer> merCounter, Node node) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(outMer.getPath()))) {
            PrintWriter pw = new PrintWriter(bw);
            List<String> sortedKeys = new ArrayList<>(merCounter.keySet());
            Collections.sort(sortedKeys);
            for (String key : sortedKeys) {
                pw.println(key + "\t" + merCounter.get(key));
            }
            pw.close();
        } catch (IOException e) {
            String errorMessage = "Could not read file (" + outMer.getPath() + ").";
            new ErrorDialogueController(errorMessage, "red", node);
        }
    }

    /**
     * Sum the number of the mer counter.
     */
    private void sum_merCounter() {
        int nMutant = super.options.getMutant_files().size();
        int nWildType = super.options.getWildType_files().size();

        for (int i = 0; i < nMutant; i++) {
            for (String mer : this.mutantMerCounter.keySet()) {
                this.mutantMerCounter.put(mer, this.merCounter.get(i).get(mer) + this.mutantMerCounter.get(mer));
            }
            this.mutantMerTotalCounter += this.merTotalCounter[i];
        }

        for (int i = 0; i < nWildType; i++) {
            for (String mer : wildTypeMerCounter.keySet()) {
                this.wildTypeMerCounter.put(mer, this.merCounter.get(nMutant + i).get(mer) + this.wildTypeMerCounter.get(mer));
            }
            this.wildTypeMerTotalCounter += this.merTotalCounter[nMutant + i];
        }
    }
}
