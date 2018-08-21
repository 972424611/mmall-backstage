package com.aekc.mmall.beans;

import lombok.Data;

import java.util.Set;

@Data
public class Mail {

    private String subject;

    private String message;

    private Set<String> receivers;
}
