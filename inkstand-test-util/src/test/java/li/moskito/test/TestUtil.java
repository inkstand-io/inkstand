package li.moskito.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public final class TestUtil {

    private TestUtil() {
    }

    /**
     * Generates a random run id.
     * 
     * @return
     */
    public static String generateRunId() {
        final String ts = new SimpleDateFormat("YYYYMMdd-mm-ss").format(new Date());
        final String runID = ts + "_" + Math.abs(new Random(System.currentTimeMillis()).nextInt()) + "";
        return runID;
    }
}
