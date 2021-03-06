package jetoze.attribut;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class Properties {
    
    public static <T> Property<T> newProperty(String name, T value) {
        return newProperty(name, value, new PropertyChangeSupport(Properties.class));
    }

    public static <T> Property<T> newProperty(String name, T value, PropertyChangeSupport changeSupport) {
        return new PropertyImpl<>(name, false, value, changeSupport);
    }
    
    public static <T> Property<T> newNullableProperty(String name) {
        return newNullableProperty(name, null, new PropertyChangeSupport(Properties.class));
    }
    
    public static <T> Property<T> newNullableProperty(String name, PropertyChangeSupport changeSupport) {
        return newNullableProperty(name, null, changeSupport);
    }
    
    public static <T> Property<T> newNullableProperty(String name, T value) {
        return newNullableProperty(name, value, new PropertyChangeSupport(Properties.class));
    }

    public static <T> Property<T> newNullableProperty(String name, T value, PropertyChangeSupport changeSupport) {
        return new PropertyImpl<>(name, true, value, changeSupport);
    }
    
    public static <T> ListProperty<T> newListProperty(String name, List<T> value) {
        return newListProperty(name, value, new PropertyChangeSupport(Properties.class));
    }
    
    public static <T> ListProperty<T> newListProperty(String name, List<T> value, PropertyChangeSupport changeSupport) {
        return new ListPropertyImpl<>(name, value, changeSupport);
    }
    
    
    // TODO: It would be nice if a nullable property could return an Optional<T>. 
    private static class PropertyImpl<T> implements Property<T> {
        private final String name;
        private final boolean allowNull;
        private T value;
        private final PropertyChangeSupport changeSupport;
        
        public PropertyImpl(String name, boolean allowNull, T value, PropertyChangeSupport changeSupport) {
            checkArgument(!name.isBlank(), "name cannot be blank");
            this.name = name;
            this.allowNull = allowNull;
            if (!allowNull) {
                requireNonNull(value);
            }
            this.value = value;
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
            if (!allowNull) {
                requireNonNull(value);
            }
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
            if (!allowNull) {
                requireNonNull(value);
            }
            this.value = value;
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
        private final PropertyImpl<ImmutableList<T>> impl;

        public ListPropertyImpl(String name, List<T> value, PropertyChangeSupport changeSupport) {
            this.impl = new PropertyImpl<>(name, false, ImmutableList.copyOf(value), changeSupport);
        }

        @Override
        public String name() {
            return impl.name();
        }

        @Override
        public ImmutableList<T> get() {
            return impl.get();
        }
        
        @Override
        public boolean isEmpty() {
            return impl.get().isEmpty();
        }

        @Override
        public void set(List<T> value) {
            impl.set(ImmutableList.copyOf(value));
        }

        @Override
        public void setSilently(List<T> value) {
            impl.setSilently(ImmutableList.copyOf(value));
        }

        @Override
        public void clear() {
            impl.set(ImmutableList.of());
        }
        
        @Override
        public void sort(Comparator<? super T> comparator) {
            requireNonNull(comparator);
            synchronized (this) {
                ArrayList<T> copy = new ArrayList<>(impl.get());
                copy.sort(comparator);
                impl.set(ImmutableList.copyOf(copy));
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
