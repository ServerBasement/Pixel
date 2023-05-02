package it.ohalee.pixel.api;

public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
        super("User not found in redis!");
    }

    public UserNotFoundException(String error) {
        super(error);
    }

}
