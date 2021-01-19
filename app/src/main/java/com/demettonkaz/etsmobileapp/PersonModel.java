package com.demettonkaz.etsmobileapp;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PersonModel extends RealmObject {

    @PrimaryKey
    private int id;
    private String personName;
    private String personSurname;
    private String personDateOfBirth;
    private String personPhoneNumber;
    private String personEmail;
    private String personNote;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonSurname() {
        return personSurname;
    }

    public void setPersonSurname(String personSurname) {
        this.personSurname = personSurname;
    }

    public String getPersonDateOfBirth() {
        return personDateOfBirth;
    }

    public void setPersonDateOfBirth(String personDateOfBirth) {
        this.personDateOfBirth = personDateOfBirth;
    }

    public String getPersonPhoneNumber() {
        return personPhoneNumber;
    }

    public void setPersonPhoneNumber(String personPhoneNumber) {
        this.personPhoneNumber = personPhoneNumber;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public String getPersonNote() {
        return personNote;
    }

    public void setPersonNote(String personNote) {
        this.personNote = personNote;
    }
}
