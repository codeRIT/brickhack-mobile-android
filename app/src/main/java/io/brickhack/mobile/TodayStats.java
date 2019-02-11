package io.brickhack.mobile;

import com.google.gson.annotations.SerializedName;

public class TodayStats {

    @SerializedName("Applications")
    private int applications;

    @SerializedName("Confirmations")
    private int confirmations;

    @SerializedName("Denials")
    private int denials;


    public TodayStats(int applications, int confirmations, int denials){
        this.applications = applications;
        this.confirmations = confirmations;
        this.denials = denials;
    }

    public int getApplications() {
        return applications;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public int getDenials() {
        return denials;
    }
}
