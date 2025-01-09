/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Match analysis of the fastq files class.
 *
 * @author NARO
 */
public class FastqMatch {
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
    // Public function
    //========================================================================//

    /**
     * Match analysis of the fastq files class constructor.
     *
     * @param options          Execution options class
     * @param bitwiseOperation Bitwise operation class
     */
    public FastqMatch(final Options options, final BitwiseOperation bitwiseOperation) {
        this.options = options;
        this.bitwiseOperation = bitwiseOperation;
    }

    /**
     * Read fastq files.
     *
     * @param fastqFile     fastq file
     * @param merCounter    mer and its counts
     * @param merTotalCount total counts of mer per file
     * @param ifile         file index
     * @return true:read success, false:read failure
     */
    public boolean read_fastqFile(String fastqFile, List<Map<String, Integer>> merCounter,
                                  long[] merTotalCount, int ifile) {
        merTotalCount[ifile] = 0L;

        if (fastqFile.endsWith(".gz")) {
            try (GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(fastqFile))) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(gzip))) {
                    return this.count_match(fastqFile, br, merCounter, merTotalCount, ifile);
                }
            } catch (IOException e) {
                return false;
            }
        } else {
            File file = new File(fastqFile);
            try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
                return this.count_match(fastqFile, br, merCounter, merTotalCount, ifile);
            } catch (IOException e) {
                return false;
            }
        }
    }

    //========================================================================//
    // Private function
    //========================================================================//

    /**
     * Count the number of mer matches.
     *
     * @param fastqFile     fastq file
     * @param br            BufferedReader of the fasta file
     * @param merCounter    mer and its counts
     * @param merTotalCount total counts of mer per file
     * @param ifile         file index
     * @return true:process success, false:process failure
     */
    private boolean count_match(String fastqFile, BufferedReader br,
                                List<Map<String, Integer>> merCounter,
                                long[] merTotalCount, int ifile) {
        final int kmer = this.options.getKmer();
        final int mask = this.options.getMax_chunk_array();
        final int chunk_length = this.options.getChunk_length();
        byte[] dna2bit = this.bitwiseOperation.getDna2bit();
        byte[] chunk = this.bitwiseOperation.getChunk();
        long readCounter = 0L;
        String str;
        String[] aLine = new String[4];
        int nLine = 0;
        fastqFile = new File(fastqFile).getName();

        try {
            while ((str = br.readLine()) != null) {
                aLine[nLine++] = str;
                if (nLine == 4) {
                    nLine = 0;
                    if (!aLine[0].startsWith("@") || !aLine[2].startsWith("+")) {
                        return false;
                    }
                    if (aLine[1].length() >= kmer) {
                        if (Thread.currentThread().isInterrupted()) {   // for Stop process
                            throw new RuntimeException();
                        }

                        if (++readCounter % 5000000L == 0L) {
                            this.options.setRuntimeMessage(fastqFile + ": parsing "
                                    + readCounter + " reads (k-mer match).");
                        }

                        int dnabit = dna2bit[aLine[1].charAt(0)];
                        for (int i = 1; i < chunk_length - 1; i++) {
                            dnabit = (dnabit << 2) + dna2bit[aLine[1].charAt(i)];
                        }

                        for (int i = 0; i <= aLine[1].length() - kmer; i++) {
                            dnabit = (dnabit << 2) + dna2bit[aLine[1].charAt(chunk_length - 1 + i)];
                            dnabit = dnabit & mask;
                            if (chunk[dnabit] == 1) {   // signed language only
                                String mer = aLine[1].substring(i, i + kmer);
                                if (merCounter.get(ifile).containsKey(mer)) {
                                    merCounter.get(ifile).put(mer, merCounter.get(ifile).get(mer) + 1);
                                }
                            }
                            merTotalCount[ifile]++;
                        }
                    }
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
