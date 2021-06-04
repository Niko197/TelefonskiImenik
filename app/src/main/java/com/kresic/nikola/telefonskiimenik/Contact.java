package com.kresic.nikola.telefonskiimenik;

public class Contact {

    int id, relationshipID;
    String name,number,email,organization;

    public Contact(){

    }

    public Contact(int id, String name, String number, String email, String organization, int relationshipID) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.email = email;
        this.organization = organization;
        this.relationshipID = relationshipID;
    }

    public Contact(String name, String number, String email, String organization, int relationshipID) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.organization = organization;
        this.relationshipID = relationshipID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public int getRelationshipID() {
        return relationshipID;
    }

    public void setRelationshipID(int relationshipID) {
        this.relationshipID = relationshipID;
    }
}
