package com.ascomp.database_prac;

public class Abc
{

    static Abc abc;

    private Abc(){

    }

    public static Abc getIns(){
        if(abc == null)
            abc = new Abc();

        return abc;
    }
}
