package com.example.iag.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {
    public static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
}
