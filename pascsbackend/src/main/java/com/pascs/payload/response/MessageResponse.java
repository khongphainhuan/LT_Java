// File: src/main/java/com/pascs/payload/response/MessageResponse.java
package com.pascs.payload.response;

import lombok.Data;

@Data
public class MessageResponse {
    
    // ✅ Field duy nhất: Message string
    private String message;

    // ✅ Constructor với message
    public MessageResponse(String message) {
        this.message = message;
    }

    // ✅ Getter method
    public String getMessage() {
        return this.message;
    }
    
    // ✅ Setter method  
    public void setMessage(String message) {
        this.message = message;
    }
}