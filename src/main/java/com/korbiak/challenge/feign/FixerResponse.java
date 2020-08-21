package com.korbiak.challenge.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixerResponse {

    private boolean success;
    private Time timestamp;
    private String base;
    private Date date;
    private Map<String, Double> rates = new HashMap<>();
}
