package com.aaryan.coronavirustracker.Model;

import lombok.*;

import javax.persistence.*;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Builder
@ToString
public class UnvalidatedUserModelDto {


    private Integer id;

    private String firstName;


    private String lastName;


    private String password;


    private String email;


    private String phoneNumber;


    private String uuid;


    private String pincode;


    private String state;


    private String city;
}
