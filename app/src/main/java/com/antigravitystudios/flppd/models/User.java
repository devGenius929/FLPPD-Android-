package com.antigravitystudios.flppd.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User extends BaseObservable {

    @SerializedName("first_name")
    @Expose
    private String firstName;

    @SerializedName("last_name")
    @Expose
    private String lastName;

    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("auth_token")
    @Expose
    private String authToken;

    @SerializedName("user_id")
    @Expose
    private Integer userId;

    @SerializedName("about")
    @Expose
    private String about;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("activated")
    @Expose
    private Boolean activated;

    @SerializedName("activated_at")
    @Expose
    private String activatedAt;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("verified")
    @Expose
    private Boolean verified;

    @SerializedName("customer_id")
    @Expose
    private String customerId;

    @SerializedName("subscribed")
    @Expose
    private Boolean subscribed;

    @SerializedName("friend")
    @Expose
    private Boolean friend;

    @SerializedName("firebase_password")
    @Expose
    private String firebasePassword;

    @SerializedName("role")
    @Expose
    private String role;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("areas")
    @Expose
    private String areas;

    @SerializedName("rank")
    @Expose
    private String rank;

    @SerializedName("hauses_sold")
    @Expose
    private Integer hausesSold;

    private int connect_state; //0 - connect, 1 - connected, 2 - pending

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Bindable
    public String getPhoneNumber() {
        if(phoneNumber == null) return null;
        return phoneNumber.substring(3);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = "+1 " + phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(String activatedAt) {
        this.activatedAt = activatedAt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }

    public Boolean getFriend() {
        return friend;
    }

    public void setFriend(Boolean friend) {
        this.friend = friend;
    }

    public String getFirebasePassword() {
        return firebasePassword;
    }

    public void setFirebasePassword(String firebasePassword) {
        this.firebasePassword = firebasePassword;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAreas() {
        return areas;
    }

    public void setAreas(String areas) {
        this.areas = areas;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Integer getHausesSold() {
        return hausesSold;
    }

    public void setHausesSold(Integer hausesSold) {
        this.hausesSold = hausesSold;
    }

    public int getConnect_state() {
        return connect_state;
    }

    public void setConnect_state(int connect_state) {
        this.connect_state = connect_state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        User thatUser;
        if(o instanceof User) thatUser = (User) o;
        else if(o instanceof Connection) thatUser = ((Connection) o).getUser();
        else return false;

        return userId.equals(thatUser.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}