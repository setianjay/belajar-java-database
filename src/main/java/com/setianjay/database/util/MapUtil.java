package com.setianjay.database.util;

/**
 * create abstract class to handle common behavior
 * */
public class MapUtil {

    private MapUtil(){

    }

    public static int mapDoubleToInt(String s) {
        double holdValueInDouble = Double.parseDouble(s);
        return (int) holdValueInDouble;
    }
}
