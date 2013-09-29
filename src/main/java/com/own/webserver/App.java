package com.own.webserver;

import com.own.webserver.watcher.PropertyFileWatcher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App {

	private OwnWebServer server;
	private ExecutorService watchExecutor;
	private static final String PROP_PATH = "./settings.properties";

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("OwnWebServer!");
		App app = new App();
		app.server = new OwnWebServer();

		try {
			app.watchSettings();
			app.server.start(app.loadProps(PROP_PATH));
		} catch (IOException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
		}
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("Type \"exit\" to quit the server");
			String line = console.readLine();
			if (line.equals("exit")) {
				System.exit(0);
			}
			if (line.equals("restart")) {
				app.server.restart(app.loadProps(PROP_PATH));
			}
		}
	}

	private void watchSettings() {
		watchExecutor = Executors.newCachedThreadPool();
		watchExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					PropertyFileWatcher watcher = new PropertyFileWatcher();
					System.out.println("PATH: " + new File("").getAbsolutePath());
					WatchKey watchKey = watcher.watchDir("./");
					while (true) {
						List<WatchEvent<?>> events = watchKey.pollEvents();
						for (WatchEvent event : events) {
							if (event.kind() == ENTRY_CREATE) {
								System.out.println("Created: " + event.context().toString());
								if (event.context().toString().equals("settings.properties")) {
									server.restart(loadProps(PROP_PATH));
								}
							}
							if (event.kind() == ENTRY_DELETE) {
								System.out.println("Delete: " + event.context().toString());
							}
							if (event.kind() == ENTRY_MODIFY) {
								System.out.println("Modify: " + event.context().toString());
								if (event.context().toString().equals("settings.properties")) {
									server.restart(loadProps(PROP_PATH));
								}
							}
						}
						Thread.sleep(1000);
					}
				} catch (IOException | InterruptedException ex) {
					Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
	}

	private Properties loadProps(String path) {
		Properties prop = new Properties();
		try (InputStream in = new FileInputStream(path)) {
			prop.load(in);
		} catch (IOException ex) {
			Logger.getLogger(OwnWebServer.class.getName()).log(Level.SEVERE, null, ex);
		}
		return prop;
	}
}
