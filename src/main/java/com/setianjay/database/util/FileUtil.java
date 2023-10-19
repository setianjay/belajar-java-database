package com.setianjay.database.util;

public class FileUtil {

    private FileUtil(){

    }

    public static String getExtensionFile(String fileName){
        String[] fileSeparated = fileName.split("\\.");
        return fileSeparated[fileSeparated.length - 1];
    }
}
