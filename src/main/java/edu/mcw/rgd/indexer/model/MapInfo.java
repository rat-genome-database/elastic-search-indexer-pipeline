package edu.mcw.rgd.indexer.model;

/**
 * Created by jthota on 2/3/2017.
 */
public class MapInfo {
    private String map;
    private String chromosome;
    private long startPos;
    private long stopPos;
    private int rank;
    private int mapKey;

    public int getMapKey() {
        return mapKey;
    }

    public void setMapKey(int mapKey) {
        this.mapKey = mapKey;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getStopPos() {
        return stopPos;
    }

    public void setStopPos(long stopPos) {
        this.stopPos = stopPos;
    }

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }
}
