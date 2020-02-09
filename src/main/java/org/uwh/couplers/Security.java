package org.uwh.couplers;

import gsp.SecurityIdType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Security implements Serializable {
    public String smcp;
    public Map<SecurityIdType, String> xrefs = new HashMap<>();
    public boolean isOperational;
    public boolean isWhenIssued;
    public int version;
}
