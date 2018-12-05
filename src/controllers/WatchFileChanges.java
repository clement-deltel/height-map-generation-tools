package controllers;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class WatchFileChanges {

	final boolean NOT_SUPPORTED_WATCH_FILE;
	
    Path watchPath = Paths.get(LoggerAlgorithm.fileLogName);

    TextArea textArea;

    WatchFileChanges(Path p){
    	this.watchPath = p;
    	System.out.println(p);
    	if (System.getProperty("os.name").contains("Linux")) {
    		FXMLController.alertDialog("Feature not supported", "This feature is not supported yet.", "It seems that you are currently using a Linux machine that does not support this feature yet.\n\nOur team is actually working on its implementation.", AlertType.INFORMATION);
    	}
    	NOT_SUPPORTED_WATCH_FILE = System.getProperty("os.name").contains("Linux");
    }
    
    public void start() {
    	//if (NOT_SUPPORTED_WATCH_FILE)
    	//	return;
        BorderPane root = new BorderPane();

        textArea = new TextArea();
        root.setCenter(textArea);

        Scene scene = new Scene(root, 800, 600);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Algorithm Logs ("+ watchPath +")");
        stage.setResizable(true);
        stage.getIcons().add(new Image("/images/firstheightmap.jpg"));
        stage.show();

        // load file initally
        if (Files.exists(watchPath)) {
            loadFile();
        }

        // watch file
        WatchThread watchThread = new WatchThread(watchPath);
        watchThread.setDaemon( true);
        watchThread.start();

    }

    private void loadFile() {

        try {

            String stringFromFile = Files.lines(watchPath).collect(Collectors.joining("\n"));
            textArea.setText(stringFromFile);
            textArea.setScrollTop(Double.MAX_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class WatchThread extends Thread {

        Path watchPath;

        public WatchThread(Path watchPath) {
            this.watchPath = watchPath;
        }

        public void run() {

            try {

                WatchService watcher = FileSystems.getDefault().newWatchService();
                WatchKey key = watchPath.getParent().register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

                while (true) {

                    // wait for key to be signaled
                    try {
                        key = watcher.take();
                    } catch (InterruptedException x) {
                        return;
                    }

                    for (WatchEvent<?> event : key.pollEvents()) {

                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        @SuppressWarnings("unchecked")
						WatchEvent<Path> ev = (WatchEvent<Path>) event;

                        Path path = ev.context();

                        if (!path.getFileName().equals(watchPath.getFileName())) {
                            continue;
                        }

                        // process file
                        Platform.runLater(() -> {
                            loadFile();
                        });

                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }

                }
            } catch (IOException x) {
                System.err.println(x);
            }
        }
    }
}