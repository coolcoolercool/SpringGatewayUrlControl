package org.example.gatewaysession.entity;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserInfo implements Serializable {
    String userName;
    List<String> urlList;
    public UserInfo() {
    }
}