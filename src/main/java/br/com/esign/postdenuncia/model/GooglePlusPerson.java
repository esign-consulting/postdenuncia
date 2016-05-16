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

import br.com.esign.postdenuncia.google.plus.Person;
import br.com.esign.postdenuncia.google.plus.PlaceLived;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "googlePlusPerson")
@SuppressWarnings("serial")
public class GooglePlusPerson implements Serializable {

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

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "googlePlusCircles",
            joinColumns = {
                @JoinColumn(name = "id_person1")},
            inverseJoinColumns = {
                @JoinColumn(name = "id_person2")})
    private Set<GooglePlusPerson> people;

    public GooglePlusPerson() {
    }

    public GooglePlusPerson(Person person) throws ParseException {
        this.setId(person.getId());
        this.setPerson(person);
        people = new HashSet<GooglePlusPerson>();
    }

    public void setPerson(Person person) throws ParseException {
        this.setName(person.getDisplayName());
        this.setEmail(person.getEmail());
        this.setGender(person.getGender());

        String birthday = person.getBirthday();
        if (birthday != null && !birthday.isEmpty()) {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            this.setBirthday(parser.parse(birthday));
        } else {
            this.setBirthday(null);
        }

        String location = null;
        List<PlaceLived> placesLived = person.getPlacesLived();
        if (placesLived != null && !placesLived.isEmpty()) {
            for (PlaceLived placeLived : placesLived) {
                if (placeLived.isPrimary()) {
                    location = placeLived.getValue();
                    break;
                }
            }
        }
        this.setLocation(location);

        if (person.getImage() != null) {
            this.setPicture(person.getImage().getUrl());
        } else {
            this.setPicture(null);
        }
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

    public Set<GooglePlusPerson> getPeople() {
        return people;
    }

    public void setPeople(Set<GooglePlusPerson> people) {
        this.people = people;
    }

}
