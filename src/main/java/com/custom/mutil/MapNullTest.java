package com.custom.mutil;

import java.util.HashMap;
import java.util.Map;

public class MapNullTest {

    public static void main(String[] args) {
        Map<Object, Object> map = new HashMap<>();
        System.out.println((String) map.get("awards"));
    }
}
