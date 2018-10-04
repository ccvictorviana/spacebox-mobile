package br.com.spacebox.utils.observer;

public interface Subject {
    void register(Observer observer);

    void unregister(Observer observer);

    void notifyObservers();
}