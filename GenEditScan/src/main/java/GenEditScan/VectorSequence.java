/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Vector sequence processing class.
 *
 * @author NARO
 */
public class VectorSequence {
    //========================================================================//
    // Local class
    //========================================================================//
    /**
     * Execution options class
     */
    private final Options options;

    /**
     * Bitwise operation class
     */
    private final BitwiseOperation bitwiseOperation;


    //========================================================================//
    // Local parameter
    //========================================================================//
    /**
     * vector sequence
     */
    private String vectorArray;

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Vector sequence processing class constructor.
     *
     * @param options          Execution options class
     * @param bitwiseOperation Bitwise operation class
     */
    public VectorSequence(Options options, BitwiseOperation bitwiseOperation) {
        this.options = options;
        this.bitwiseOperation = bitwiseOperation;
    }

    /**
     * Read a fasta file.
     *
     * @param merCounter mer and its counts
     * @param posPair    vector position mer pairs
     * @return true:read success, false:read failure
     */
    public boolean read_vectorFile(Map<String, Integer> merCounter, Map<Integer, Pair<String, String>> posPair) {
        File file = new File(this.options.getVector_file());
        List<String> readList = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
            String str;
            while ((str = br.readLine()) != null) {
                readList.add(str);
            }
        } catch (IOException e) {
            return false;
        }

        StringBuilder sequence = new StringBuilder();
        for (String str : readList) {
            if (str.startsWith(">")) {
                this.set_merCounter(sequence.toString(), merCounter, posPair);
                sequence = new StringBuilder();
            } else {
                sequence.append(str);
            }
        }
        this.vectorArray = this.set_merCounter(sequence.toString(), merCounter, posPair);
        if (this.vectorArray != null) {
            this.create_chunk(merCounter);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get vector array.
     *
     * @return vector array
     */
    public String getVectorArray() {
        return this.vectorArray;
    }

    //========================================================================//
    // Private function
    //========================================================================//

    /**
     * Set k-mer in hash table.
     *
     * @param sequence   vector sequence
     * @param merCounter mer and its counts
     * @param posPair    vector position mer pairs
     */
    private String set_merCounter(String sequence, Map<String, Integer> merCounter,
                                  Map<Integer, Pair<String, String>> posPair) {
        int kmer = this.options.getKmer();
        int vector_length = sequence.length();

        if (vector_length > 0) {
            String buff = sequence +
                    sequence.substring(0, kmer - 1);

            // Convert to upper case
            String circulation = buff.toUpperCase();

            for (int i = 0; i < vector_length; i++) {
                String mer = circulation.substring(i, kmer + i);
                // Complementary sequence of kmer
                String revMer = CommonTools.complementaryMer(mer);
                merCounter.put(mer, 0);
                merCounter.put(revMer, 0);
                posPair.put(i, new Pair<>(mer, revMer));
            }
            return circulation;
        } else {
            return null;
        }
    }

    /**
     * Create chunk array.
     *
     * @param merCounter mer and its counts
     */
    private void create_chunk(Map<String, Integer> merCounter) {
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
}
