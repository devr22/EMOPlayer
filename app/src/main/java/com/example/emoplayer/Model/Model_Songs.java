package com.example.emoplayer.Model;

import java.io.Serializable;

public class Model_Songs implements Serializable {

    private String album_art;
    private String artist;
    private String id;
    private String songCategory;
    private String songDuration;
    private String songLink;
    private String songTitle;

    public Model_Songs() {

    }

    public Model_Songs(String album_art, String artist, String id, String songCategory, String songDuration, String songLink, String songTitle) {
        this.album_art = album_art;
        this.artist = artist;
        this.id = id;
        this.songCategory = songCategory;
        this.songDuration = songDuration;
        this.songLink = songLink;
        this.songTitle = songTitle;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSongCategory() {
        return songCategory;
    }

    public void setSongCategory(String songCategory) {
        this.songCategory = songCategory;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }
}
