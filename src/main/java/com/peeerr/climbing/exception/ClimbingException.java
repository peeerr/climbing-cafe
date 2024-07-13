package com.peeerr.climbing.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public abstract class ClimbingException extends RuntimeException {

    public final Map<String, String> validation = new HashMap<>();

    public ClimbingException(String message) {
        super(message);

    }

    public abstract int getStatusCode();

}
