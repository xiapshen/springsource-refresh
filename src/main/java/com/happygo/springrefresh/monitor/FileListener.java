package com.happygo.springrefresh.monitor;

import com.happygo.springrefresh.PropertySourceRefresh;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * ClassName: FileListener <br/>
 * Description: 文件监听器，按照文件或者目录的不同行为调用不同的行为函数 <br/>
 * Date: 2017/11/28 10:50 <br/>
 *
 * @author sxp(sxp 1378127237 qq.com)<br>
 * @version 1.0 <br/>
 */
public class FileListener implements FileAlterationListener {

    /**
     * The constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(FileListener.class);

    /**
     * The Map.
     */
    private Map<String, PropertySourceRefresh> map;

    /**
     * Instantiates a new File monitor.
     *
     * @param map the map
     */
    public FileListener(Map<String, PropertySourceRefresh> map) {
        this.map = map;
    }

    /**
     * On start.
     *
     * @param observer the observer
     */
    @Override
    public void onStart(FileAlterationObserver observer) {
        //empty
    }

    /**
     * On directory create.
     *
     * @param directory the directory
     */
    @Override
    public void onDirectoryCreate(File directory) {
        logger.info(directory.getAbsolutePath() + " onDirectoryCreate()");
    }

    /**
     * On directory change.
     *
     * @param directory the directory
     */
    @Override
    public void onDirectoryChange(File directory) {
        logger.info(directory.getAbsolutePath() + " onDirectoryChange()");
    }

    /**
     * On directory delete.
     *
     * @param directory the directory
     */
    @Override
    public void onDirectoryDelete(File directory) {
        logger.info(directory.getAbsolutePath() + " onDirectoryDelete()");
    }

    /**
     * On file create.
     *
     * @param file the file
     */
    @Override
    public void onFileCreate(File file) {
        logger.info(file.getAbsolutePath() + " onFileCreate()");
    }

    /**
     * On file change.
     *
     * @param file the file
     */
    @Override
    public void onFileChange(File file) {
        logger.info(file.getAbsolutePath() + " onFileChange() ");
        map.forEach((fileName, propertySourceRefresh) -> {
            if (file.getName().equals(fileName)) {
                propertySourceRefresh.refresh();
            }
        });
    }

    /**
     * On file delete.
     *
     * @param file the file
     */
    @Override
    public void onFileDelete(File file) {
        logger.info(file.getAbsolutePath() + " onFileDelete()");
    }

    /**
     * On stop.
     *
     * @param observer the observer
     */
    @Override
    public void onStop(FileAlterationObserver observer) {
        //empty
    }
}