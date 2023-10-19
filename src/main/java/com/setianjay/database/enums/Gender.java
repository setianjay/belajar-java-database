package com.setianjay.database.enums;

public enum Gender {
    MALE("Male"),
    FEMALE("Female");

    private final String value;

    Gender(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public static Gender mapStringToGender(String gender){
        if (gender.equals(Gender.MALE.getValue())) {
            return Gender.MALE;
        }

        return Gender.FEMALE;
    }
}
