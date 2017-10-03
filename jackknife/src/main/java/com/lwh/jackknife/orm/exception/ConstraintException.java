package com.lwh.jackknife.orm.exception;

/**
 * 约束条件异常。
 */
public class ConstraintException extends RuntimeException{

    public ConstraintException(){
    }

    public ConstraintException(String message){
        super(message);
    }
}
