/*
 * GenEditScan-GUI
 * Copyright 2019 National Agriculture and Food Research Organization (NARO)
 */
package GenEditScan;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * Controller's base class.
 *
 * @author NARO
 */
public class ControllerBase {
    //========================================================================//
    // Arguments from upper method
    //========================================================================//
    /**
     * User configuration class
     */
    protected UserConfiguration userConfiguration;

    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Controller's base class constructor.
     */
    public ControllerBase() {
    }

    /**
     * Set the user configuration file.
     *
     * @param uc User configuration class
     */
    public void setConfiguration(UserConfiguration uc) {
        this.userConfiguration = uc;
    }

    //========================================================================//
    // Protected function
    //========================================================================//

    /**
     * Set the string to Text field.
     *
     * @param tf Text field
     * @param s  string
     */
    protected void setTextField(TextField tf, String s) {
        if (s != null && !s.isEmpty()) {
            tf.setText(s);
        }
    }

    /**
     * Set the integer to Text field.
     *
     * @param tf Text field
     * @param i  integer
     */
    protected void setTextField(TextField tf, int i) {
        if (i > 0) {
            tf.setText(String.valueOf(i));
        }
    }

    /**
     * Set the double to Text field.
     *
     * @param tf Text field
     * @param d  double
     */
    protected void setTextField(TextField tf, double d) {
        if (d > 0.0) {
            tf.setText(String.valueOf(d));
        }
    }

    /**
     * Set the integer to Spinner.
     *
     * @param sp spinner
     * @param i  integer
     */
    protected void setSpinner(Spinner<Integer> sp, int i) {
        if (i > 0) {
            sp.getValueFactory().setValue(i);
        }
    }

    /**
     * Set the double to Spinner.
     *
     * @param sp spinner
     * @param d  double
     */
    protected void setSpinnerDouble(Spinner<Double> sp, double d) {
        if (d >= 0.0) {
            sp.getValueFactory().setValue(d);
        }
    }

    /**
     * Set the status to CheckBox.
     *
     * @param cb CheckBox
     * @param b  status
     */
    protected void setCheckBox(CheckBox cb, boolean b) {
        cb.setSelected(b);
    }

    /**
     * Set the value to ColorPicker.
     *
     * @param cp ColorPicker
     * @param s  value
     */
    protected void setColorPicker(ColorPicker cp, String s) {
        if (s != null && !s.isEmpty()) {
            cp.setValue(Color.valueOf(s));
        }
    }
}
