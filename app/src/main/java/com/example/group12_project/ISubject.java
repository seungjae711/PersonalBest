package com.example.group12_project;


//Structure from week 9 Lab
public interface ISubject<ObserverT> {
    void register(ObserverT observer);

   // void unregister(ObserverT observer);
}
