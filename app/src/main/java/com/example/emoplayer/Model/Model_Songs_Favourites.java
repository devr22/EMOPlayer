package com.example.emoplayer.Model;

public class Model_Songs_Favourites {

    String docID;
    String songID;

    public Model_Songs_Favourites() {

    }

    public Model_Songs_Favourites(String docID, String songID) {
        this.docID = docID;
        this.songID = songID;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }
}
