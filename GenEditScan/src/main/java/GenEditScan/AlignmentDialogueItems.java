/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.beans.property.*;

/**
 * Alignment dialog items class.
 *
 * @author NARO
 */
public class AlignmentDialogueItems {
    //========================================================================//
    // Local data
    //========================================================================//
    private final IntegerProperty position;
    private final IntegerProperty id;
    private final StringProperty sequence;
    private final IntegerProperty mutant;
    private final IntegerProperty wildType;
    private final FloatProperty pvalue;
    private final FloatProperty fdr;
    private final FloatProperty bonferroni;

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Alignment dialog items class constructor.
     *
     * @param position   vector position
     * @param id         ID
     * @param sequence   sequence
     * @param mutant     mutant
     * @param wildType   wild type
     * @param pvalue     P-value
     * @param fdr        FDR
     * @param bonferroni Bonferroni
     */
    public AlignmentDialogueItems(Integer position, Integer id, String sequence,
                                  Integer mutant, Integer wildType,
                                  Float pvalue, Float fdr, Float bonferroni) {
        this.position = new SimpleIntegerProperty(position);
        this.id = new SimpleIntegerProperty(id);
        this.sequence = new SimpleStringProperty(sequence);
        this.mutant = new SimpleIntegerProperty(mutant);
        this.wildType = new SimpleIntegerProperty(wildType);
        this.pvalue = new SimpleFloatProperty(pvalue);
        this.fdr = new SimpleFloatProperty(fdr);
        this.bonferroni = new SimpleFloatProperty(bonferroni);
    }

    //========================================================================//
    // Property
    //========================================================================//
    public IntegerProperty positionProperty() {
        return this.position;
    }

    public IntegerProperty idProperty() {
        return this.id;
    }

    public StringProperty sequenceProperty() {
        return this.sequence;
    }

    public IntegerProperty mutantProperty() {
        return this.mutant;
    }

    public IntegerProperty wildTypeProperty() {
        return this.wildType;
    }

    public FloatProperty pvalueProperty() {
        return this.pvalue;
    }

    public FloatProperty fdrProperty() {
        return this.fdr;
    }

    public FloatProperty bonferroniProperty() {
        return this.bonferroni;
    }

}
