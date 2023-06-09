package com.endava.developement.java.webapphomework.exceptions;

public class DepartmentNotFoundException extends RuntimeException{

    public DepartmentNotFoundException(){
        super("You typed department id which is not registered in the system!");
    }
}
