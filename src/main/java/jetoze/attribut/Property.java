package jetoze.attribut;

import java.beans.PropertyChangeListener;

public interface Property<T> {

    String name();
    
    T get();
    
    void set(T value);
    
    void setSilently(T value);
    
    void addListener(PropertyChangeListener listener);
    
    void removeListener(PropertyChangeListener listener);
    
}
