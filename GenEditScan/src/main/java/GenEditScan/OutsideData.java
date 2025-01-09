/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Outside data class.
 *
 * @author NARO
 */
public class OutsideData {
    public Map<Integer, Map<Integer, Double>> gval = new HashMap<>();
    public Map<Integer, Map<Integer, Double>> pval = new HashMap<>();
    public Map<Integer, List<String>> left_chain = new HashMap<>();
    public Map<Integer, List<String>> right_chain = new HashMap<>();
    public Map<Integer, List<Integer>> mutant_count = new HashMap<>();
    public Map<Integer, List<Integer>> wildType_count = new HashMap<>();
}
