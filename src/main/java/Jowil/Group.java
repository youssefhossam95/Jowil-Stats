package Jowil;

import java.util.ArrayList;

public class Group {

    //fields
    String name;
    Integer qCount;
    ArrayList<String> possibleAnswers;


    Group(String name, int qCount){
        this.name=name;
        this.qCount=qCount;
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

    public ArrayList<String> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(ArrayList<String> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

}
