package com.happygo.springrefresh.monitor;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: FileMonitor <br/>
 * Description: 用于观察某个目录中的文件是否发生删除或者修改创建 <br/>
 * Date: 2017/11/28 10:49 <br/>
 *
 * @author sxp(sxp 1378127237 qq.com)<br>
 * @version 1.0 <br/>
 */
public class FileMonitor {
    /**
     * The RefreshScope.
     */
    private FileAlterationMonitor monitor;
    /**
     * The Interval time.
     */
    private long intervalTime = 60000L;

    /**
     * Instantiates a new File monitor.
     */
    public FileMonitor() {
        monitor = new FileAlterationMonitor(intervalTime);
    }

    /**
     * Instantiates a new File monitor.
     *
     * @param interval the interval
     * @param timeUnit the time unit
     */
    public FileMonitor(long interval, TimeUnit timeUnit) {
        interval = timeUnit.toMillis(interval);
        this.intervalTime = interval;
        monitor = new FileAlterationMonitor(intervalTime);
    }

    /**
     * Monitor.
     *
     * @param path        the path
     * @param listener    the listener
     * @param filterFiles the filter files
     */
    public void monitor(String path, FileAlterationListener listener, List<String> filterFiles) {
        IOFileFilter ioFileFilter = new NameFileFilter(filterFiles.toArray(new String[filterFiles.size()]));
        FileAlterationObserver observer = new FileAlterationObserver(new File(path), ioFileFilter);
        monitor.addObserver(observer);
        observer.addListener(listener);
    }

    /**
     * Stop.
     *
     * @throws Exception the exception
     */
    public void stop() throws Exception{
        monitor.stop();
    }

    /**
     * Start.
     *
     * @throws Exception the exception
     */
    public void start() throws Exception {
        monitor.start();
    }
}
