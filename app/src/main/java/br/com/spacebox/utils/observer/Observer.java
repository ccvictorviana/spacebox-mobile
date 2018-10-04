package br.com.spacebox.utils.observer;

public interface Observer<T extends IObservableEntity> {
    void update(T entity);
}