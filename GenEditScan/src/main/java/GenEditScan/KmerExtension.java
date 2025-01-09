/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Extension analysis of the k-mer class.
 *
 * @author NARO
 */
public class KmerExtension extends KmerBaseAbstract {
    //========================================================================//
    // Local data
    //========================================================================//
    /**
     * Extension analysis of the fastq files class.
     */
    protected FastqExtension fastqExtension;

    //========================================================================//
    // Local parameters
    //========================================================================//
    private final Map<String, List<Pair<String, String>>> mutantMerCounter = new HashMap<>();
    private final Map<String, List<Pair<String, String>>> wildTypeMerCounter = new HashMap<>();
    private final List<Map<String, List<Pair<String, String>>>> merCounter = new ArrayList<>();
    private long mutantMerTotalCounter;
    private long wildTypeMerTotalCounter;
    private long[] merTotalCounter;

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Extension analysis of the k-mer class constructor.
     *
     * @param options          Execution options class
     * @param bitwiseOperation Bitwise operation class
     * @param statisticsFile   Statistics file class
     */
    public KmerExtension(Options options, BitwiseOperation bitwiseOperation, StatisticsFile statisticsFile) {
        super.options = options;
        super.bitwiseOperation = bitwiseOperation;
        super.statisticsFile = statisticsFile;

        // FASTQ extension
        this.fastqExtension = new FastqExtension(super.options, super.bitwiseOperation);
    }

    /**
     * Set mer counters for analysis.
     *
     * @return true:existence, false:nothing
     */
    public boolean set_merCounter() {
        super.options.setRuntimeMessage("Now preparing the both sides of the k-mer process.");

        this.initialize_counterMer();
        final String vectorArray = this.statisticsFile.getVectorArray();
        final Map<Integer, Double> fdr = this.statisticsFile.getFdr();

        for (int i = 0; i < super.options.number_of_samples(); i++) {
            this.merCounter.add(new HashMap<>());
        }

        for (Map.Entry<Integer, Double> entry : fdr.entrySet()) {
            if (entry.getValue() <= this.options.getThreshold_fdr()) {
                String mer = vectorArray.substring(entry.getKey(), entry.getKey() + super.options.getKmer());
                // Complementary sequence of this.kmer
                String revMer = CommonTools.complementaryMer(mer);
                this.mutantMerCounter.put(mer, new ArrayList<>());
                this.mutantMerCounter.put(revMer, new ArrayList<>());
                this.wildTypeMerCounter.put(mer, new ArrayList<>());
                this.wildTypeMerCounter.put(revMer, new ArrayList<>());
                for (int i = 0; i < super.options.number_of_samples(); i++) {
                    this.merCounter.get(i).put(mer, new ArrayList<>());
                    this.merCounter.get(i).put(revMer, new ArrayList<>());
                }
            }
        }

        this.create_chunk(this.mutantMerCounter);
        this.merTotalCounter = new long[super.options.number_of_samples()];

        // Progress bar start
        CommonTools.kmerExtensionProgress(super.options, 0);
        return !this.mutantMerCounter.isEmpty();
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
        super.options.setRuntimeMessage("Now both sides of k-mer process [Mutant] "
                + (ifile + 1) + "/" + super.options.getMutant_files().size() + " (" + file.getName() + ").");

        // mutant
        boolean ret = this.fastqExtension.read_fastqFile(super.options.getMutant_files().get(ifile),
                this.merCounter, this.merTotalCounter, ifile);

        if (ret) {
            // update progress bar
            super.ifastq = CommonTools.increment(super.ifastq);
            CommonTools.kmerExtensionProgress(super.options, super.ifastq);
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
        super.options.setRuntimeMessage("Now both sides of k-mer process [Wild type] "
                + (ifile + 1) + "/" + super.options.getWildType_files().size() + " (" + file.getName() + ").");

        int nMutant = super.options.getMutant_files().size();

        // wild type
        boolean ret = this.fastqExtension.read_fastqFile(this.options.getWildType_files().get(ifile),
                this.merCounter, this.merTotalCounter, ifile + nMutant);

        if (ret) {
            // update progress bar
            super.ifastq = CommonTools.increment(super.ifastq);
            CommonTools.kmerExtensionProgress(super.options, super.ifastq);
        } else {
            String message = "Fastq file of wild type (" + this.options.getWildType_files().get(ifile) + ") could not be read.";
            CommonTools.runTimeErrorMessage(message, "red", node);
        }
    }

    /**
     * Create the outside file.
     *
     * @param node base screen for dialog
     * @return outside file.
     */
    public TextField create_outsideFile(Node node) {
        if (super.options.getThreshold_fdr() >= 0.0) {
            this.sum_merCounter();
            this.statisticsFile.set_merCount(this.mutantMerTotalCounter, this.wildTypeMerTotalCounter);
            return this.statisticsFile.create_outsideFile(this.mutantMerCounter, this.wildTypeMerCounter, node);
        }
        return null;
    }

    //============================================================================//
    // Private function
    //============================================================================//

    /**
     * Initialize the arrays that count the mer.
     */
    private void initialize_counterMer() {
        // clear
        this.mutantMerCounter.clear();
        this.wildTypeMerCounter.clear();
        this.merCounter.clear();

        // Progress bar
        super.ifastq = 0;
    }

    /**
     * Create the chunk array.
     *
     * @param merCounter mer and its counts
     */
    private void create_chunk(Map<String, List<Pair<String, String>>> merCounter) {
        byte[] dna2bit = this.bitwiseOperation.getDna2bit();
        byte[] chunk = this.bitwiseOperation.getChunk();

        for (String str : merCounter.keySet()) {
            int dnabit = dna2bit[str.charAt(0)];
            for (int i = 1; i < this.options.getChunk_length(); i++) {
                dnabit = (dnabit << 2) + dna2bit[str.charAt(i)];
            }
            if (dnabit != this.options.getMax_chunk_array()) {
                chunk[dnabit] = 1;
            }
        }
    }

    /**
     * Sum the number of mer counter.
     */
    private void sum_merCounter() {
        int nMutant = super.options.getMutant_files().size();
        int nWildType = super.options.getWildType_files().size();

        for (int i = 0; i < nMutant; i++) {
            for (Map.Entry<String, List<Pair<String, String>>> entry : this.mutantMerCounter.entrySet()) {
                this.mutantMerCounter.get(entry.getKey()).addAll(this.merCounter.get(i).get(entry.getKey()));
            }
            this.mutantMerTotalCounter += this.merTotalCounter[i];
        }

        for (int i = nMutant; i < nMutant + nWildType; i++) {
            for (Map.Entry<String, List<Pair<String, String>>> entry : this.wildTypeMerCounter.entrySet()) {
                this.wildTypeMerCounter.get(entry.getKey()).addAll(this.merCounter.get(i).get(entry.getKey()));
            }
            this.wildTypeMerTotalCounter += this.merTotalCounter[i];
        }
    }
}
