package CoffeeShop;

import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private static Logger instance;

    private Logger() {
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return Logger.instance;
    }

    public void log(String message) {
        System.out.println(message);

        try (FileWriter writer = new FileWriter("app.log", true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
