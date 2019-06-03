package com.custom.mutil;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUtil {

    /**
     * 读取properties文件
     */
    public static Properties loadProperties(File file) throws Exception {
        Properties p = new Properties();
        try (FileInputStream fin = new FileInputStream(file);
             InputStream in = new BufferedInputStream(fin)
        ) {
            p.load(in);
        }
        return p;
    }

    /**
     * 读取目录下的所有properties文件
     */
    public static Map<String, Properties> loadProperties(String directory) throws Exception {
        Map<String, Properties> proMap = new HashMap<>();
        List<String> paths = getAllPath(directory);
        for (String dirPath : paths) {
            Path path = Paths.get(dirPath);
            try (DirectoryStream<Path> fileStream = Files.newDirectoryStream(path, "*.properties")) {
                for (Path filePath : fileStream) {
                    File file = filePath.toFile();
                    String fileName = file.getName();
                    Properties properties = loadProperties(file);
                    proMap.put(fileName, properties);
                }
            }
        }
        return proMap;
    }

    /**
     * 读取文件内容
     */
    public static String readFile(String filePath) throws Exception {
        File file = new File(filePath);
        return readFile(file);
    }

    /**
     * 读取文件内容
     */
    public static String readFile(File file) throws Exception {
        try (ByteArrayOutputStream rst = new ByteArrayOutputStream(); InputStream in = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                rst.write(buffer, 0, length);
            }
            return rst.toString("UTF-8");
        }
    }

    public static List<String> readFileToList(String filePath) throws Exception {
        File file = new File(filePath);
        return readFileToList(file);
    }

    public static List<String> readFileToList(File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
             BufferedReader br = new BufferedReader(isr)
        ) {
            List<String> results = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                results.add(line);
            }
            return results;
        }
    }

    public static void writeFile(String filePath, String value) throws Exception {
        File file = new File(filePath);
        writeFile(file, value);
    }

    public static void writeFile(File file, String value) throws Exception {
        try (OutputStream out = new FileOutputStream(file)) {
            out.write(value.getBytes());
            out.flush();
        }
    }

    private static List<String> getAllPath(String directory) throws Exception {
        File path = new File(directory);
        if (!path.exists()) {
            throw new RuntimeException(directory + " does not exists.");
        }
        //
        List<String> paths = new ArrayList<>();
        paths.add(directory);
        getAllPath(paths, path);
        return paths;
    }

    private static void getAllPath(List<String> paths, File path) throws Exception {
        File[] files = path.listFiles();
        if (null == files || files.length == 0) {
            return;
        }
        //
        for (File childFile : files) {
            if (childFile.isDirectory()) {
                paths.add(childFile.getPath());
                getAllPath(paths, childFile);
            }
        }
    }

    public static void copyFile(File src, File target) throws Exception {
        try (FileInputStream fis = new FileInputStream(src);
             FileOutputStream fout = new FileOutputStream(target)) {
            byte[] data = new byte[1024];
            int rs = -1;
            while ((rs = fis.read(data)) > 0) {
                fout.write(data, 0, rs);
            }
        }
    }


    public static void main(String[] args) {
        try {
            File file = new File("D:\\test.txt");
            //FileUtil.copyFile(file, new File(file.getAbsolutePath() + "_backup_" + DateUtil.format_yyyyMMddHHmmss(new Date())));
            System.out.println("path:" + file.getPath());
            System.out.println("absPath:" + file.getAbsolutePath());
            System.out.println("cannoPath:" + file.getCanonicalPath());
            System.out.println("parent:" + file.getParent());
            System.out.println("exist:" + file.exists());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
