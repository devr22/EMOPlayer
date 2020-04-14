package com.example.emoplayer.Model;

public class Model_Songs_Favourites {

    String docId;
    String songId;

    public Model_Songs_Favourites(){

    }

    public Model_Songs_Favourites(String docId, String songId) {
        this.docId = docId;
        this.songId = songId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }
}
