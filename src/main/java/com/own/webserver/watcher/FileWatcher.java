package com.own.webserver.watcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 *
 * @author MacYser
 */
public abstract class FileWatcher {

	protected WatchService watchService;
	protected Path path;

	public FileWatcher() throws IOException {
		this.watchService = FileSystems.getDefault().newWatchService();
	}

	public WatchKey watchDir(String stringPath) throws IOException, InterruptedException {
		Path path = Paths.get(stringPath);

//		watchKey = watchService.take();

		return path.register(watchService,
				ENTRY_CREATE,
				ENTRY_DELETE,
				ENTRY_MODIFY);

//		List<WatchEvent<?>> events = watchKey.pollEvents();
//		for (WatchEvent event : events) {
//			if (event.kind() == ENTRY_CREATE) {
//				System.out.println("Created: " + event.context().toString());
//			}
//			if (event.kind() == ENTRY_DELETE) {
//				System.out.println("Delete: " + event.context().toString());
//			}
//			if (event.kind() == ENTRY_MODIFY) {
//				System.out.println("Modify: " + event.context().toString());
//			}
//		}
	}
}
