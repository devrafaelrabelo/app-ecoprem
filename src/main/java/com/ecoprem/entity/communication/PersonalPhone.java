package com.ecoprem.entity.communication;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "personal_phone")
public class PersonalPhone {

    @Id
    private UUID id;

    private String number;
    private String type; // mobile, landline, etc.
}