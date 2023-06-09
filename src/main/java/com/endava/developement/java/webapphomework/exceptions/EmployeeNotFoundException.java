package com.endava.developement.java.webapphomework.exceptions;

public class EmployeeNotFoundException extends RuntimeException{

    public EmployeeNotFoundException(){
        super("No such employee in the system !");
    }
}
