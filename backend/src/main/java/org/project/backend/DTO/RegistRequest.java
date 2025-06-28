package org.project.backend.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegistRequest {
    String realName;
    String username;
    String password;
}
