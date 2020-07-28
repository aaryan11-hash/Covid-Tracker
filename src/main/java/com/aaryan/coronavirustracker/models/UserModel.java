package com.aaryan.coronavirustracker.models;

import lombok.*;

import javax.validation.constraints.*;
import java.util.UUID;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Builder
public class UserModel {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{5,40}@(gmail|yahoo|hotmail|outlook).com$",message = "please make sure that you are entering only gmail and valid credentials")
    private String email;

    @Pattern(regexp = "^[0-9]{10}",message ="phone number cannot contain characters!!")
    private String phoneNumber;

    @Pattern(regexp = "^[0-9]{6}",message = "pincodes are built of exactly 6 characters")
    private String pincode;

    private String uuidIndentifier;

    private String notificationFrequency;



}
