package io.dovid.multitimer.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by umber on 12/24/2017.
 */

public class MyCollectionUtils {

    /**
     * The two collections have the same number of elements
     *
     * @param c1 the first collection of Objects
     * @param c2 second collection of Objects
     * @return the indexes of all changed elements
     */
    public static <T> Integer[] indexesOfChangedElements(List<T> c1, List<T> c2) {
        if (c1.size() != c2.size()) {
            throw new IllegalArgumentException("the two list must have the same size");
        }

        ArrayList<Integer> l = new ArrayList<>();
        for (int i = 0; i < c1.size(); i++) {
            if (!c1.get(i).equals(c2.get(i))) {
                l.add(i);
            }
        }
        Integer a[] = new Integer[l.size()];
        return l.toArray(a);
    }
}
