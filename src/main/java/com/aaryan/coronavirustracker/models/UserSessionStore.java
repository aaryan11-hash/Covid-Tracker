package com.aaryan.coronavirustracker.models;


import lombok.*;

import java.util.HashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserSessionStore {

    private HashSet<String> emailList;
    private int loginAccessCount;
    private Long prevTimeOfLoginCheck;

    public UserSessionStore(HashSet set,int loginAccessCount){
        this.emailList = set;
        this.loginAccessCount = loginAccessCount;
    }

}
