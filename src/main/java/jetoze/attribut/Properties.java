package jetoze.attribut;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Properties {
    
    public static <T> Property<T> newProperty(String name, T value) {
        return newProperty(name, value, new PropertyChangeSupport(Properties.class));
    }

    public static <T> Property<T> newProperty(String name, T value, PropertyChangeSupport changeSupport) {
        return new PropertyImpl<>(name, value, changeSupport);
    }
    
    public static <T> ListProperty<T> newListProperty(String name, List<T> value) {
        return newListProperty(name, value, new PropertyChangeSupport(Properties.class));
    }
    
    public static <T> ListProperty<T> newListProperty(String name, List<T> value, PropertyChangeSupport changeSupport) {
        return new ListPropertyImpl<>(name, value, changeSupport);
    }
    
    
    private static class PropertyImpl<T> implements Property<T> {
        private final String name;
        private T value;
        private final PropertyChangeSupport changeSupport;
        
        public PropertyImpl(String name, T value, PropertyChangeSupport changeSupport) {
            checkArgument(!name.isBlank(), "name cannot be blank");
            this.name = name;
            this.value = requireNonNull(value);
            this.changeSupport = requireNonNull(changeSupport);
        }
        
        @Override
        public String name() {
            return name;
        }

        @Override
        public synchronized T get() {
            return value;
        }

        @Override
        public void set(T value) {
            requireNonNull(value);
            T old;
            synchronized (this) {
                if (value == this.value) {
                    return;
                }
                old = this.value;
                this.value = value;
            }
            changeSupport.firePropertyChange(name, old, value);
        }

        @Override
        public synchronized void setSilently(T value) {
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
        
        public void sendChangeEvent() {
            changeSupport.firePropertyChange(name, null, null);
        }
    }
    
    
    private static class ListPropertyImpl<T> implements ListProperty<T> {
        private final PropertyImpl<List<T>> impl;
        private volatile boolean doDefensiveCopy = true;

        public ListPropertyImpl(String name, List<T> value, PropertyChangeSupport changeSupport) {
            this.impl = new PropertyImpl<>(name, value, changeSupport);
        }

        @Override
        public String name() {
            return impl.name();
        }

        @Override
        public List<T> get() {
            return valueToUse(impl.get());
        }

        private List<T> valueToUse(List<T> value) {
            return doDefensiveCopy
                    ? new ArrayList<>(value)
                    : value;
        }
        
        @Override
        public void set(List<T> value) {
            List<T> valueToUse = valueToUse(value);
            impl.set(valueToUse);
        }

        @Override
        public void setSilently(List<T> value) {
            impl.setSilently(value);
        }

        @Override
        public void setDoDefensiveCopy(boolean value) {
            this.doDefensiveCopy = value;
        }

        @Override
        public void clear() {
            impl.set(new ArrayList<>());
        }
        
        @Override
        public void sort(Comparator<? super T> comparator) {
            requireNonNull(comparator);
            synchronized (this) {
                impl.get().sort(comparator);
            }
            impl.sendChangeEvent();
        }
        
        @Override
        public void addListener(PropertyChangeListener listener) {
            impl.addListener(listener);
        }

        @Override
        public void removeListener(PropertyChangeListener listener) {
            impl.removeListener(listener);
        }
    }
    
}
