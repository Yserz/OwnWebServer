package com.own.webserver;

import com.own.webserver.watcher.PropertyFileWatcher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.nio.file.StandardWatchEventKinds.*;
import java.util.Properties;

/**
 *
 * @author MacYser
 */
public class OwnWebServer implements Runnable {

	private ServerSocket serverSocket = null;
	private boolean running = true;
	private ExecutorService executor;

	public OwnWebServer() {
	}

	public void start(Properties settings) throws IOException {
		System.out.println("Starting OwnWebServer");
		serverSocket = new ServerSocket(Integer.parseInt(settings.getProperty("webserver.port", "8080")));
		System.out.println("HTTP| Server started on port: " + Integer.parseInt(settings.getProperty("webserver.port", "8080")));
		executor = Executors.newCachedThreadPool();
		running = true;
		executor.execute(this);
	}

	@Override
	public void run() {
		while (running) {
			try {
				Socket s = serverSocket.accept();
				executor.execute(new RequestHandler(s));
			} catch (IOException ex) {
				Logger.getLogger(OwnWebServer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public void stop() throws InterruptedException, IOException {
		System.out.println("Stopping OwnWebServer");
		running = false;
		serverSocket.close();
		executor.shutdownNow();
		executor.awaitTermination(5, TimeUnit.SECONDS);
		executor = null;
	}

	public void restart(Properties settings) throws IOException, InterruptedException {
		System.out.println("Restarting OwnWebServer");
		stop();
		start(settings);
	}
}
