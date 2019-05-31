package com.evranger.soulevspy.advisor;

import org.json.JSONObject;

public class ChargeLocation {
    private double m_distFromLookupPos;
    private Pos m_pos;
    private double m_maxEffect;
    private String m_readableName;
    private JSONObject m_origJson;

    public ChargeLocation(double dist, Pos pos, double maxEffect, String readableName, JSONObject origJson) {
        m_distFromLookupPos = dist;
        m_pos = pos;
        m_maxEffect = maxEffect;
        m_readableName = readableName;
        m_origJson = origJson;
    }

    public double get_distFromLookupPos() {
        return m_distFromLookupPos;
    }

    public void set_distFromLookupPos(double m_distFromLookupPos) {
        this.m_distFromLookupPos = m_distFromLookupPos;
    }

    public Pos get_pos() {
        return m_pos;
    }

    public double get_maxEffect() {
        return m_maxEffect;
    }

    public String get_readableName() {
        return m_readableName;
    }

    public JSONObject get_origJson() {
        return m_origJson;
    }
}
