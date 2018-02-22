package com.happygo.springrefresh;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.happygo.springrefresh.annotation.RefreshScope;
import com.happygo.springrefresh.monitor.FileListener;
import com.happygo.springrefresh.monitor.FileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ContextFileListener <br/>
 * Description: ContextFileListener <br/>
 * Date: 2017/11/28 10:51 <br/>
 *
 * @author sxp(sxp 1378127237 qq.com)<br>
 * @version 1.0 <br/>
 */
public class ContextFileListener implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    /**
     * The constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ContextFileListener.class);

    /**
     * The Application context.
     */
    private ApplicationContext applicationContext;

    /**
     * The File monitor.
     */
    private FileMonitor fileMonitor;

    /**
     * The Environment.
     */
    private ConfigurableEnvironment environment;

    /**
     * Instantiates a new Context file monitor.
     */
    public ContextFileListener() {
    }

    /**
     * Instantiates a new Context file monitor.
     *
     * @param fileMonitor the file monitor
     * @param environment the environment
     */
    public ContextFileListener(FileMonitor fileMonitor, ConfigurableEnvironment environment) {
        this.fileMonitor = fileMonitor;
        this.environment = environment;
    }

    /**
     * Sets file monitor.
     *
     * @param fileMonitor the file monitor
     * @return the file monitor
     */
    public ContextFileListener setFileMonitor(FileMonitor fileMonitor) {
        this.fileMonitor = fileMonitor;
        return this;
    }

    /**
     * Sets environment.
     *
     * @param environment the environment
     * @return the environment
     */
    public ContextFileListener setEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;
        return this;
    }

    /**
     * On application event.
     *
     * @param event the event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            logger.info(">>>>>>>>初始化配置文件变化监听器");
            Map<String, Object> beansWithAnnotationMap = applicationContext.getBeansWithAnnotation(RefreshScope.class);
            Map<String, PropertySourceRefresh> beansWrapperMap = Maps.newHashMap();
            HashSet<String> hashSet = Sets.newLinkedHashSet();
            beansWithAnnotationMap.forEach((key, val) -> {
                RefreshScope refreshScope = AnnotationUtils.findAnnotation(val.getClass(), RefreshScope.class);
                hashSet.add(refreshScope.path());
                beansWrapperMap.put(refreshScope.file(), (PropertySourceRefresh) val);
            });
            hashSet.forEach(path -> {
                List<String> filterFiles = Lists.newArrayList();
                beansWithAnnotationMap.forEach((key, val) -> {
                    RefreshScope refreshScope = AnnotationUtils.findAnnotation(val.getClass(), RefreshScope.class);
                    if (path.equals(refreshScope.path())) {
                        filterFiles.add(refreshScope.file());
                    }
                });
                fileMonitor.monitor(getPath(path, filterFiles.get(0)), new FileListener(beansWrapperMap), filterFiles);
            });
            fileMonitor.start();
            logger.info(">>>>>>>>配置文件变化监听器初始化完成");
        } catch (Exception e) {
           logger.warn("配置文件变化监听器启动异常", e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            try {
                fileMonitor.stop();
            } catch (Exception e) {
                logger.warn("配置文件变化监听器关闭异常", e);
            }
        }));
    }

    /**
     * Gets path.
     *
     * @param path the path
     * @return the path
     */
    private String getPath(String path, String file) {
        String var0 = "";
        if (path.startsWith("${") && path.endsWith("}")) {
            String var1 = path.replace("${", "").replace("}", "");
            var0 = System.getProperty(var1);
            if (StringUtils.isEmpty(var0)) {
                var0 = environment.getProperty(var1);
            }
            if (StringUtils.isEmpty(var0)) {
                throw new IllegalArgumentException("The path [" + path + "] does not exist!");
            }
        } else {
            var0 = path;
        }
        //if var0 is empty, get classpath
        if (StringUtils.isEmpty(var0)) {
            try {
                Assert.notNull(file);
                String absolutePath = new ClassPathResource(file).getFile().getAbsolutePath();
                absolutePath = absolutePath.substring(0, absolutePath.length()-file.length());
                var0 = absolutePath;
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
        return var0;
    }

    /**
     * Sets application context.
     *
     * @param applicationContext the application context
     * @throws BeansException the beans exception
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.environment = (ConfigurableEnvironment) applicationContext.getEnvironment();
    }
}
