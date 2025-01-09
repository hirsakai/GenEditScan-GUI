/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.*;

/**
 * Graph drawing process class.
 *
 * @author NARO
 */
public class DrawGraph {
    //========================================================================//
    // Constructor's parameter
    //========================================================================//
    private final String statisticsFile;
    private final LineChart<Number, Number> lineChartUpper;
    private final AreaChart<Number, Number> areaChartLower;
    private final String mutantColor;
    private final String wildTypeColor;
    private final String significantColor;
    private final String negligibleColor;
    private final String thresholdColor;
    private final double thresholdValue;

    //========================================================================//
    // Local parameter
    //========================================================================//
    private final List<Integer> position = new ArrayList<>();
    private String vectorArray;
    private final List<Integer> mutantCount = new ArrayList<>();
    private final List<Integer> wildTypeCount = new ArrayList<>();
    private final List<Double> gvalue = new ArrayList<>();
    private final List<Double> pvalue = new ArrayList<>();
    private final List<Double> fdr = new ArrayList<>();
    private final List<Double> bonferroni = new ArrayList<>();
    private int kmer = 0;
    private final Map<Integer, Integer> sequenceIndex = new HashMap<>();
    private final List<String> sequenceList = new ArrayList<>();

    private final float FLOAT_LIMIT = 1.0E-38F;

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Graph drawing process class constructor.
     *
     * @param statistics statistics.txt file
     * @param chartUpper LineChart shown above
     * @param chartLower LineChart shown below
     * @param mcolor     mutant color
     * @param wcolor     wild type color
     * @param scolor     significant color
     * @param ncolor     negligible color
     * @param tcolor     threshold color of the test
     * @param threshold  Threshold for verification
     */
    public DrawGraph(TextField statistics,
                     LineChart<Number, Number> chartUpper, AreaChart<Number, Number> chartLower,
                     String mcolor, String wcolor, String scolor, String ncolor, String tcolor,
                     double threshold) {
        // Constructor's parameter
        this.statisticsFile = statistics.getText();
        this.lineChartUpper = chartUpper;
        this.areaChartLower = chartLower;
        this.mutantColor = mcolor;
        this.wildTypeColor = wcolor;
        this.significantColor = scolor;
        this.negligibleColor = ncolor;
        this.thresholdColor = tcolor;
        this.thresholdValue = threshold;
    }

    /**
     * Draw K-mer counts and statistics.
     *
     * @param selectedIndex y-axis title identification index
     * @return true:normal, false:abnormal
     */
    public boolean drawStatistics(int selectedIndex) {
        File file = new File(this.statisticsFile);
        try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
            int iPos = 0;
            int iSeq = 1;
            int iMutant = 2;
            int iWildType = 3;
            int iGval = 4;
            int iPval = 5;
            int iFDR = 6;
            int iBonferroni = 7;

            StringBuilder sequence = new StringBuilder();
            String str = br.readLine();
            if (str.startsWith("#K-mer")) {
                this.kmer = Integer.parseInt(str.split("\t")[1]);
                str = br.readLine();
            } else {
                return false;
            }

            if (str.startsWith("#Pos")) {
                List<String> headers = new ArrayList<>(Arrays.asList(str.split("\t")));
                if (headers.contains("#Pos")) {
                    iPos = headers.indexOf("#Pos");
                }
                if (headers.contains("Seq")) {
                    iSeq = headers.indexOf("Seq");
                }
                if (headers.contains("Mutant")) {
                    iMutant = headers.indexOf("Mutant");
                }
                if (headers.contains("WildType")) {
                    iWildType = headers.indexOf("WildType");
                }
                if (headers.contains("Gval")) {
                    iGval = headers.indexOf("Gval");
                }
                if (headers.contains("Pval")) {
                    iPval = headers.indexOf("Pval");
                }
                if (headers.contains("FDR")) {
                    iFDR = headers.indexOf("FDR");
                }
                if (headers.contains("Bonferroni")) {
                    iBonferroni = headers.indexOf("Bonferroni");
                }
            }

            while ((str = br.readLine()) != null) {
                String[] posLine = str.split("\t");
                this.position.add(Integer.parseInt(posLine[iPos]));
                sequence.append(posLine[iSeq]);
                this.mutantCount.add(Integer.parseInt(posLine[iMutant]));
                this.wildTypeCount.add(Integer.parseInt(posLine[iWildType]));
                this.gvalue.add(Math.max(Double.parseDouble(posLine[iGval]), 0.0));
                this.pvalue.add(Math.max(Double.parseDouble(posLine[iPval]), FLOAT_LIMIT));
                this.fdr.add(Math.max(Double.parseDouble(posLine[iFDR]), FLOAT_LIMIT));
                this.bonferroni.add(Math.max(Double.parseDouble(posLine[iBonferroni]), FLOAT_LIMIT));
            }
            this.vectorArray = sequence.append(sequence, 0, this.kmer - 1).toString();

            // Upper chart wild type (first)
            this.integerSeries(this.wildTypeColor, this.wildTypeCount);

            // Upper chart mutant (second)
            this.integerSeries(this.mutantColor, this.mutantCount);

            switch (selectedIndex) {
                case 0:
                    List<Double> value0 = minusLog10(this.fdr);
                    double threshold0 = -Math.log10(this.thresholdValue);
                    this.doubleSeriesThreshold(threshold0);
                    this.highValueSequence(value0, threshold0);
                    this.doubleSeriesMultiColors(value0, threshold0);
                    break;
                case 1:
                    List<Double> value1 = minusLog10(this.bonferroni);
                    double threshold1 = -Math.log10(this.thresholdValue);
                    this.doubleSeriesThreshold(threshold1);
                    this.highValueSequence(value1, threshold1);
                    this.doubleSeriesMultiColors(value1, threshold1);
                    break;
                case 2:
                    List<Double> value2 = minusLog10(this.pvalue);
                    double threshold2 = -Math.log10(this.thresholdValue);
                    this.doubleSeriesThreshold(threshold2);
                    this.highValueSequence(value2, threshold2);
                    this.doubleSeriesMultiColors(value2, threshold2);
                    break;
                case 3:
                    this.doubleSeriesThreshold(this.thresholdValue);
                    this.highValueSequence(this.gvalue, this.thresholdValue);
                    this.doubleSeriesMultiColors(this.gvalue, this.thresholdValue);
                    break;
                default:
                    break;
            }

            NumberAxis xAxis = (NumberAxis) this.lineChartUpper.getXAxis();
            if (!xAxis.isAutoRanging()) {
                double xAxisMin = xAxis.getLowerBound();
                double xAxisMax = xAxis.getUpperBound();

                // Y-axis (upper)
                NumberAxis yAxisUpper = (NumberAxis) this.lineChartUpper.getYAxis();
                if (yAxisUpper.isAutoRanging()) {
                    this.adjustYaxisRange(this.lineChartUpper, xAxisMin, xAxisMax);
                }

                // Y-axis (lower)
                NumberAxis yAxisLower = (NumberAxis) this.areaChartLower.getYAxis();
                if (yAxisLower.isAutoRanging()) {
                    this.adjustYaxisRange(this.areaChartLower, xAxisMin, xAxisMax);
                }

            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    //========================================================================//
    // Private function
    //========================================================================//

    /**
     * LineChart drawing process for K-mer counts.
     *
     * @param color color
     * @param value y-axis dat
     */
    private void integerSeries(String color, List<Integer> value) {
        ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList();
        for (int i = 0; i < this.position.size(); i++) {
            data.add(new XYChart.Data<>(this.position.get(i), value.get(i)));
        }

        XYChart.Series<Number, Number> series = new XYChart.Series<>(data);
        this.lineChartUpper.getData().add(series);

        // set line color on Series node
        String lineStyle = String.format("-fx-stroke: %s;", color);
        series.getNode().setStyle(lineStyle);
    }

    /**
     * Draw the threshold value on LineChart.
     *
     * @param threshold threshold
     */
    private void doubleSeriesThreshold(double threshold) {
        ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList();
        data.add(new XYChart.Data<>(this.position.getFirst(), threshold));
        data.add(new XYChart.Data<>(this.position.getLast(), threshold));
        XYChart.Series<Number, Number> series;
        series = new XYChart.Series<>(data);
        this.areaChartLower.getData().add(series);

        // set line color on Series node
        String lineStyle = String.format("-fx-stroke: %s;", this.thresholdColor);

        String fillStyle = "-fx-fill: TRANSPARENT;";
        Node fill = series.getNode().lookup(".chart-series-area-fill");
        Node line = series.getNode().lookup(".chart-series-area-line");
        fill.setStyle(fillStyle);
        line.setStyle(lineStyle);
    }

    /**
     * Get k-mer sequences that exceed the threshold.
     *
     * @param value     test value
     * @param threshold threshold of test value
     */
    private void highValueSequence(List<Double> value, double threshold) {
        boolean status = false;
        int startIndex = -1;
        if (value.getFirst() >= threshold) {
            status = true;
            startIndex = 0;
        }

        for (int i = 1; i < this.position.size() - 1; i++) {
            if (status && value.get(i) < threshold) {
                status = false;
                int idx = this.sequenceList.size();
                for (int j = startIndex; j < i; j++) {
                    this.sequenceIndex.put(j, idx);
                }
                this.sequenceList.add(this.vectorArray.substring(startIndex, i + this.kmer - 1));
            } else if (!status && value.get(i) >= threshold) {
                startIndex = i;
                status = true;
            }
        }

        int lastIdx = this.position.size() - 1;
        if (status && value.get(lastIdx) < threshold) {
            int idx = this.sequenceList.size();
            for (int j = startIndex; j < lastIdx; j++) {
                this.sequenceIndex.put(j, idx);
            }
            this.sequenceList.add(this.vectorArray.substring(startIndex, lastIdx + this.kmer - 1));
        } else if (!status && value.get(lastIdx) >= threshold) {
            startIndex = lastIdx;
            int idx = this.sequenceList.size();
            this.sequenceIndex.put(startIndex, idx);
            this.sequenceList.add(this.vectorArray.substring(lastIdx, lastIdx + this.kmer));
        }
    }

    /**
     * Process of drawing a LineChart of the G statistic.
     *
     * @param value     test value
     * @param threshold threshold of test value
     */
    private void doubleSeriesMultiColors(List<Double> value, Double threshold) {
        XYChart.Series<Number, Number> series;
        ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList();
        data.add(new XYChart.Data<>(this.position.getFirst(), value.getFirst()));
        boolean status = value.getFirst() >= threshold;
        String color = value.getFirst() >= threshold ? this.significantColor : this.negligibleColor;

        String fillStyle = "-fx-fill: TRANSPARENT;";

        for (int i = 1; i < this.position.size(); i++) {
            if ((status && value.get(i) < threshold) ||
                    !status && value.get(i) >= threshold) {
                double posThread = this.positionInterporation(this.position.get(i - 1), value.get(i - 1),
                        this.position.get(i), value.get(i));
                data.add(new XYChart.Data<>(posThread, threshold));
                series = new XYChart.Series<>(data);
                this.areaChartLower.getData().add(series);

                // set line color on Series node
                String lineStyle = String.format("-fx-stroke: %s;", color);
                Node fill = series.getNode().lookup(".chart-series-area-fill"); // only for AreaChart
                Node line = series.getNode().lookup(".chart-series-area-line");
                fill.setStyle(fillStyle);
                line.setStyle(lineStyle);

                if (this.sequenceIndex.containsKey(i - 1)) {
                    this.selectVectorRange(series, fill, i - 1);
                }

                data = FXCollections.observableArrayList();
                data.add(new XYChart.Data<>(posThread, threshold));
                color = status ? this.negligibleColor : this.significantColor;
                status = !status;
            }
            data.add(new XYChart.Data<>(this.position.get(i), value.get(i)));
        }

        int lastIdx = this.position.size() - 1;
        series = new XYChart.Series<>(data);
        this.areaChartLower.getData().add(series);

        // set line color on Series node
        String lineStyle = String.format("-fx-stroke: %s;", color);
        Node fill = series.getNode().lookup(".chart-series-area-fill"); // only for AreaChart
        Node line = series.getNode().lookup(".chart-series-area-line");
        fill.setStyle(fillStyle);
        line.setStyle(lineStyle);

        if (this.sequenceIndex.containsKey(lastIdx)) {
            this.selectVectorRange(series, fill, lastIdx);
        }
    }

    /**
     * Select a range of vector with the mouse.
     *
     * @param series XYChart.Series
     * @param fill   Node
     * @param index  vector position
     */
    private void selectVectorRange(XYChart.Series<Number, Number> series, Node fill, int index) {
        int seqIndex = this.sequenceIndex.get(index);
        String mer = this.sequenceList.get(seqIndex);
        int startIndex = index + this.kmer - mer.length();
        String areaStyle = String.format("-fx-stroke: %s;", this.significantColor);

        // handler for clicking on data point:
        series.getNode().setOnMouseClicked(t -> {
            if (t.getButton() == MouseButton.PRIMARY) {
                fill.setStyle(areaStyle);
                new AlignmentDialogueController(fill, startIndex + 1, startIndex + mer.length());
            }
        });
    }

    /**
     * Get the negative value of log.
     *
     * @param rawValue raw value
     * @return negative value of log
     */
    private List<Double> minusLog10(List<Double> rawValue) {
        List<Double> value = new ArrayList<>();
        for (double raw : rawValue) {
            value.add(-Math.log10(raw));
        }
        return value;
    }

    /**
     * Calculate the coordinates on the vector sequence when the G statistic is threshold.
     *
     * @param x1 coordinate value of X1
     * @param y1 coordinate value of Y1
     * @param x2 coordinate value of X2
     * @param y2 coordinate value of Y2
     * @return interpolated value of X coordinate
     */
    private double positionInterporation(double x1, double y1, double x2, double y2) {
        double dy = y2 - y1;
        if (Math.abs(dy) <= 1.0e-14) {
            return (x2 - x1) / 2.0;
        } else {
            return (this.thresholdValue - y1) * (x2 - x1) / dy + x1;
        }
    }

    /**
     * Adjust y-coordinate range.
     *
     * @param xyChart  LineChart or AreaChart
     * @param xAxisMin minimum value of x-coordinate
     * @param xAxisMax maximum value of x-coordinate
     */
    public void adjustYaxisRange(XYChart<Number, Number> xyChart, double xAxisMin, double xAxisMax) {
        NumberAxis yAxis = (NumberAxis) xyChart.getYAxis();
        double yAxisMax = 0.0;

        for (XYChart.Series<Number, Number> series : xyChart.getData()) {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                Number xValue = data.getXValue();
                Number yValue = data.getYValue();

                // Only data points within the range of the X axis are considered
                if (xValue.doubleValue() >= xAxisMin && xValue.doubleValue() <= xAxisMax) {
                    if (yValue.doubleValue() > yAxisMax) yAxisMax = yValue.doubleValue();
                }
            }
        }

        // Set Y-axis range based on calculated range
        if (yAxisMax > 0.0) {
            // Turn off automatic range setting
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0.0);

            yAxisMax *= 1.2;
            BigDecimal number = new BigDecimal(yAxisMax);

            // Specify the number of significant digits
            int precision = 2;

            // Use MathContext to specify significant digits and rounding mode
            MathContext mc = new MathContext(precision, RoundingMode.UP);

            // Round numbers with the specified number of significant digits and rounding mode
            BigDecimal rounded = number.round(mc);
            yAxis.setUpperBound(rounded.doubleValue());
        }
    }
}
