package com.example.serverchodientu.dto.product;

public class SetFavoriteRespond {
    boolean isFavorite;
    String message;

    public SetFavoriteRespond(boolean isFavorite, String message) {
        this.isFavorite = isFavorite;
        this.message = message;
    }

    public SetFavoriteRespond() {

    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
