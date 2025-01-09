/*
 * Name        : GenEditScan-GUI
 * Author      : NARO
 * Version     : 202412
 * Copyright   : (c) 2019 National Agriculture and Food Research Organization (NARO)
 * Description : K-mer analysis tool
 */
package GenEditScan;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Locale;

/**
 * Main class.
 *
 * @author NARO
 */
public class Main extends Application {
    //========================================================================//
    // Public function
    //========================================================================//

    /**
     * Start program.
     *
     * @param primaryStage primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        // Set locale
        Locale.setDefault(Locale.ENGLISH);

        // Main screen
        new MainScreenController(primaryStage);
    }

    /**
     * Main function.
     *
     * @param args arguments of the main function (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
