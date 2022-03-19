package com.dharma.shopinar;

public class ScreenItem {

    String Title,Description;
    int ScreenImg;

    String WelcomeSlideAnim;


    public ScreenItem(String title, String description, String welcomeSlideAnim) {
        Title = title;
        Description = description;
        WelcomeSlideAnim =  welcomeSlideAnim;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getWelcomeSlideAnim() {
        return WelcomeSlideAnim;
    }


    public void setScreenImg(int screenImg) {
        ScreenImg = screenImg;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public int getScreenImg() {
        return ScreenImg;
    }
}
