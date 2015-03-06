package com.github.lemniscate.hipstack.config;

import com.github.lemniscate.spring.crud.annotation.EnableApiResources;
import com.github.lemniscate.hipstack.domain.Models;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dave on 2/27/15.
 */
@Configuration
@EnableApiResources(Models.class)
public class ApiResourceConfig {
    
}
