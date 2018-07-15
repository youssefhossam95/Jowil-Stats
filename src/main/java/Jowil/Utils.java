package Jowil;

import java.util.ArrayList;

public class Utils {
    public static  double getNumberWithinLimits(double number , double lowerLimit , double upperLimit) {
        if(number < lowerLimit)
            return lowerLimit;
        else  if (number > upperLimit)
            return upperLimit;
        else
            return number ;
    }

}
