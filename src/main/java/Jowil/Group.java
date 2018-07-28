package Jowil;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

public class Group extends RecursiveTreeObject<Group> {

    //fields
    private String name;
    private Integer qCount;
    private SimpleStringProperty nameProp;
    private SimpleStringProperty qCountProp;
    private ArrayList<String> possibleAnswers;
    private boolean isNumeric;



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

    public String getCleanedName(){
        return name.charAt(name.length()-1)=='-'?name.substring(0,name.length()-1):name;
    }

    public void setName(String name) {
        this.name = name;
        this.nameProp.set(name);
    }

    public Integer getqCount() {
        return qCount;
    }

    public void setqCount(Integer qCount) {
        this.qCount = qCount;
        this.qCountProp.set(qCount.toString());
    }

    public String getNameProp() {
        return nameProp.get();
    }

    public SimpleStringProperty getCleanedNameProp() {
        return new SimpleStringProperty(getCleanedName());
    }



    public SimpleStringProperty namePropProperty() {
        return nameProp;
    }

    public void setNameProp(String nameProp) {
        this.nameProp.set(nameProp);
        this.name=nameProp;
    }

    public String getqCountProp() {
        return qCountProp.get();
    }

    public SimpleStringProperty getObsQcountProp(){
        return new SimpleStringProperty(getqCountProp());
    }

    public SimpleStringProperty qCountPropProperty() {
        return qCountProp;
    }

    public void setqCountProp(String qCountProp) {
        this.qCountProp.set(qCountProp);
        this.qCount=Integer.parseInt(qCountProp);
    }


    public ArrayList<String> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(ArrayList<String> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    public void generatePossibleAnswers(String groupMaxChoice){

        isNumeric=true;
        possibleAnswers=new ArrayList<String>();
        int maxInt=0,minInt=1;
        String maxString="";
        char maxChar,minChar;
        try {
             maxInt = Integer.parseInt(groupMaxChoice);
        }catch(NumberFormatException e){
            isNumeric=false;
            maxChar=groupMaxChoice.charAt(0);//ignore extra letters
            if(Character.isLowerCase(new Character(maxChar)))
                minChar='a';
            else
                minChar='A';
            maxInt=maxChar;
            minInt=minChar;
        }



        for(int i=minInt;i<=maxInt;i++){
            String answer=isNumeric?Integer.toString(i):""+(char)i;
            possibleAnswers.add(answer);
        }


    }

    public void addAnswerToEnd(){

        String lastAnswer=possibleAnswers.get(possibleAnswers.size()-1);


        String newAnswer=isNumeric?Integer.toString(Integer.parseInt(lastAnswer)+1):""+(char)(lastAnswer.charAt(0)+1);

        possibleAnswers.add(newAnswer);


    }

    public void updatePossibleAnswers(ArrayList<Boolean> isPossible){

        ArrayList<String> newPossibleAnswers=new ArrayList<String>();

        for(int i=0;i<possibleAnswers.size();i++){

            if(isPossible.get(i))
                newPossibleAnswers.add(possibleAnswers.get(i));

        }

        possibleAnswers=newPossibleAnswers;
    }



}
