package Jowil;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

public class Group {

    //fields
    private String name;
    private Integer qCount;
    private SimpleStringProperty nameProp;
    private SimpleStringProperty qCountProp;
    private ArrayList<String> possibleAnswers;


    Group(String name, int qCount){
        this.name=name;
        this.qCount=qCount;
        nameProp=new SimpleStringProperty(name);
        qCountProp=new SimpleStringProperty(Integer.toString(qCount));
    }



    //setters and getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getqCount() {
        return qCount;
    }

    public void setqCount(Integer qCount) {
        this.qCount = qCount;
    }

    public String getNameProp() {
        return nameProp.get();
    }

    public SimpleStringProperty namePropProperty() {
        return nameProp;
    }

    public void setNameProp(String nameProp) {
        this.nameProp.set(nameProp);
    }

    public String getqCountProp() {
        return qCountProp.get();
    }

    public SimpleStringProperty qCountPropProperty() {
        return qCountProp;
    }

    public void setqCountProp(String qCountProp) {
        this.qCountProp.set(qCountProp);
    }


    public ArrayList<String> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(ArrayList<String> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

}
