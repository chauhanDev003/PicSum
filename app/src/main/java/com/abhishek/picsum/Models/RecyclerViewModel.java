package com.abhishek.picsum.Models;

public class RecyclerViewModel {
    private String id, author;

    public RecyclerViewModel(String id, String author) {
        this.id = id;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


}
