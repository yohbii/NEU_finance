package org.project.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpResponse<T> {
    private int code;

    private T data;

    private String message;

    private String error;
}
