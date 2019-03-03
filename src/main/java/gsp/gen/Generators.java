package gsp.gen;

import gsp.BuySell;
import gsp.FirmAccount;
import gsp.Offering;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Generators {
    public static Stream<FirmAccount> generateFirmAccounts(int n) {
        return IntStream.rangeClosed(1, n).mapToObj(i -> {
            String mnemonic = "FIRM"+i;
            String strategy = String.valueOf(i % 100);
            String goc = "GOC" + String.valueOf(i % 10);
            return new FirmAccount(mnemonic, "001", strategy, goc);
        });
    }

    public static String generateCUSIP(Random r) {
        int i = r.nextInt(10000);
        return String.format("%04d", i);
    }

    public static double generatePrice(Random r) {
        return 95.0+10*r.nextDouble();
    }

    public static Offering generateOffering(Random r, String venue) {
        return new Offering(generateCUSIP(r), venue, generatePrice(r), 10000.0, r.nextBoolean() ? BuySell.BUY : BuySell.SELL);
    }
}
