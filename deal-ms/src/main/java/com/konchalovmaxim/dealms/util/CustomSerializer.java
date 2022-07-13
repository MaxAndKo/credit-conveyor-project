package com.konchalovmaxim.dealms.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konchalovmaxim.dealms.dto.EmailMessageDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;
//TODO верно ли здесь все?
@RequiredArgsConstructor
public class CustomSerializer implements Serializer {
    private final ObjectMapper objectMapper;


    @Override
    public void configure(Map configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Object messageDTO) {
        try{
            if (messageDTO == null){
                System.out.println("Null received at serializing");
                return null;
            }
            else {
                System.out.println("Serializing...");
                return objectMapper.writeValueAsBytes(messageDTO);
            }
        } catch (Exception e){
            throw new SerializationException("Error when serializing emailMessageDto to byte[]");
        }
    }

    @Override
    public void close() {
    }
}
