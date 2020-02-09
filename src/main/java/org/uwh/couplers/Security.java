package org.uwh.couplers;

import java.io.Serializable;

public class Security implements Serializable {
    public String smcp;
    public String cusip;
    public String isin;
    public boolean isActive;
    public int version;
}
