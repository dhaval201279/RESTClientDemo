package com.its.RESTClientDemo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardEntity implements Serializable {
    private Long id;
    private String issuingNetwork;
    private String cardNumber;
    private String name;
    private String expiryDate;
    private String alias;
}
