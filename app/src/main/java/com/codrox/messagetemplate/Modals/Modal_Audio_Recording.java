package com.codrox.messagetemplate.Modals;

public class Modal_Audio_Recording {
    String id;
    String audio_path;
    String date;
    String audio_status;

    private boolean isPlaying=false;

    public Modal_Audio_Recording(String id, String audio_path, String date, String audio_status) {
        this.id = id;
        this.audio_path = audio_path;
        this.date = date;
        this.audio_status = audio_status;
    }

    public String getId() {
        return id;
    }

    public String getAudio_path() {
        return audio_path;
    }

    public String getDate() {
        return date;
    }

    public String getAudio_status() {
        return audio_status;
    }

    public boolean getPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
