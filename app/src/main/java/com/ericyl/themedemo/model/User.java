package com.ericyl.themedemo.model;

import java.io.Serializable;

/**
 * Created by liangyu on 15/5/20.
 */
public class User implements Serializable {
    public static final String KEY_NAME = "User";
    private Integer index;
    private String username;
    private String password;
    private String email;
    private Integer emailVerification;

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(int index, String username, String password, String email) {
        this.index = index;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEmailVerification() {
        return emailVerification;
    }

    public void setEmailVerification(Integer emailVerification) {
        this.emailVerification = emailVerification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!index.equals(user.index)) return false;
        if (!username.equals(user.username)) return false;
        if (!password.equals(user.password)) return false;
        if (!email.equals(user.email)) return false;
        return emailVerification.equals(user.emailVerification);

    }

    @Override
    public int hashCode() {
        int result = index.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + emailVerification.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "index=" + index +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", emailVerification=" + emailVerification +
                '}';
    }
}
