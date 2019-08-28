package org.nlogo.extensions.sound;

public class Utils {

    static int findAbsMax(short[] x) {
        int maxi = 0;
        for (int i = 1; i < x.length; i++)
            if (Math.abs(x[i]) > Math.abs(x[maxi])) maxi = i;
        return maxi;
    }
}
