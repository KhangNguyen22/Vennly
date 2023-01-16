package comp5216.sydney.edu.au.vennly;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Class to estimate the size of an object. Purpose is to just to test
 * effectiveness of compression
 */
public class SizeEstimator {
    public static int estimateSize(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(baos);
            objectOut.writeObject(object);
            objectOut.close();
            byte[] bytes =  baos.toByteArray();
            return bytes.length;
        } catch (IOException e) {
            Log.e("game state message","Compress failed");
            return 0;
        }
    }

}
