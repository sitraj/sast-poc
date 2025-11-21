package com.sast.demo
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JacksonConfig {
    @Bean
    fun vulnobjectMapper(): ObjectMapper {

        val mapper = ObjectMapper();

        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); // this in itself should be flagged too
        return mapper;

    }
}
