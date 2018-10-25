package Jowil;

import java.util.Arrays;


public abstract class Translator {

    final private static String arabicNumbers="٠١٢٣٤٥٦٧٨٩";
    final private static String arabicLetters="أبجدهوزحطيكلمنسعفصقرشتثخذوضظغ";

    private static boolean isNumeric(String s){
        try{
            Integer.parseInt(s);
        }catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    public static String englishToArabic(String input){

        if(input.length()==0)
            return input;
        StringBuilder output=new StringBuilder();
        if(isNumeric(input)){
            for(int i=0;i<input.length();i++){
                int englishNumber=Integer.parseInt(Character.toString(input.charAt(i)));
                output.append(arabicNumbers.charAt(englishNumber));
            }
        }
        else{
            char c=input.charAt(0);
            System.out.println("input: "+input);
            System.out.println("c: "+c);
            int index=Character.isUpperCase(c)?c-'A':c-'a';
            if(index>=arabicLetters.length() || index<0 || input.length()>1)
                return input;

            output.append(arabicLetters.charAt(index));
        }

        return output.toString();
    }

    public static String arabicToEnglish(String input){
        if(input.length()==0)
            return input;
        StringBuilder output=new StringBuilder();
        int index=arabicLetters.indexOf(input.charAt(0));
        if(index!=-1) // check if it's a letter.
            return Character.toString((char)('A'+index));

        for(int i=0;i<input.length();i++){
            index=Arrays.asList(arabicNumbers).indexOf(Character.toString(input.charAt(i)));
            if(index==-1)
                return input;
            output.append(index);
        }
        return output.toString();
    }

}
