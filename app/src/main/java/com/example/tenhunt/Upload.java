package com.example.tenhunt;

public class Upload {
    private String postTitle;
    private String mImageUrl;
    private String postDesc;
    private String postPrice;
    private String postContact;
    private String postEmail;
    private String key;
    private int position;

    public Upload(){
        //empty constructor
    }

    public Upload(int position){
        this.position = position;
    }

    public Upload(String title, String imageUrl, String desc, String price, String contact, String email) {

        this.postTitle = title;
        this.mImageUrl = imageUrl;
        this.postDesc = desc;
        this.postPrice = price;
        this.postContact = contact;
        this.postEmail = email;
    }


    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public String getPostPrice() {
        return postPrice;
    }

    public void setPostPrice(String postPrice) {
        this.postPrice = postPrice;
    }

    public String getPostContact() {
        return postContact;
    }

    public void setPostContact(String postContact) {
        this.postContact = postContact;
    }

    public String getPostEmail() {
        return postEmail;
    }

    public void setPostEmail(String postEmail) {
        this.postEmail = postEmail;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
