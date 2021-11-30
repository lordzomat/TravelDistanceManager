package de.lordz.java.tools.tdm;

public enum TraceLevel {
    Verbose(4), Information(3), Warning(2), Error(1), ALL(0);

    private int numVal;

    TraceLevel(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}