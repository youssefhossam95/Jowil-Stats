package Jowil;

import java.util.concurrent.LinkedBlockingQueue;

public class ColorGenerator {
    private final static String [] manualModeTableColors={"green","blue","red","orange","pink"};
    LinkedBlockingQueue<String> availableColors;

    ColorGenerator(){

        resetAvailable();
    }

    public String getNextColor(){
        return availableColors.poll();
    }

    public void addToAvailable(String color){
        availableColors.add(color);
    }

    public void resetAvailable(){

        availableColors=new LinkedBlockingQueue<>();
        for(String color:manualModeTableColors){
            availableColors.add(color);
        }
    }
}
