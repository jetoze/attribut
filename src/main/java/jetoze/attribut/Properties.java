package jetoze.attribut;

import static java.util.Objects.*;
import static com.google.common.base.Preconditions.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Properties {

    public static <T> Property<T> newProperty(String name, T value) {
        return new PropertyImpl<>(name, value);
    }
    
    private static class PropertyImpl<T> implements Property<T> {
        private final String name;
        private T value;
        private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
        
        public PropertyImpl(String name, T value) {
            checkArgument(!name.isBlank(), "name cannot be blank");
            this.name = name;
            this.value = requireNonNull(value);
        }
        
        @Override
        public String name() {
            return name;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void set(T value) {
            requireNonNull(value);
            if (value == this.value) {
                return;
            }
            T old = this.value;
            this.value = value;
            changeSupport.firePropertyChange(name, old, value);
        }

        @Override
        public void setSilently(T value) {
            this.value = requireNonNull(value);
        }

        @Override
        public void addListener(PropertyChangeListener listener) {
            requireNonNull(listener);
            changeSupport.addPropertyChangeListener(name, listener);
        }

        @Override
        public void removeListener(PropertyChangeListener listener) {
            requireNonNull(listener);
            changeSupport.removePropertyChangeListener(name, listener);
        }
    }
    
}
