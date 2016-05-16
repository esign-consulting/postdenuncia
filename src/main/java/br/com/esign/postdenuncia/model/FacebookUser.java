package br.com.esign.postdenuncia.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import br.com.esign.postdenuncia.facebook.Device;
import br.com.esign.postdenuncia.facebook.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "facebookUser")
@SuppressWarnings("serial")
public class FacebookUser implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "gender")
    private String gender;

    @Column(name = "location")
    private String location;

    @Column(name = "picture")
    private String picture;

    @Column(name = "appleOwner")
    private boolean appleOwner;

    @Column(name = "androidOwner")
    private boolean androidOwner;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "facebookFriendship",
            joinColumns = {
                @JoinColumn(name = "id_user")},
            inverseJoinColumns = {
                @JoinColumn(name = "id_friend")})
    private Set<FacebookUser> friends;

    public FacebookUser() {
    }

    public FacebookUser(User user) throws ParseException {
        this.setId(user.getId());
        this.setUser(user);
        friends = new HashSet<FacebookUser>();
    }

    public void setUser(User user) throws ParseException {
        this.setName(user.getName());
        this.setEmail(user.getEmail());
        this.setGender(user.getGender());

        String birthday = user.getBirthday();
        if (birthday != null && !birthday.isEmpty()) {
            SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
            this.setBirthday(parser.parse(birthday));
        } else {
            this.setBirthday(null);
        }

        if (user.getLocation() != null) {
            this.setLocation(user.getLocation().getName());
        } else {
            this.setLocation(null);
        }

        if (user.getPicture() != null && user.getPicture().getData() != null) {
            this.setPicture(user.getPicture().getData().getUrl());
        } else {
            this.setPicture(null);
        }

        boolean appleOwner = false, androidOwner = false;
        List<Device> devices = user.getDevices();
        if (devices != null && !devices.isEmpty()) {
            for (Device device : devices) {
                String os = device.getOs();
                if ("iOS".equals(os)) {
                    appleOwner = true;
                } else if ("Android".equals(os)) {
                    androidOwner = true;
                }
            }
        }
        this.setAppleOwner(appleOwner);
        this.setAndroidOwner(androidOwner);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isAppleOwner() {
        return appleOwner;
    }

    public void setAppleOwner(boolean appleOwner) {
        this.appleOwner = appleOwner;
    }

    public boolean isAndroidOwner() {
        return androidOwner;
    }

    public void setAndroidOwner(boolean androidOwner) {
        this.androidOwner = androidOwner;
    }

    public Set<FacebookUser> getFriends() {
        return friends;
    }

    public void setFriends(Set<FacebookUser> friends) {
        this.friends = friends;
    }

}