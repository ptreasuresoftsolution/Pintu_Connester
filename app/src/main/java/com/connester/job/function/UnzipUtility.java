package com.connester.job.function;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 *
 * @author www.codejava.net
 */
public class UnzipUtility {
    Status status;

    public UnzipUtility(Status status) {
        this.status = status;
    }

    public interface Status {
        public void complete();

        public void fail(Throwable throwable);

        public void process(int i);
    }

    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     *
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public void unzip(String zipFilePath, String destDirectory) {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        try {
            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry entry = zipIn.getNextEntry();

            // iterates over entries in the zip file
            int i = 0;
            while (entry != null) {
                long available = new FileInputStream(zipFilePath).available();
                i += entry.getSize();
                if (available != 0) {
                    int per = (int) ((i * 100) / available);
                    status.process(per);
                    Log.e("check", available + " <> " + per + " >> " + i);
                }
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    Log.e("CHECK ZIP FILE ", "YES");
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    Log.e("CHECK ZIP DIR ", "YES");
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
            status.complete();
        } catch (Exception e) {
            status.fail(e);
        }
    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}