package br.com.esign.postdenuncia.facebook;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendsData {

    private Set<User> data;

    public Set<User> getData() {
        return data;
    }

    public void setData(Set<User> data) {
        this.data = data;
    }

}