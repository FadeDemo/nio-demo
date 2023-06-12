package org.fade.demo.niodemo.jdknio;

import com.sun.nio.file.ExtendedWatchEventModifier;

import java.io.IOException;
import java.nio.file.*;

/**
 * {@link java.nio.file.WatchService} 示例
 * @author fade
 * @see java.nio.file.Watchable
 * @see java.nio.file.WatchEvent
 * @see java.nio.file.WatchKey
 */
public class WatchServiceExample {

    public static void main(String[] args) {
        try {
            // 创建 WatchService 对象
            WatchService watchService = FileSystems.getDefault().newWatchService();
            // 注册要监视的目录
            Path directory = Paths.get("path/to/directory");
            directory.register(watchService, new WatchEvent.Kind[]{
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE },
                    ExtendedWatchEventModifier.FILE_TREE);
            // 处理事件
            while (true) {
                WatchKey watchKey = watchService.take();
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    // 处理事件
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("File created: " + event.context());
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("File modified: " + event.context());
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("File deleted: " + event.context());
                    }
                }
                watchKey.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
