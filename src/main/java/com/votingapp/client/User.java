package com.votingapp.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String voterID;
    private String password;
    private boolean isLogin = false;

    public User(String voterID, String password) {
        this.voterID = voterID;
        this.password = password;
        this.isLogin = false;
    }
}
