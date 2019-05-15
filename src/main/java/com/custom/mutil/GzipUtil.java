package com.custom.mutil;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class GzipUtil {

    private static final Logger logger = LoggerFactory.getLogger(GzipUtil.class);
    //
    public static File compress(File source, File target) {
        FileInputStream in = null;
        GZIPOutputStream out = null;
        try {
            in = new FileInputStream(source);
            out = new GZIPOutputStream(new FileOutputStream(target));
            byte[] array = new byte[1024];
            int number = -1;
            while ((number = in.read(array, 0, array.length)) != -1) {
                out.write(array, 0, number);
            }
        } catch (Exception e) {
            logger.error("",e);
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                logger.error("",e);
                return null;
            }
        }
        return target;
    }

    public static File compress(List<File> sources, File target) {
        FileOutputStream out = null;
        TarArchiveOutputStream os = null;
        List<FileInputStream> fisList = new ArrayList<FileInputStream>();
        try {
            out = new FileOutputStream(target);
            os = new TarArchiveOutputStream(out);
            for (File file : sources) {
                os.putArchiveEntry(new TarArchiveEntry(file, file.getName()));
                FileInputStream fis = new FileInputStream(file);
                fisList.add(fis);
                IOUtils.copy(fis, os);
                os.closeArchiveEntry();
            }
        } catch (Exception e) {
            logger.error("",e);
            return null;
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
                for (FileInputStream fis : fisList) {
                    fis.close();
                }
            } catch (IOException e) {
                logger.error("",e);
                return null;
            }
        }
        return target;
    }
}
