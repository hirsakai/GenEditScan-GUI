# GenEditScan: a fast and efficient analysis tool to detect foreign DNA in the genome-edited agricultural products using high-throughput sequencing data
This repository provides programs for the k-mer analysis to detect unintended external DNA in a host genome.

GenEditScan-GUI is based upon kmer (https://github.com/taitoh1970/kmer).

## Installation

### Installing Java
Please make sure that java 21 or later version is installed and `bin` directory's PATH is valid.  
Since GenEditScan-GUI is using JavaFX library, "Full JDK" or "Full JRE" package must be installed (e.g. "Full JDK" package of Liberica JDK: https://bell-sw.com/libericajdk).

The automatically generated source code (zip, tar.gz) includes a pre-built jar file.

## Getting started

### Windows
Move to the "GenEditScan" directory and double-click on "GenEditScan_win.vbs" or a shortcut to this vbs file (note: a copy of the vbs file will not function).

### Mac
Move to the "GenEditScan" directory and launch "GenEditScan_mac.sh" from the terminal or double-click on "GenEditScan_mac.command".

See the `GenEditScan-GUI_UserGuide.pdf` file in the GenEditScan directory for details.

## Dependencies

### Apache Batik
https://xmlgraphics.apache.org/batik

### Apache FOP
https://xmlgraphics.apache.org/fop

### JFXConverter
https://jfxconverter.sourceforge.io

### Colt
https://dst.lbl.gov/ACSSoftware/colt

## Citation
If you're using GenEditScan-GUI in your work, please cite:

Itoh T, Onuki R, Tsuda M, Oshima M, Endo M, Sakai H, Tanaka T, Ohsawa R, Tabei Y. Foreign DNA detection by high-throughput sequencing to regulate genome-edited agricultural products. Sci Rep. 2020 Mar 18;10(1):4914. doi: 10.1038/s41598-020-61949-5. PMID: 32188926; PMCID: PMC7080720.  
https://www.nature.com/articles/s41598-020-61949-5
