// File: src/main/java/com/pascs/payload/response/MessageResponse.java
package com.pascs.payload.response;

public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}