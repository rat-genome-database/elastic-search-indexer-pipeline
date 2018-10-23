package edu.mcw.rgd.indexer.model;

/**
 * Created by jthota on 2/3/2017.
 */
public class MapInfo {
    private String map;
    private String chromosome;
    private long startPos;
    private long stopPos;

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
