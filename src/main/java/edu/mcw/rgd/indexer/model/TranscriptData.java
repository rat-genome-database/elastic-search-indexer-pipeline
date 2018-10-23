package edu.mcw.rgd.indexer.model;

/**
 * Created by jthota on 2/3/2017.
 */
public class TranscriptData {
    private int transcript_id;
    private String tr_acc_id;
    private String protein_acc_id;

    public int getTranscript_id() {
        return transcript_id;
    }

    public void setTranscript_id(int transcript_id) {
        this.transcript_id = transcript_id;
    }

    public String getTr_acc_id() {
        return tr_acc_id;
    }

    public void setTr_acc_id(String tr_acc_id) {
        this.tr_acc_id = tr_acc_id;
    }

    public String getProtein_acc_id() {
        return protein_acc_id;
    }

    public void setProtein_acc_id(String protein_acc_id) {
        this.protein_acc_id = protein_acc_id;
    }
}
