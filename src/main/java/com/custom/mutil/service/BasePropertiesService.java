package com.custom.mutil.service;

import com.custom.mutil.FileUtil;
import com.custom.mutil.StringUtil;

import java.util.*;

public class BasePropertiesService {

    private Set<String> pathSet = new HashSet<>();
    private Map<String, Properties> propertiesMap = new HashMap<>();

    static {
        BasePropertiesService baseService = new BasePropertiesService();
        baseService.startLoad();
    }

    /**
     * load the files in the directory
     *
     * @param directory properties file storage directory
     */
    protected void addDirectory(String directory) {
        pathSet.add(directory);
        try {
            loadDirectory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDirectory() throws Exception{
        for (String path : pathSet) {
            Map<String, Properties> propertiesMap = FileUtil.loadProperties(path);
            this.propertiesMap.putAll(propertiesMap);
        }
    }

    private void startLoad() {
        PropertiesLoadThread loadThread = new PropertiesLoadThread();
        loadThread.start();
    }

    protected String getString(String key, String defaultValue) {
        String value;
        if (null != propertiesMap) {
            for (Properties pro : propertiesMap.values()) {
                value = pro.getProperty(key);
                if (StringUtil.isNotEmpty(value)) {
                    return value.trim();
                }
            }
        }
        return defaultValue;
    }

    protected boolean getBoolean(String key, boolean defaultValue) {
        String value;
        if (null != propertiesMap) {
            for (Properties pro : propertiesMap.values()) {
                value = pro.getProperty(key);
                if (StringUtil.isNotEmpty(value)) {
                    return Boolean.valueOf(value);
                }
            }
        }
        return defaultValue;
    }

    protected Properties getProperties(String fileName) {
        if (null != propertiesMap) {
            return propertiesMap.get(fileName);
        }
        return null;
    }

    private class PropertiesLoadThread extends Thread {
        @Override
        public void run() {
            Thread.currentThread().setName("PropertiesLoadThread");
            while (true) {
                try {
                    loadDirectory();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //
                try {
                    Thread.sleep(30000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
