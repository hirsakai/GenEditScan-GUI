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
 * Create statistics file class.
 *
 * @author NARO
 */
public class StatisticsFile {
    //========================================================================//
    // Local class
    //========================================================================//
    /**
     * Execution options class
     */
    Options options;

    /**
     * Run the G-test class
     */
    Gtest gtest;

    /**
     * Outside data class
     */
    OutsideData outsideData = new OutsideData();

    //========================================================================//
    // Local parameters
    //========================================================================//
    /**
     * bases of the circular vector genome
     */
    String vectorArray;

    /**
     * position and k-mer complementary pair on vector
     */
    Map<Integer, Pair<String, String>> vectorPosPair;

    /**
     * position frequency of mutant
     */
    List<Integer> mutantPosFreq = new ArrayList<>();

    /**
     * position frequency of wild type
     */
    List<Integer> wildTypePosFreq = new ArrayList<>();

    /**
     * number of outside data per k-mer
     */
    Map<Integer, Integer> table_size = new HashMap<>();

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Create statistics file class constructor.
     *
     * @param options Execution options
     */
    public StatisticsFile(Options options) {
        this.options = options;
        this.gtest = new Gtest(this.options);
    }

    /**
     * Set mer total count.
     *
     * @param mutant_mer_count  count of the mutant total mer
     * @param wildType_mer_cont count of the wild type total mer
     */
    public void set_merCount(final long mutant_mer_count, final long wildType_mer_cont) {
        this.gtest.set_mer_total(mutant_mer_count, wildType_mer_cont);
    }

    /**
     * Create the statistics.txt file.
     *
     * @param node base screen for dialog
     * @return statistics.txt file
     */
    public TextField create_statisticsFile(Node node) {
        String statisticsFile = this.options.getOutDirectory() + File.separator + this.options.getOut_prefix() + ".statistics.txt";

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(statisticsFile))) {
            PrintWriter pw = new PrintWriter(bw);
            pw.println("#K-mer\t" + this.options.getKmer());
            pw.println("#Pos\tSeq\tMutant\tWildType\tGval\tPval\tFDR\tBonferroni");

            // Calculate G-value for k-mer match analysis.
            this.gtest.sequence_match(this.mutantPosFreq, this.wildTypePosFreq);

            //========== Output ==========//
            for (int i = 0; i < this.mutantPosFreq.size(); i++) {
                pw.print((i + 1) + "\t" + this.vectorArray.charAt(i));
                pw.print("\t" + this.mutantPosFreq.get(i));
                pw.print("\t" + this.wildTypePosFreq.get(i));
                pw.print("\t" + this.gtest.getGval().get(i).floatValue());
                pw.print("\t" + this.gtest.getPval().get(i).floatValue());
                pw.print("\t" + this.gtest.getFdr().get(i).floatValue());
                pw.println("\t" + this.gtest.getBon().get(i).floatValue());
            }
            pw.close();
            var statisticsTextField = new TextField();
            statisticsTextField.setText(statisticsFile);
            return statisticsTextField;
        } catch (IOException e) {
            String message = "Could not open (" + statisticsFile + ").";
            CommonTools.runTimeErrorMessage(message, "red", node);
            return null;
        }
    }

    /**
     * Create the outside.txt file.
     *
     * @param mutantMerPair   mutant mer pairs at each end
     * @param wildTypeMerPair wild type mer pairs at each end
     * @param node            base screen for dialog
     * @return outside.txt file
     */
    public TextField create_outsideFile(Map<String, List<Pair<String, String>>> mutantMerPair,
                                        Map<String, List<Pair<String, String>>> wildTypeMerPair, Node node) {
        String outsideFile = this.options.getOutDirectory() + File.separator + this.options.getOut_prefix() + ".outside.txt";

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(outsideFile))) {
            PrintWriter pw = new PrintWriter(bw);

            int number_of_extensions = this.create_outsideData(mutantMerPair, wildTypeMerPair);

            // Calculate FDR using the Benjamini-Hochberg method.
            Map<Integer, Map<Integer, Double>> fdr_extension = this.gtest.extended_fdr(this.outsideData.pval);

            pw.println("#K-mer\t" + this.options.getKmer() + "\tFDR\t" + this.options.getThreshold_fdr()
                    + "\tBases\t" + this.options.getBases_on_each_side());

            for (int i = 0; i < this.vectorArray.length() - this.options.getKmer(); i++) {
                String kmer = this.vectorArray.substring(i, i + this.options.getKmer());
                if (this.gtest.getFdr().get(i) <= this.options.getThreshold_fdr()) {
                    pw.print((i + 1) + "\t" + this.table_size.get(i));
                    pw.print("\t" + kmer);
                    pw.print("\t" + this.mutantPosFreq.get(i));
                    pw.print("\t" + this.wildTypePosFreq.get(i));
                    pw.print("\t" + this.gtest.getGval().get(i).floatValue());
                    pw.print("\t" + this.gtest.getPval().get(i).floatValue());
                    pw.print("\t" + this.gtest.getFdr().get(i).floatValue());
                    pw.println("\t" + this.gtest.getBon().get(i).floatValue());

                    for (int j = 0; j < this.outsideData.left_chain.get(i).size(); j++) {
                        pw.print(this.outsideData.left_chain.get(i).get(j) + "\t");
                        pw.print(this.outsideData.right_chain.get(i).get(j) + "\t");
                        pw.print(this.outsideData.mutant_count.get(i).get(j) + "\t");
                        pw.print(this.outsideData.wildType_count.get(i).get(j) + "\t");
                        pw.print(this.outsideData.left_chain.get(i).get(j));
                        pw.print(kmer);
                        pw.print(this.outsideData.right_chain.get(i).get(j) + "\t");
                        pw.print(this.outsideData.gval.get(i).get(j).floatValue() + "\t");
                        pw.print(this.outsideData.pval.get(i).get(j).floatValue() + "\t");
                        pw.print(fdr_extension.get(i).get(j).floatValue() + "\t");
                        pw.println((float) Math.min(this.outsideData.pval.get(i).get(j) * number_of_extensions, 1.0));
                    }
                }
            }

            pw.close();
            var outsideTextField = new TextField();
            outsideTextField.setText(outsideFile);
            return outsideTextField;
        } catch (IOException e) {
            String message = "Could not open (" + outsideFile + ").";
            CommonTools.runTimeErrorMessage(message, "red", node);
            return null;
        }
    }

    // Setter / Getter

    void setVectorArray(String vectorArray) {
        this.vectorArray = vectorArray;
    }

    String getVectorArray() {
        return this.vectorArray;
    }

    void setVectorPosPair(Map<Integer, Pair<String, String>> vectorPosPair) {
        this.vectorPosPair = vectorPosPair;
    }

    void setMutantPosFreq(List<Integer> mutantPosFreq) {
        this.mutantPosFreq = mutantPosFreq;
    }

    void setWildTypePosFreq(List<Integer> wildTypePosFreq) {
        this.wildTypePosFreq = wildTypePosFreq;
    }

    Map<Integer, Double> getFdr() {
        return this.gtest.getFdr();
    }

    //============================================================================//
    // Private function
    //============================================================================//

    /**
     * Create outside data.
     *
     * @param mutantMerPair   mutant mer pairs at each end
     * @param wildTypeMerPair wild type mer pairs at each end
     * @return Outside data
     */
    private int create_outsideData(Map<String, List<Pair<String, String>>> mutantMerPair,
                                   Map<String, List<Pair<String, String>>> wildTypeMerPair) {
        Map<Pair<Integer, Integer>, Double> gval_stock = new HashMap<>();
        Map<Pair<Integer, Integer>, Double> pval_stock = new HashMap<>();
        int number_of_extensions = 0;

        for (int i = 0; i < this.vectorArray.length() - this.options.getKmer(); i++) {
            if (this.gtest.getFdr().get(i) <= this.options.getThreshold_fdr()) {
                final String mer_plus = this.vectorPosPair.get(i).getKey();
                final String mer_minus = this.vectorPosPair.get(i).getValue();

                Map<Pair<String, String>, Integer> mutant_side_pair_count = new HashMap<>();
                Map<Pair<String, String>, Integer> wildType_side_pair_count = new HashMap<>();

                if (mutantMerPair.containsKey(mer_plus)) {
                    for (Pair<String, String> itr : mutantMerPair.get(mer_plus)) {
                        int counter = mutant_side_pair_count.getOrDefault(itr, 0) + 1;
                        mutant_side_pair_count.put(itr, counter);
                    }
                }

                if (wildTypeMerPair.containsKey(mer_plus)) {
                    for (Pair<String, String> itr : wildTypeMerPair.get(mer_plus)) {
                        int counter = wildType_side_pair_count.getOrDefault(itr, 0) + 1;
                        wildType_side_pair_count.put(itr, counter);
                    }
                }

                if (!mer_plus.equals(mer_minus)) {
                    if (mutantMerPair.containsKey(mer_minus)) {
                        for (Pair<String, String> itr : mutantMerPair.get(mer_minus)) {
                            // Obtain the complementary sequence of k-mer.
                            String revMer1 = CommonTools.complementaryMer(itr.getValue());
                            String revMer2 = CommonTools.complementaryMer(itr.getKey());
                            Pair<String, String> pair_rev = new Pair<>(revMer1, revMer2);
                            int counter = mutant_side_pair_count.getOrDefault(pair_rev, 0) + 1;
                            mutant_side_pair_count.put(pair_rev, counter);
                        }
                    }

                    if (wildTypeMerPair.containsKey(mer_minus)) {
                        for (Pair<String, String> itr : wildTypeMerPair.get(mer_minus)) {
                            // Obtain the complementary sequence of k-mer.
                            String revMer1 = CommonTools.complementaryMer(itr.getValue());
                            String revMer2 = CommonTools.complementaryMer(itr.getKey());
                            Pair<String, String> pair_rev = new Pair<>(revMer1, revMer2);
                            int counter = wildType_side_pair_count.getOrDefault(pair_rev, 0);
                            wildType_side_pair_count.put(pair_rev, counter);
                        }
                    }
                }

                List<Pair<Integer, Pair<String, String>>> v = new ArrayList<>();
                for (Map.Entry<Pair<String, String>, Integer> itr : mutant_side_pair_count.entrySet()) {
                    v.add(new Pair<>(itr.getValue(), itr.getKey()));
                }

                v.sort((p1, p2) -> p2.getKey().compareTo(p1.getKey()));

                this.table_size.put(i, v.size());
                int ic = 0;

                for (Pair<Integer, Pair<String, String>> itr_v : v) {
                    int mutant_count = mutant_side_pair_count.getOrDefault(itr_v.getValue(), 0);
                    int wildType_count = wildType_side_pair_count.getOrDefault(itr_v.getValue(), 0);

                    if (this.outsideData.left_chain.containsKey(i)) {
                        this.outsideData.left_chain.get(i).add(itr_v.getValue().getKey());
                        this.outsideData.right_chain.get(i).add(itr_v.getValue().getValue());

                        this.outsideData.mutant_count.get(i).add(mutant_count);
                        this.outsideData.wildType_count.get(i).add(wildType_count);
                    } else {
                        this.outsideData.left_chain.put(i, new ArrayList<>(List.of(itr_v.getValue().getKey())));
                        this.outsideData.right_chain.put(i, new ArrayList<>(List.of(itr_v.getValue().getValue())));

                        this.outsideData.mutant_count.put(i, new ArrayList<>(List.of(mutant_count)));
                        this.outsideData.wildType_count.put(i, new ArrayList<>(List.of(wildType_count)));
                    }

                    Pair<Integer, Integer> target = new Pair<>(mutant_count, wildType_count);
                    if (gval_stock.containsKey(target)) {
                        if (this.outsideData.gval.containsKey(i)) {
                            this.outsideData.gval.get(i).put(ic, gval_stock.get(target));
                            this.outsideData.pval.get(i).put(ic, pval_stock.get(target));
                        } else {
                            Map<Integer, Double> gvalMap = new HashMap<>();
                            Map<Integer, Double> pvalMap = new HashMap<>();
                            gvalMap.put(ic, gval_stock.get(target));
                            pvalMap.put(ic, pval_stock.get(target));
                            this.outsideData.gval.put(i, gvalMap);
                            this.outsideData.pval.put(i, pvalMap);
                        }
                    } else {
                        // G-test
                        List<Double> g_p = this.gtest.sequence_extension(mutant_count, wildType_count);

                        if (this.outsideData.gval.containsKey(i)) {
                            this.outsideData.gval.get(i).put(ic, g_p.get(0));
                            this.outsideData.pval.get(i).put(ic, g_p.get(1));
                        } else {
                            Map<Integer, Double> gvalMap = new HashMap<>();
                            Map<Integer, Double> pvalMap = new HashMap<>();
                            gvalMap.put(ic, g_p.get(0));
                            pvalMap.put(ic, g_p.get(1));
                            this.outsideData.gval.put(i, gvalMap);
                            this.outsideData.pval.put(i, pvalMap);
                        }

                        gval_stock.put(target, g_p.get(0));
                        pval_stock.put(target, g_p.get(1));
                    }

                    ic++;
                    number_of_extensions++;
                }
            }
        }
        return number_of_extensions;
    }
}
