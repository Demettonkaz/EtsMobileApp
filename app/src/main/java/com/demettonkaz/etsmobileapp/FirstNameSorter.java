package com.demettonkaz.etsmobileapp;

import java.util.Comparator;

public class FirstNameSorter implements Comparator<PersonModel> {

    @Override
    public int compare(PersonModel p1, PersonModel p2) {
        return p1.getPersonName().compareTo(p2.getPersonName());
    }
}