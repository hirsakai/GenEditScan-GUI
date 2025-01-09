/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

/**
 * Bitwise operation class.
 *
 * @author NARO
 */
public class BitwiseOperation {
    //========================================================================//
    // Local data
    //========================================================================//
    /**
     * for bitwise operation, DNA expressed in 2 bits
     */
    private final byte[] dna2bit;

    /**
     * for bitwise operation, chunk array
     */
    private final byte[] chunk;

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Bitwise operation class constructor.
     *
     * @param options Execution options class
     */
    public BitwiseOperation(Options options) {
        this.dna2bit = new byte[128];
        this.dna2bit[84] = 0;   // T or others
        this.dna2bit[67] = 1;   // C
        this.dna2bit[65] = 2;   // A
        this.dna2bit[71] = 3;   // G
        this.chunk = new byte[options.getMax_chunk_array() + 1];  // +1 : signed language only
    }

    // Getter

    public byte[] getDna2bit() {
        return this.dna2bit;
    }

    public byte[] getChunk() {
        return this.chunk;
    }
}
