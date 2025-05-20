package com.potager.customExceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp = System.currentTimeMillis();


    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
