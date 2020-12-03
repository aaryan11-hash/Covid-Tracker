package com.aaryan.coronavirustracker.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.UUID;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserModelDto implements Serializable {

    static final long serialNumber=-1214124L;

    @JsonProperty("firstname")
    @NotBlank
    private String firstName;

    @JsonProperty("lastname")
    @NotBlank
    private String lastName;

    @Pattern(regexp = "[a-zA-Z0-9]{8,20}",message = "caanot be null")
    private String password;

    @JsonProperty("email")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9.]{5,40}@(gmail.com|yahoo.com|hotmail.com|outlook.com|sitpune.edu.in)$",message = "please make sure that you are entering only gmail and valid credentials")
    private String email;

    @JsonProperty("phonenumber")
    @Pattern(regexp = "^[0-9]{10}",message ="phone number cannot contain characters!!")
    private String phoneNumber;

    @JsonProperty("pincode")
    @Pattern(regexp = "^[0-9]{6}",message = "pincodes are built of exactly 6 characters")
    private String pincode;

    @JsonProperty("uuid")
    private String uuid;

    @NotNull
    private String state;

    @NotNull
    private String city;


    private String token;

}
