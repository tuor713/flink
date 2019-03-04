package producer;

import gsp.BuySell;
import gsp.Offering;
import quickfix.FixVersions;
import quickfix.Message;
import quickfix.field.*;

/*
Simulate venue offerings coming through as Fix messages
 */
public class FixOfferingFactory {
    public static Message createQuoteMessage(String cusip, double quote, gsp.BuySell side, double size) {
        /* FIX spec may differ for venues, as an example see https://btobits.com/fixopaedia/fixdic44/message_Quote_S_.html */
        Message m = new Message();

        m.getHeader().setString(BeginString.FIELD, FixVersions.BEGINSTRING_FIX44);
        m.getHeader().setString(MsgType.FIELD, MsgType.QUOTE);
        m.getHeader().setString(SenderCompID.FIELD, "venue");
        m.getHeader().setString(TargetCompID.FIELD, "citi");

        m.setString(QuoteID.FIELD, "quoteid:"+cusip+ ":"+quote+":"+size);

        m.setInt(QuoteType.FIELD, QuoteType.INDICATIVE);
        m.setChar(Side.FIELD, (side == BuySell.BUY) ? Side.BUY : Side.SELL);

        m.setString(Symbol.FIELD, cusip);
        m.setString(SecurityID.FIELD, cusip);
        m.setString(SecurityIDSource.FIELD, "1");

        m.setDouble(Quantity.FIELD, size);

        if (side == BuySell.BUY) {
            m.setDouble(BidPx.FIELD, quote);
            m.setDouble(BidSize.FIELD, size);
        } else {
            m.setDouble(OfferPx.FIELD, quote);
            m.setDouble(OfferSize.FIELD, size);
        }

        return m;
    }

    public static Offering fixToOffering(Message m, String venue) throws Exception {
        if (!m.getHeader().getField(new MsgType()).getValue().equals(MsgType.QUOTE))
            return null;

        Offering res = new Offering();
        res.setCUSIP(m.getField(new SecurityID()).getValue());
        res.setVenue(venue);

        BuySell side = (m.getField(new Side()).getValue() == Side.BUY) ? BuySell.BUY : BuySell.SELL;
        res.setSide(side);
        if (side == BuySell.BUY) {
            res.setPrice(m.getField(new BidPx()).getValue());
            res.setSize(m.getField(new BidSize()).getValue());
        } else {
            res.setPrice(m.getField(new OfferPx()).getValue());
            res.setSize(m.getField(new OfferSize()).getValue());
        }

        return res;
    }

    public static void main(String[] args) throws Exception {
        Message m = createQuoteMessage("ABC123", 101.00, BuySell.BUY,50000);
        System.out.println(m);

        for (char c : m.toString().toCharArray()) {
            System.out.print(c);
            System.out.print("["+((int) c) + "] ");
        }
        System.out.println();

        // Roundtrip from toString, catches header problems etc
        Message m2 = new Message(m.toString());
        System.out.println(m2);

        // Convert to internal model
        System.out.println(fixToOffering(m2, "VENUE"));
    }
}
