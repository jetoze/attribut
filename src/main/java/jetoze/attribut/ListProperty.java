package jetoze.attribut;

import java.util.Comparator;
import java.util.List;

public interface ListProperty<T> extends Property<List<T>> {
    
    // TODO: Define this when the property is created and remove this setter.
    void setDoDefensiveCopy(boolean value);

    void clear();
    
    void sort(Comparator<? super T> comparator);
    
}
