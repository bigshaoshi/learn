package com.learn.learnredis.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User implements Serializable {

    private String id;

    private String name;

    private Integer age;

    private Integer sex;

    private String nickname;

    private String username;

    private String password;

    private Date birth;

}
