/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.jfxconverter.JFXConverter;
import org.jfxconverter.drivers.svg.ConvertorSVGGraphics2D;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.geom.Rectangle2D;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Graph drawing process controller class.
 *
 * @author NARO
 */
public class DrawGraphController extends ControllerBase implements Initializable {
    @FXML
    private AnchorPane mainPaneID;          // Main window

    //========================================================================//
    // fx:id
    //========================================================================//
    @FXML
    private VBox vBoxChartID;                   // VBox for two LineChart
    @FXML
    private LineChart<Number, Number> lineChartUpperID;    // LineChart figure (upper)
    @FXML
    private NumberAxis xupperAxis;              // LineChart X-axis (upper)
    @FXML
    private NumberAxis yupperAxis;              // LineChart Y-axis (upper)
    @FXML
    private AreaChart<Number, Number> areaChartLowerID;   // LineChart figure (lower)
    @FXML
    private NumberAxis xlowerAxis;              // LineChart X-axis (lower)
    @FXML
    private NumberAxis ylowerAxis;              // LineChart Y-axis (lower)

    //========================================================================//
    // fx:id (Y-axis (upper))
    //========================================================================//
    @FXML
    private Label yupperAxisLabelID;            // Y-axis (upper)
    @FXML
    private TextField yupperAxisTitleID;        // Y-axis (upper) title
    @FXML
    private ToggleGroup toggleYupperAxis;
    @FXML
    private RadioButton radioYupperAutoID;      // Y-axis (upper) auto
    @FXML
    private RadioButton radioYupperSpecifyID;   // Y-axis (upper) specify
    @FXML
    private TextField yupperAxisFromID;         // Y-axis (upper) from
    @FXML
    private TextField yupperAxisToID;           // Y-axis (upper) to
    @FXML
    private Label mutantLabelID;                // Mutant label
    @FXML
    private ColorPicker colorPickerMutantID;    // Mutant color
    @FXML
    private Label wildTypeLabelID;              // Wild type label
    @FXML
    private ColorPicker colorPickerWildTypeID;  // Wild type color

    //========================================================================//
    // fx:id (Y-axis (lower))
    //========================================================================//
    @FXML
    private Label ylowerAxisLabelID;            // Y-axis (lower)
    @FXML
    private ComboBox<String> ylowerAxisTitleID;        // Y-axis (lower) title
    @FXML
    private ToggleGroup toggleYlowerAxis;
    @FXML
    private RadioButton radioYlowerAutoID;      // Y-axis (lower) auto
    @FXML
    private RadioButton radioYlowerSpecifyID;   // Y-axis (lower) specify
    @FXML
    private TextField ylowerAxisFromID;         // Y-axis (lower) from
    @FXML
    private TextField ylowerAxisToID;           // Y-axis (lower) to
    @FXML
    private Label significantLabelID;           // Significant label;
    @FXML
    private ColorPicker colorPickerSignificantID;   // Significant color
    @FXML
    private Label notSignificantLabelID;        // Not significant label
    @FXML
    private ColorPicker colorPickerNotSignificantID;    // Not significant color
    @FXML
    private Label thresholdLabelID;             // Threshold label
    @FXML
    private TextField thresholdValueID;        // Threshold value
    @FXML
    private ColorPicker colorPickerThresholdID; // Threshold color

    //========================================================================//
    // fx:id (X-axis)
    //========================================================================//
    @FXML
    private Label xAxisLabelID;                 // X-axis
    @FXML
    private TextField xAxisTitleID;             // X-axis title
    @FXML
    private ToggleGroup toggleXAxis;
    @FXML
    private RadioButton radioXAutoID;           // X-axis auto
    @FXML
    private RadioButton radioXSpecifyID;        // X-axis specify
    @FXML
    private TextField xAxisFromID;              // X-axis from
    @FXML
    private TextField xAxisToID;                // X-axis to

    //========================================================================//
    // fx:id (others)
    //========================================================================//
    @FXML
    private CheckBox checkRotateYticksID;       // Rotate yticks
    @FXML
    private TextField statisticsFileID;         // Statistics file
    @FXML
    private Button buttonOutsideFileID;         // Outside file:
    @FXML
    private TextField outsideFileID;            // Outside file

    //========================================================================//
    // Local parameter
    //========================================================================//
    private final Rectangle rectangle = new Rectangle(0, 0, 0, 0);
    private int xStart = 0;
    private final int MIN_WIDTH = 1;
    private final double CORRECT_MOUSE_POSITION = 10.0;

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Graph drawing process controller class constructor.
     */
    public DrawGraphController() {
        this.rectangle.setFill(Color.rgb(0, 0, 255, 0.1));
    }

    /**
     * Get the data from the Configuration file.
     */
    public void importConfiguration() {
        super.setTextField(this.yupperAxisTitleID, super.userConfiguration.getYupperAxisTitle());
        this.radioYupperAutoID.setSelected(super.userConfiguration.getYupperAxisAuto());
        this.radioYupperSpecifyID.setSelected(super.userConfiguration.getYupperAxisSpecify());
        super.setTextField(this.yupperAxisFromID, super.userConfiguration.getYupperAxisFrom());
        super.setTextField(this.yupperAxisToID, super.userConfiguration.getYupperAxisTo());
        super.setColorPicker(this.colorPickerMutantID, super.userConfiguration.getMutantColor());
        super.setColorPicker(this.colorPickerWildTypeID, super.userConfiguration.getWildTypeColor());
        this.ylowerAxisTitleID.getSelectionModel().select(super.userConfiguration.getYlowerAxisTitle());
        this.radioYlowerAutoID.setSelected(super.userConfiguration.getYlowerAxisAuto());
        this.radioYlowerSpecifyID.setSelected(super.userConfiguration.getYlowerAxisSpecify());
        super.setTextField(this.ylowerAxisFromID, super.userConfiguration.getYlowerAxisFrom());
        super.setTextField(this.ylowerAxisToID, super.userConfiguration.getYlowerAxisTo());
        super.setColorPicker(this.colorPickerSignificantID, super.userConfiguration.getSignificantColor());
        super.setColorPicker(this.colorPickerNotSignificantID, super.userConfiguration.getNotSignificantColor());
        super.setTextField(this.thresholdValueID, super.userConfiguration.getThresholdValue());
        super.setColorPicker(this.colorPickerThresholdID, super.userConfiguration.getThresholdColor());
        super.setTextField(this.xAxisTitleID, super.userConfiguration.getXaxisTitle());
        this.radioXAutoID.setSelected(super.userConfiguration.getXaxisAuto());
        this.radioXSpecifyID.setSelected(super.userConfiguration.getXaxisSpecify());
        super.setTextField(this.xAxisFromID, super.userConfiguration.getXaxisFrom());
        super.setTextField(this.xAxisToID, super.userConfiguration.getXaxisTo());
        this.checkRotateYticksID.setSelected(super.userConfiguration.getRotateYticks());
        super.setTextField(this.statisticsFileID, super.userConfiguration.getStatisticsFile());
        super.setTextField(this.outsideFileID, super.userConfiguration.getOutsideFile());
        this.setLabelsColor();
    }

    /**
     * Set the data in the Configuration file.
     */
    public void exportConfiguration() {
        super.userConfiguration.setYupperAxisTitle(this.yupperAxisTitleID);
        super.userConfiguration.setYupperAxisAuto(this.radioYupperAutoID);
        super.userConfiguration.setYupperAxisSpecify(this.radioYupperSpecifyID);
        super.userConfiguration.setYupperAxisFrom(this.yupperAxisFromID);
        super.userConfiguration.setYupperAxisTo(this.yupperAxisToID);
        super.userConfiguration.setMutantColor(this.colorPickerMutantID);
        super.userConfiguration.setWildTypeColor(this.colorPickerWildTypeID);
        super.userConfiguration.setYlowerAxisTitle(this.ylowerAxisTitleID);
        super.userConfiguration.setYlowerAxisAuto(this.radioYlowerAutoID);
        super.userConfiguration.setYlowerAxisSpecify(this.radioYlowerSpecifyID);
        super.userConfiguration.setYlowerAxisFrom(this.ylowerAxisFromID);
        super.userConfiguration.setYlowerAxisTo(this.ylowerAxisToID);
        super.userConfiguration.setSignificantColor(this.colorPickerSignificantID);
        super.userConfiguration.setNotSignificantColor(this.colorPickerNotSignificantID);
        super.userConfiguration.setThresholdValue(this.thresholdValueID);
        super.userConfiguration.setThresholdColor(this.colorPickerThresholdID);
        super.userConfiguration.setXaxisTitle(this.xAxisTitleID);
        super.userConfiguration.setXaxisAuto(this.radioXAutoID);
        super.userConfiguration.setXaxisSpecify(this.radioXSpecifyID);
        super.userConfiguration.setXaxisFrom(this.xAxisFromID);
        super.userConfiguration.setXaxisTo(this.xAxisToID);
        super.userConfiguration.setRotateYticks(this.checkRotateYticksID);
        super.userConfiguration.setStatisticsFile(this.statisticsFileID);
        super.userConfiguration.setOutsideFile(this.outsideFileID);
    }

    /**
     * Sets the rotation angle of the Y-axis memory.
     *
     * @param b true:rotate 270 degree, false:not rotate
     */
    public void setRotateYticks(boolean b) {
        this.checkRotateYticksID.setSelected(b);
    }

    /**
     * Set the color of the Label to the color of the ColorPicker.
     */
    public void setLabelsColor() {
        // For display characters (ColorPicker's bug?)
        super.setColorPicker(this.colorPickerMutantID, this.colorPickerMutantID.getValue().toString());
        super.setColorPicker(this.colorPickerWildTypeID, this.colorPickerWildTypeID.getValue().toString());
        super.setColorPicker(this.colorPickerSignificantID, this.colorPickerSignificantID.getValue().toString());
        super.setColorPicker(this.colorPickerNotSignificantID, this.colorPickerNotSignificantID.getValue().toString());
        super.setColorPicker(this.colorPickerThresholdID, this.colorPickerThresholdID.getValue().toString());

        // Set label's color
        this.mutantLabelID.setTextFill(this.colorPickerMutantID.getValue());
        this.wildTypeLabelID.setTextFill(this.colorPickerWildTypeID.getValue());
        this.significantLabelID.setTextFill(this.colorPickerSignificantID.getValue());
        this.notSignificantLabelID.setTextFill(this.colorPickerNotSignificantID.getValue());
        this.thresholdLabelID.setTextFill(this.colorPickerThresholdID.getValue());
    }

    /**
     * Do not output a graph legend.
     */
    public void removeLegends() {
        this.lineChartUpperID.setLegendVisible(false);
        this.areaChartLowerID.setLegendVisible(false);
    }

    /**
     * Set statistics.txt file.
     *
     * @param statisticsFilePath path of statistics.txt
     */
    public void setStatisticsFile(String statisticsFilePath) {
        this.statisticsFileID.setText(statisticsFilePath);
        this.checkRotateYticksID.setSelected(true);
    }

    /**
     * Set outside.txt file.
     *
     * @param outsideFilePath path of outside.txt
     */
    public void setOutsideFile(String outsideFilePath) {
        this.outsideFileID.setText(outsideFilePath);
    }

    /**
     * Initialize.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Y-axis (upper) title
        this.yupperAxisTitleID.textProperty().addListener((observe, oldVal, newVal) -> this.yupperAxis.setLabel(newVal));

        // Y-axis (upper) auto
        this.radioYupperAutoID.selectedProperty().addListener((observe, oldVal, newVal) -> {
            if (newVal) {
                this.redraw();
            }
        });

        // Select outside file (right button)
        this.outsideFileID.textProperty().addListener((observe, oldVal, newVal) -> {
            AlignmentDialogueController.outsideFile = newVal;
            this.buttonOutsideFileID.setDisable(newVal.isEmpty());
        });

        // Y-axis (lower) title
        this.ylowerAxisTitleID.valueProperty().addListener((observe, oldVal, newVal) -> {
            this.ylowerAxis.setLabel(newVal);
            if (newVal.startsWith("FDR")) {
                this.thresholdValueID.setText("0.01");
            } else if (newVal.startsWith("Bonferroni")) {
                this.thresholdValueID.setText("0.01");
            } else if (newVal.startsWith("P-value")) {
                this.thresholdValueID.setText("0.01");
            } else if (newVal.startsWith("G-statistics")) {
                this.thresholdValueID.setText("6.63");
            }
            this.redraw();
        });

        // Y-axis (lower) auto
        this.radioYlowerAutoID.selectedProperty().addListener((observe, oldVal, newVal) -> {
            if (newVal) {
                this.redraw();
            }
        });

        // X-axis title
        this.xAxisTitleID.textProperty().addListener((observe, oldVal, newVal) -> {
            this.xupperAxis.setLabel(newVal);
            this.xlowerAxis.setLabel(newVal);
        });

        // X-axis auto or specify
        this.radioXAutoID.selectedProperty().addListener((observe, oldVal, newVal) -> {
            if (newVal) {
                this.redraw();
            }
        });

        this.lineChartUpperID.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                Axis<Number> yAxis = this.lineChartUpperID.getYAxis();
                Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
                double y = yAxis.sceneToLocal(mouseSceneCoords).getY();
                int yClick = yAxis.getValueForDisplay(y).intValue();

                this.radioYupperAutoID.setSelected(false);
                this.radioYupperSpecifyID.setSelected(true);
                this.yupperAxisToID.setText(String.valueOf(yClick));
                redrawAction();
            }
        });

        this.areaChartLowerID.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                Axis<Number> yAxis = this.areaChartLowerID.getYAxis();
                Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
                double y = yAxis.sceneToLocal(mouseSceneCoords).getY();
                int yClick = yAxis.getValueForDisplay(y).intValue();

                this.radioYlowerAutoID.setSelected(false);
                this.radioYlowerSpecifyID.setSelected(true);
                this.ylowerAxisToID.setText(String.valueOf(yClick));
                redrawAction();
            }
        });

        final double[] mouseX = new double[1];

        // Processing when the mouse is pressed.
        this.lineChartUpperID.setOnMousePressed(event -> {
            Axis<Number> xAxis = this.lineChartUpperID.getXAxis();
            Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
            double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
            this.xStart = xAxis.getValueForDisplay(x).intValue();

            this.mainPaneID.getChildren().add(this.rectangle);
            mouseX[0] = event.getX() + this.CORRECT_MOUSE_POSITION;
            this.rectangle.setX(mouseX[0]);
            this.rectangle.setY(this.vBoxChartID.getScene().getY());
            this.rectangle.setWidth(0.0);
            this.rectangle.setHeight(this.vBoxChartID.getLayoutBounds().getHeight() - 50.0);
        });

        // Processing when the mouse is dragged.
        this.lineChartUpperID.setOnMouseDragged(event -> {
            this.rectangle.setWidth(Math.abs(event.getX() + this.CORRECT_MOUSE_POSITION - mouseX[0]));
            this.rectangle.setX(Math.min(mouseX[0], event.getX() + this.CORRECT_MOUSE_POSITION));
        });

        // Processing when the mouse is released.
        this.lineChartUpperID.setOnMouseReleased(event -> {
            Axis<Number> xAxis = this.lineChartUpperID.getXAxis();
            Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
            double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
            int xEnd = xAxis.getValueForDisplay(x).intValue();

            // If the mouse is dragged in the opposite direction.
            if (xEnd + this.MIN_WIDTH < this.xStart) {
                int xtmp = xEnd;
                xEnd = this.xStart;
                this.xStart = xtmp;
            }
            this.xStart = Math.max(this.xStart, 0);

            if (xEnd > this.xStart + this.MIN_WIDTH) {
                this.radioXAutoID.setSelected(false);
                this.radioXSpecifyID.setSelected(true);
                this.xAxisFromID.setText(String.valueOf(this.xStart));
                this.xAxisToID.setText(String.valueOf(xEnd));
                redrawAction();
            }

            this.mainPaneID.getChildren().remove(this.rectangle);
        });

        // Processing when the mouse is pressed.
        this.areaChartLowerID.setOnMousePressed(event -> {
            Axis<Number> xAxis = this.areaChartLowerID.getXAxis();
            Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
            double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
            this.xStart = xAxis.getValueForDisplay(x).intValue();

            this.mainPaneID.getChildren().add(this.rectangle);
            mouseX[0] = event.getX() + this.CORRECT_MOUSE_POSITION;
            this.rectangle.setX(mouseX[0]);
            this.rectangle.setY(this.vBoxChartID.getScene().getY());
            this.rectangle.setWidth(0.0);
            this.rectangle.setHeight(this.vBoxChartID.getLayoutBounds().getHeight() - 50.0);
        });

        // Processing when the mouse is dragged.
        this.areaChartLowerID.setOnMouseDragged(event -> {
            this.rectangle.setWidth(Math.abs(event.getX() + this.CORRECT_MOUSE_POSITION - mouseX[0]));
            this.rectangle.setX(Math.min(mouseX[0], event.getX() + this.CORRECT_MOUSE_POSITION));
        });

        // Processing when the mouse is released.
        this.areaChartLowerID.setOnMouseReleased(event -> {
            Axis<Number> xAxis = this.areaChartLowerID.getXAxis();
            Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
            double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
            int xEnd = xAxis.getValueForDisplay(x).intValue();

            // If the mouse is dragged in the opposite direction.
            if (xEnd + this.MIN_WIDTH < this.xStart) {
                int xtmp = xEnd;
                xEnd = this.xStart;
                this.xStart = xtmp;
            }
            this.xStart = Math.max(this.xStart, 0);

            if (xEnd > this.xStart + this.MIN_WIDTH) {
                this.radioXAutoID.setSelected(false);
                this.radioXSpecifyID.setSelected(true);
                this.xAxisFromID.setText(String.valueOf(this.xStart));
                this.xAxisToID.setText(String.valueOf(xEnd));
                redrawAction();
            }

            this.mainPaneID.getChildren().remove(this.rectangle);
        });
    }

    //========================================================================//
    // Draw graph [On Action]
    //========================================================================//

    /**
     * Change RadioButton in Y-axis (upper) to specify.
     */
    @FXML
    private void yupperAxisClicked() {
        this.radioYupperSpecifyID.setSelected(true);
    }

    /**
     * Change RadioButton in Y-axis (lower) to specify.
     */
    @FXML
    private void ylowerAxisClicked() {
        this.radioYlowerSpecifyID.setSelected(true);
    }

    /**
     * Change RadioButton in X-axis to specify.
     */
    @FXML
    private void xAxisClicked() {
        this.radioXSpecifyID.setSelected(true);
    }

    /**
     * Change the color of the mutant label.
     */
    @FXML
    private void colorPickerMutantAction() {
        this.mutantLabelID.setTextFill(this.colorPickerMutantID.getValue());
        this.redraw();
    }

    /**
     * Change the color of the wild type label.
     */
    @FXML
    private void colorPickerWildTypeAction() {
        this.wildTypeLabelID.setTextFill(this.colorPickerWildTypeID.getValue());
        this.redraw();
    }

    /**
     * Change the color of the significant label.
     */
    @FXML
    private void colorPickerSignificantAction() {
        this.significantLabelID.setTextFill(this.colorPickerSignificantID.getValue());
        this.redraw();
    }

    /**
     * Change the color of the not significant label.
     */
    @FXML
    private void colorPickerNotSignificantAction() {
        this.notSignificantLabelID.setTextFill(this.colorPickerNotSignificantID.getValue());
        this.redraw();
    }

    /**
     * Change the color of the threshold label.
     */
    @FXML
    private void colorPickerThresholdAction() {
        this.thresholdLabelID.setTextFill(this.colorPickerThresholdID.getValue());
        this.redraw();
    }

    /**
     * Redraw the graph.
     */
    @FXML
    public void redrawAction() {
        if (this.checkDrawData()) {        // check input data
            this.clearGraphAction();

            // stroke-width
            this.lineChartUpperID.getStyleClass().add("thick-chart");
            this.areaChartLowerID.getStyleClass().add("thick-chart");

            // X-axis
            this.xupperAxis.setLabel(this.xAxisTitleID.getText());
            this.xlowerAxis.setLabel(this.xAxisTitleID.getText());
            if (this.radioXAutoID.isSelected()) {
                this.xupperAxis.setAutoRanging(true);
                this.xlowerAxis.setAutoRanging(true);
            } else {
                this.xupperAxis.setAutoRanging(false);
                this.xlowerAxis.setAutoRanging(false);

                int xAxisMin = Integer.parseInt(this.xAxisFromID.getText());
                int xAxisMax = Integer.parseInt(this.xAxisToID.getText());

                this.xupperAxis.setLowerBound(xAxisMin);
                this.xupperAxis.setUpperBound(xAxisMax);

                this.xlowerAxis.setLowerBound(xAxisMin);
                this.xlowerAxis.setUpperBound(xAxisMax);
            }

            // Y-axis (upper)
            String ytitle = this.yupperAxisTitleID.getText();
            this.yupperAxis.setLabel(ytitle);
            if (this.radioYupperAutoID.isSelected()) {
                this.yupperAxis.setAutoRanging(true);
            } else {
                this.yupperAxis.setAutoRanging(false);
                this.yupperAxis.setLowerBound(Integer.parseInt(this.yupperAxisFromID.getText()));
                this.yupperAxis.setUpperBound(Integer.parseInt(this.yupperAxisToID.getText()));
            }

            // Y-axis (lower)
            this.ylowerAxis.setLabel(this.ylowerAxisTitleID.getValue());
            if (this.radioYlowerAutoID.isSelected()) {
                this.ylowerAxis.setAutoRanging(true);
            } else {
                this.ylowerAxis.setAutoRanging(false);
                this.ylowerAxis.setLowerBound(Integer.parseInt(this.ylowerAxisFromID.getText()));
                this.ylowerAxis.setUpperBound(Integer.parseInt(this.ylowerAxisToID.getText()));
            }

            if (this.radioYlowerAutoID.isSelected()) {
                this.ylowerAxis.setAutoRanging(true);
            } else {
                this.ylowerAxis.setAutoRanging(false);
                this.ylowerAxis.setLowerBound(Integer.parseInt(this.ylowerAxisFromID.getText()));
                this.ylowerAxis.setUpperBound(Integer.parseInt(this.ylowerAxisToID.getText()));
            }

            // Rotate yticks
            this.checkRotateYticksAction();

            // Line color
            String mutantColorHex = "#" + this.colorPickerMutantID.getValue().toString().substring(2);
            String wildTypeColorHex = "#" + this.colorPickerWildTypeID.getValue().toString().substring(2);
            String significantColorHex = "#" + this.colorPickerSignificantID.getValue().toString().substring(2);
            String notSignificantColorHex = "#" + this.colorPickerNotSignificantID.getValue().toString().substring(2);
            String thresholdColorHex = "#" + this.colorPickerThresholdID.getValue().toString().substring(2);

            // Threshold value
            double tvalue = Double.parseDouble(this.thresholdValueID.getText());

            // Draw graph
            final DrawGraph drawGraph = new DrawGraph(this.statisticsFileID, this.lineChartUpperID, this.areaChartLowerID,
                    mutantColorHex, wildTypeColorHex, significantColorHex, notSignificantColorHex, thresholdColorHex,
                    tvalue);

            if (!drawGraph.drawStatistics(this.ylowerAxisTitleID.getSelectionModel().getSelectedIndex())) {
                String errorMessage = "The format of statistics.txt file did not match.";
                new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
            }
        }
    }

    /**
     * Switches the rotation angle of the Y-axis memory.
     */
    @FXML
    private void checkRotateYticksAction() {
        if (this.checkRotateYticksID.isSelected()) {
            this.lineChartUpperID.getYAxis().setTickLabelRotation(270);
            this.areaChartLowerID.getYAxis().setTickLabelRotation(270);
        } else {
            this.lineChartUpperID.getYAxis().setTickLabelRotation(0);
            this.areaChartLowerID.getYAxis().setTickLabelRotation(0);
        }
    }

    /**
     * Select PDF or PNG format to save the graph to a file.
     */
    @FXML
    private void saveGraphAction() {
        File file = CommonTools.saveImage(this.mainPaneID);
        if (file == null) {
            return;
        }

        if (file.getPath().endsWith(".png")) {
            boolean test = this.statisticsFileID.getText().endsWith("statistics.txt");
            WritableImage snapShot = test ? this.vBoxChartID.snapshot(new SnapshotParameters(), null) :
                    this.lineChartUpperID.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null), "png", file);
            } catch (IOException e) {
                String errorMessage = "Could not create file (" + file.getPath() + ").";
                new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
            }
        } else if (file.getPath().endsWith(".pdf")) {
            this.saveVectorPdf(file);
        }
    }

    /**
     * Clear the graph drawing screen.
     */
    @FXML
    private void clearGraphAction() {
        this.lineChartUpperID.getData().clear();
        this.areaChartLowerID.getData().clear();
    }

    /**
     * Select the statistics.txt file.
     */
    @FXML
    private void selectStatisticsFileAction() {
        TextField inputTextFile = new TextField();
        CommonTools.selectStatistics(inputTextFile, this.mainPaneID);
        if (!inputTextFile.getText().isEmpty()) {
            String statistics = inputTextFile.getText();
            this.statisticsFileID.setText(statistics);
            this.redrawAction();
        }
    }

    /**
     * Open Alignment dialog.
     */
    @FXML
    private void outsideFileAction() {
        new AlignmentDialogueController(this.mainPaneID);
    }

    /**
     * Select the outside.txt file.
     */
    @FXML
    private void selectOutsideFileAction() {
        TextField inputTextFile = new TextField();
        CommonTools.selectOutside(inputTextFile, this.mainPaneID);
        if (!inputTextFile.getText().isEmpty()) {
            String outside = inputTextFile.getText();
            this.outsideFileID.setText(outside);
        }
    }

    //========================================================================//
    // Private function
    //========================================================================//

    private void redraw() {
        if (this.statisticsFileID != null && !this.statisticsFileID.getText().isEmpty()) {
            this.redrawAction();
        }
    }

    /**
     * Check the data for graphing.
     *
     * @return true:no problem, false:problem occurrence
     */
    private boolean checkDrawData() {
        if (!CommonTools.checkInputText(this.statisticsFileID)) {
            String errorMessage = "Statistics file was not specified.";
            new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
            return false;
        }

        if (this.radioXSpecifyID.isSelected()) {
            if (!this.checkSpecifiedAxis(this.xAxisLabelID, this.xAxisFromID, this.xAxisToID)) {
                return false;
            }
        }

        if (this.radioYupperSpecifyID.isSelected()) {
            if (!this.checkSpecifiedAxis(this.yupperAxisLabelID, this.yupperAxisFromID, this.yupperAxisToID)) {
                return false;
            }
        }

        if (this.radioYlowerSpecifyID.isSelected()) {
            return this.checkSpecifiedAxis(this.ylowerAxisLabelID, this.ylowerAxisFromID, this.ylowerAxisToID);
        }

        return true;
    }

    /**
     * Check the range of the axes of the graph.
     *
     * @param axisLabel axis title
     * @param fromPos   axis minimum
     * @param toPos     axis maximum
     * @return true:no problem, false:problem occurrence
     */
    private boolean checkSpecifiedAxis(Label axisLabel, TextField fromPos, TextField toPos) {
        String axis = axisLabel.getText();
        if (!CommonTools.checkInputText(fromPos) || !CommonTools.checkInputText(toPos)) {
            String errorMessage = "A range of " + axis + " was not specified.";
            new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
            return false;
        }

        if (Double.parseDouble(fromPos.getText()) >= Double.parseDouble(toPos.getText())) {
            String errorMessage = "The end position of " + axis + " must be greater than start one.";
            new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
            return false;
        }

        return true;
    }

    /**
     * Create a scalable PDF file.
     *
     * @param file PDF file to be created
     */
    private void saveVectorPdf(File file) {
        Document doc = SVGDOMImplementation.getDOMImplementation()
                .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        try {
            File svgFile = File.createTempFile("kmer", ".svg");
            Writer writer = new BufferedWriter(new FileWriter(svgFile));
            TranscoderOutput output = new TranscoderOutput(writer);

            Bounds bounds = this.vBoxChartID.getBoundsInLocal();
            Rectangle2D rec = new Rectangle2D.Double(bounds.getMinX(), bounds.getMinY(),
                    bounds.getWidth(), bounds.getHeight());

            SVGGraphics2D g2D = new ConvertorSVGGraphics2D(doc);
            JFXConverter converter = new JFXConverter();
            converter.convert(g2D, this.vBoxChartID);

            // Get the root element and add size
            String minX = Double.toString(rec.getMinX());
            String minY = Double.toString(rec.getMinY());
            String width = Double.toString(rec.getWidth() * 1.05);
            String height = Double.toString(rec.getHeight() * 1.03);
            String size = minX + " " + minY + " " + width + " " + height;

            Element svgRoot = g2D.getRoot();
            svgRoot.setAttributeNS(null, "viewBox", size);

            Writer wr = output.getWriter();
            g2D.stream(svgRoot, wr);
            writer.flush();

            TranscoderInput transcoderInput = new TranscoderInput(new FileInputStream(svgFile.getPath()));
            OutputStream outputStream = new FileOutputStream(file.getPath());
            TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);

            PDFTranscoder pdfTranscoder = new PDFTranscoder();
            pdfTranscoder.addTranscodingHint(PDFTranscoder.KEY_WIDTH, (float) rec.getWidth());
            pdfTranscoder.addTranscodingHint(PDFTranscoder.KEY_HEIGHT, (float) rec.getHeight());
            pdfTranscoder.transcode(transcoderInput, transcoderOutput);

            outputStream.flush();
            outputStream.close();
        } catch (DOMException | IOException | TranscoderException e) {
            String errorMessage = "Could not create file (" + file.getPath() + ").";
            new ErrorDialogueController(errorMessage, "red", this.mainPaneID);
        }
    }
}
