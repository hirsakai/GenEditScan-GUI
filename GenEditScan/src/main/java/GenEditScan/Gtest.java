/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import cern.jet.stat.Gamma;
import javafx.util.Pair;

import java.util.*;

import static java.lang.Math.log;

/**
 * Run the G-test class.
 *
 * @author NARO
 */
public class Gtest {
    //========================================================================//
    // Local class
    //========================================================================//
    /**
     * Execution options class
     */
    Options options;

    //========================================================================//
    // Local parameters
    //========================================================================//
    /**
     * count of mutant total mer
     */
    private double mutant_mer_total;

    /**
     * count of wild type total mer
     */
    private double wildType_mer_total;

    /**
     * G-value
     */
    Map<Integer, Double> gval = new HashMap<>();

    /**
     * P-value
     */
    Map<Integer, Double> pval = new HashMap<>();

    /**
     * FDR (Benjamini-Hochberg)
     */
    Map<Integer, Double> fdr = new HashMap<>();

    /**
     * Bonferroni
     */
    Map<Integer, Double> bon = new HashMap<>();

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Run the G-test class constructor.
     *
     * @param options Execution options class
     */
    public Gtest(Options options) {
        this.options = options;
    }

    /**
     * Set mer total counter.
     *
     * @param mutant_mer_total   count of mutant total mer
     * @param wildType_mer_total count of wild type total mer
     */
    public void set_mer_total(long mutant_mer_total, long wildType_mer_total) {
        this.mutant_mer_total = (double) mutant_mer_total;
        this.wildType_mer_total = (double) wildType_mer_total;
    }

    /**
     * Calculate G-value for the k-mer match analysis.
     *
     * @param mutantPosFreq   position frequency of mutant
     * @param wildTypePosFreq position frequency of wild type
     */
    public void sequence_match(List<Integer> mutantPosFreq, List<Integer> wildTypePosFreq) {
        // for total
        double mutant_mer_total_log = log(this.mutant_mer_total) * this.mutant_mer_total;
        double wildType_mer_total_log = log(this.wildType_mer_total) * this.wildType_mer_total;
        double mer_total = this.mutant_mer_total + this.wildType_mer_total;
        double mer_q3 = log(mer_total) * mer_total;
        double mer_qcomm = (mer_total / this.mutant_mer_total + mer_total / this.wildType_mer_total - 1.0)
                / (6.0 * mer_total);

        Map<Pair<Integer, Integer>, Double> gval_stock = new HashMap<>();
        Map<Pair<Integer, Integer>, Double> pval_stock = new HashMap<>();
        Map<Pair<Integer, Integer>, Double> bon_stock = new HashMap<>();
        int vector_len = mutantPosFreq.size();

        for (int i = 0; i < vector_len; i++) {
            Pair<Integer, Integer> target = new Pair<>(mutantPosFreq.get(i), wildTypePosFreq.get(i));
            if (gval_stock.containsKey(target)) {
                this.gval.put(i, gval_stock.get(target));
                this.pval.put(i, pval_stock.get(target));
                this.bon.put(i, bon_stock.get(target));
            } else {
                // for total
                double mutant_mer_match = mutantPosFreq.get(i);
                double wildType_mer_match = wildTypePosFreq.get(i);

                if (mutant_mer_match * this.wildType_mer_total >= wildType_mer_match * this.mutant_mer_total) {
                    // G-value
                    double g = this.adjusted_g(mutant_mer_total_log, wildType_mer_total_log,
                            mer_total, mer_q3, mer_qcomm, mutant_mer_match, wildType_mer_match);
                    this.gval.put(i, g);
                    // P-value; To avoid chdtrc underflow error.
                    // When Gval is greater than 170, Pval is less than 1.175494e-38 (float limit).
                    double p = g > 0.0 ? (g < 170.0 ? this.chdtrc(g) : 0) : 1.0;
                    this.pval.put(i, p);
                    // Bonferroni
                    this.bon.put(i, Math.min(p * vector_len, 1.0));
                } else {
                    this.gval.put(i, 0.0);
                    this.pval.put(i, 1.0);
                    this.bon.put(i, 1.0);
                }

                gval_stock.put(target, this.gval.get(i));
                pval_stock.put(target, this.pval.get(i));
                bon_stock.put(target, this.bon.get(i));
            }
        }

        // Calculate FDR using the Benjamini-Hochberg method.
        this.matched_fdr();
    }

    /**
     * Calculate G-value for the k-mer extension analysis.
     *
     * @param mutant_count   count of mutant match mer
     * @param wildType_count count of wild type match mer
     * @return G-value
     */
    public List<Double> sequence_extension(final int mutant_count, final int wildType_count) {
        double mutant_mer_total_log = log(this.mutant_mer_total) * this.mutant_mer_total;
        double wildType_mer_total_log = log(this.wildType_mer_total) * this.wildType_mer_total;
        double mer_total = this.mutant_mer_total + this.wildType_mer_total;
        double mer_q3 = log(mer_total) * mer_total;
        double mer_qcomm = (mer_total / this.mutant_mer_total + mer_total / this.wildType_mer_total - 1.0)
                / (6.0 * mer_total);

        double mutant_mer_match = mutant_count;
        double wildType_mer_match = wildType_count;

        if (mutant_mer_match * this.wildType_mer_total >= wildType_mer_match * this.mutant_mer_total) {
            // G-value
            double g = this.adjusted_g(mutant_mer_total_log, wildType_mer_total_log,
                    mer_total, mer_q3, mer_qcomm, mutant_mer_match, wildType_mer_match);
            // P-value; To avoid chdtrc underflow error.
            // When Gval is greater than 170, Pval is less than 1.175494e-38 (float limit).
            double p = g > 0.0 ? (g < 170.0 ? this.chdtrc(g) : 0) : 1.0;
            return new ArrayList<>(Arrays.asList(g, p));
        } else {
            return new ArrayList<>(Arrays.asList(0.0, 1.0));
        }
    }

    /**
     * Calculate FDR using the Benjamini-Hochberg method.
     *
     * @param pval P-values
     * @return FDR
     */
    Map<Integer, Map<Integer, Double>> extended_fdr(final Map<Integer, Map<Integer, Double>> pval) {
        List<Pair<Double, Integer>> v = new ArrayList<>();
        int ic = 0;
        for (Map.Entry<Integer, Map<Integer, Double>> entry : pval.entrySet()) {
            for (Map.Entry<Integer, Double> entry_p : entry.getValue().entrySet()) {
                v.add(new Pair<>(entry_p.getValue(), ic++));
            }
        }

        v.sort(Comparator.comparing(Pair::getKey));

        // Get values in element order
        Map<Integer, Double> f = new HashMap<>();
        final double vector_len = v.size();
        double vector_pos = 1.0;
        double pval_prev = v.getFirst().getKey();
        double fdr_prev = Math.min(pval_prev * vector_len, 1.0);

        for (Pair<Double, Integer> itr : v) {
            if (itr.getKey() == pval_prev) {
                f.put(itr.getValue(), fdr_prev);
            } else {
                pval_prev = itr.getKey();
                f.put(itr.getValue(), Math.min(itr.getKey() * vector_len / vector_pos, 1.0));
                fdr_prev = f.get(itr.getValue());
            }
            vector_pos += 1.0;
        }

        Map<Integer, Map<Integer, Double>> fdr_map = new HashMap<>();
        ic = 0;
        for (Map.Entry<Integer, Map<Integer, Double>> entry : pval.entrySet()) {
            Map<Integer, Double> map = new HashMap<>();
            for (Map.Entry<Integer, Double> entry_p : entry.getValue().entrySet()) {
                map.put(entry_p.getKey(), f.get(ic++));
            }
            fdr_map.put(entry.getKey(), map);
        }

        return fdr_map;
    }

    // Getter

    Map<Integer, Double> getGval() {
        return this.gval;
    }

    Map<Integer, Double> getPval() {
        return this.pval;
    }

    Map<Integer, Double> getFdr() {
        return this.fdr;
    }

    Map<Integer, Double> getBon() {
        return this.bon;
    }

    //========================================================================//
    // Private function
    //========================================================================//

    /**
     * Calculate Williams's correction of G-value.
     *
     * @param mutant_total_log   the product of log(count of mutant total mer) and (count of mutant total mer)
     * @param wildType_total_log the product of log(count of wild type total mer) and (count of wild type total mer)
     * @param total              count of mutant and wild type total mer
     * @param q3                 the product of log(total) and total
     * @param qcomm              correction parameter
     * @param mutant_match       count of mutant match mer
     * @param wildType_match     count of wild type match mer
     * @return corrected G-value
     */
    private double adjusted_g(final double mutant_total_log, final double wildType_total_log,
                              final double total, final double q3, final double qcomm,
                              final double mutant_match, final double wildType_match) {
        final double mutant_match_log = log(mutant_match) * mutant_match;
        final double wildType_match_log = log(wildType_match) * wildType_match;

        final double mutant_notmatch = this.mutant_mer_total - mutant_match;
        final double mutant_notmatch_log = log(mutant_notmatch) * mutant_notmatch;
        final double wildType_notmatch = this.wildType_mer_total - wildType_match;
        final double wildType_notmatch_log = log(wildType_notmatch) * wildType_notmatch;

        final double match = mutant_match + wildType_match;
        final double match_log = log(match) * match;
        final double notmatch = mutant_notmatch + wildType_notmatch;
        final double notmatch_log = log(notmatch) * notmatch;

        double q1;
        if (mutant_match == 0.0 && wildType_match == 0.0) {
            q1 = mutant_notmatch_log + wildType_notmatch_log;
        } else if (wildType_match == 0.0) {
            q1 = mutant_match_log + mutant_notmatch_log + wildType_notmatch_log;
        } else if (mutant_match == 0.0) {
            q1 = mutant_notmatch_log + wildType_match_log + wildType_notmatch_log;
        } else {
            q1 = mutant_match_log + mutant_notmatch_log + wildType_match_log + wildType_notmatch_log;
        }

        final double q2 = match == 0.0 ?
                mutant_total_log + wildType_total_log + notmatch_log :
                mutant_total_log + wildType_total_log + match_log + notmatch_log;

        // Compute G
        final double g = 2.0 * (q1 - q2 + q3);

        // Williams's correction for 2 x 2 table is ...
        final double q = match == 0.0 ?
                1.0 + (total / notmatch - 1.0) * qcomm :
                1.0 + (total / match + total / notmatch - 1.0) * qcomm;
        return g / q;
    }

    /**
     * Calculate FDR using the Benjamini-Hochberg method.
     */
    private void matched_fdr() {
        List<Map.Entry<Integer, Double>> list_entries = new ArrayList<>(this.pval.entrySet());
        list_entries.sort(Map.Entry.comparingByValue());

        // Get values in element order
        double vector_len = this.gval.size();
        double vector_pos = 1.0;
        double pval_prev = list_entries.getFirst().getValue();
        double fdr_prev = Math.min(pval_prev * vector_len, 1.0);

        for (Map.Entry<Integer, Double> entry : list_entries) {
            if (entry.getValue() == pval_prev) {
                this.fdr.put(entry.getKey(), fdr_prev);
            } else {
                pval_prev = entry.getValue();
                double value = Math.min(entry.getValue() * vector_len / vector_pos, 1.0);
                this.fdr.put(entry.getKey(), value);
                fdr_prev = value;
            }
            vector_pos += 1.0;
        }
    }

    /**
     * Complemented Chi-square distribution.
     *
     * @param x G-value
     * @return the area under the right hand tail (from x to infinity)
     * of the Chi square probability density function with v degrees of freedom
     */
    private double chdtrc(double x) {
        // degrees of freedom
        double df = 1.0;

        if (x < 0.0) {
            return 0.0;
        } else {
            return Gamma.incompleteGammaComplement(df / 2.0, x / 2.0);
        }
    }
}
