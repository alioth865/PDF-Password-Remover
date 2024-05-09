package org.example.exception;

public class FileWithoutPasswordException extends Exception {

    public FileWithoutPasswordException(String msg) {
        super(msg);
    }

}