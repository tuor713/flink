package org.uwh;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class TracePrint {
    public String id;
    public String cusip;
    public LocalDate tradeDate;
    public double quantity;
    public LocalDate cancelDate;
    public String cancelId;

    public static TracePrint fromCsvLine(String line) {
        TracePrint print = new TracePrint();
        List<String> parts = Arrays.asList(line.split(","));

        print.id = parts.get(0);
        print.cusip = parts.get(1);
        print.quantity = Double.valueOf(parts.get(2));
        print.tradeDate = LocalDate.from(DateTimeFormatter.ofPattern("yyyy.MM.dd").parse(parts.get(3)));

        print.cancelId = null;
        print.cancelDate = null;

        if (parts.size() >= 5) {
            print.cancelId = parts.get(5);
            if (!"".equals(parts.get(4))) {
                print.cancelDate = LocalDate.from(DateTimeFormatter.ofPattern("yyyy.MM.dd").parse(parts.get(4)));
            }
        }

        return print;
    }

    @Override
    public String toString() {
        return "TracePrint{" +
                "id='" + id + '\'' +
                ", cusip='" + cusip + '\'' +
                ", tradeDate=" + tradeDate +
                ", quantity=" + quantity +
                ", cancelDate=" + cancelDate +
                ", cancelId='" + cancelId + '\'' +
                '}';
    }
}
