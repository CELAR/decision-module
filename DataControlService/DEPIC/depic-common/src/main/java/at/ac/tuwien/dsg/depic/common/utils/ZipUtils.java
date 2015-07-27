/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.utils;

/**
 *
 * @author Jun
 */
import at.ac.tuwien.dsg.depic.common.utils.Logger;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private String parentDir;

    public void zipDir(String zipFileName, String dir) {
        File dirObj = new File(dir);

        parentDir = dirObj.getParent();
     //   Logger.logInfo(" DIR: " + parentDir);

        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
            Logger.logInfo("Creating : " + zipFileName);
            addDir(dirObj, out);
            out.close();
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    private void addDir(File dirObj, ZipOutputStream out) {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[1024];

        try {

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    addDir(files[i], out);
                    continue;
                }
                FileInputStream in = new FileInputStream(files[i].getAbsolutePath());

                out.putNextEntry(new ZipEntry(files[i].getAbsolutePath().replaceAll(parentDir, "")));
                int len;
                while ((len = in.read(tmpBuf)) > 0) {
                    out.write(tmpBuf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    List<String> fileList;

    /**
     * Unzip it
     *
     * @param zipFile input zip file
     * @param output zip file output folder
     */
    public void unZipIt(String zipFile) {

        Logger.logInfo(zipFile);
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = null;
        try {
            zip = new ZipFile(file);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        
        
        //String newPath = zipFile.substring(0, zipFile.length() - 4);

        String newPath = file.getParent();
        
        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            //destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            if (!entry.isDirectory()) {
                BufferedInputStream is = null;
                try {
                    is = new BufferedInputStream(zip
                            .getInputStream(entry));
                } catch (IOException ex) {
                    System.err.println(ex);
                }
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(destFile);
                } catch (FileNotFoundException ex) {
                    System.err.println(ex);
                }
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        BUFFER);

                try {
                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                } catch (IOException ex) {
                   System.err.println(ex);
                }
                try {
                    dest.flush();
                } catch (IOException ex) {
                    System.err.println(ex);
                }
                try {
                    dest.close();
                } catch (IOException ex) {
                    System.err.println(ex);
                }
                try {
                    is.close();
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }

        }
    }

}
