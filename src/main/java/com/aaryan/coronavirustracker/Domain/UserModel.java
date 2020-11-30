package com.aaryan.coronavirustracker.Domain;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
@ToString

public class UserModel {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

   // @Column(updatable = true,nullable = false,columnDefinition = "varchar(30)")
    private String firstName;

   // @Column(updatable = true,nullable = false,columnDefinition = "varchar(30)")
    private String lastName;

    //@Column(updatable = true,nullable = false,columnDefinition = "varchar(30)")
    private String password;

    //@Column(updatable = true,nullable = false,columnDefinition = "varchar(30)")
    private String email;

    //@Column(updatable = true,nullable = false,columnDefinition = "varchar(40)")
    private String phoneNumber;

   // @Column(updatable = true,nullable = false,columnDefinition = "varchar(30)")
    private String pincode;

   // @Column(updatable = true,nullable = false,columnDefinition = "varchar(30)")
    private String state;

   // @Column(updatable = true,nullable = false,columnDefinition = "varchar(30)")
    private String city;






}
