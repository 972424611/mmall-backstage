package com.aekc.mmall.beans;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageResult<T> {

    private List<T> data = new ArrayList<>();

    private int total;
}
