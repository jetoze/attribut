package jetoze.attribut;

import java.beans.PropertyChangeListener;

public interface Property<T> {

    // TODO: Decouple from java.beans.PropertyChangeListener? Use our own listener, 
    // with typed (<T>) new and old values?
    
    String name();
    
    T get();
    
    void set(T value);
    
    void setSilently(T value);
    
    void addListener(PropertyChangeListener listener);
    
    void removeListener(PropertyChangeListener listener);
    
}
