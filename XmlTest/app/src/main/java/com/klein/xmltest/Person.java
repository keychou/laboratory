package com.klein.xmltest;

/**
 * Created by klein on 18-3-31.
 */


public class Person {
    private int id;
    private String name;
    private String blog;

    public Person() {
        this.id = -1;
        this.name = "";
        this.blog = "";
    }

    public Person(int id, String name, String blog) {
        this.id = id;
        this.name = name;
        this.blog = blog;
    }

    public Person(Person person) {
        this.id = person.id;
        this.name = person.name;
        this.blog = person.blog;
    }

    public Person getPerson(){
        return this;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getBlog() {
        return this.blog;
    }

    public String toString() {
        return "Person \nid = " + id + "\nname = " + name + "\nblog = " + blog + "\n";
    }
}
