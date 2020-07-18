package jetoze.attribut;

import java.util.Comparator;
import java.util.List;

import com.google.common.collect.ImmutableList;

public interface ListProperty<T> extends Property<List<T>> {

    ImmutableList<T> get();
    
    void clear();
    
    void sort(Comparator<? super T> comparator);
    
}
