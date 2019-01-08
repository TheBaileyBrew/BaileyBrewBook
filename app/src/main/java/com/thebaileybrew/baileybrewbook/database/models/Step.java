package com.thebaileybrew.baileybrewbook.database.models;

public class Step {

    private int stepId;
    private String stepShortDescription;
    private String fullDescription;
    private String stepVideoUrl;
    private String stepThumbnail;

    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public void setStepShortDescription(String stepShortDescription) {
        this.stepShortDescription = stepShortDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public void setStepVideoUrl(String stepVideoUrl) {
        this.stepVideoUrl = stepVideoUrl;
    }

    public void setStepThumbnail(String stepThumbnail) {
        this.stepThumbnail = stepThumbnail;
    }

    public int getStepId() {
        return stepId;
    }

    public String getStepShortDescription() {
        return stepShortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public String getStepVideoUrl() {
        return stepVideoUrl;
    }

    public String getStepThumbnail() {
        return stepThumbnail;
    }
}
