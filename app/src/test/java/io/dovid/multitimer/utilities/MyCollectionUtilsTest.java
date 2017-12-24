package io.dovid.multitimer.utilities;

import org.junit.Test;

import java.util.ArrayList;

import io.dovid.multitimer.model.TimerEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by umber on 12/24/2017.
 */
public class MyCollectionUtilsTest {

    @Test
    public void indexesOfChangedElements() {
        sameElements();
        differentElements();
        someDifferentElements();
        differentSizes();
    }

    @Test
    public void sameElements() {
        ArrayList<TimerEntity> l1 = new ArrayList<>();
        ArrayList<TimerEntity> l2 = new ArrayList<>();

        // Test 1: Lists all elements equals
        TimerEntity t1 = new TimerEntity(1, "timer1", 0, 0, true, true);
        TimerEntity t2 = new TimerEntity(2, "timer2", 0, 0, true, true);
        TimerEntity t3 = new TimerEntity(3, "timer3", 0, 0, true, true);
        l1.add(t1);
        l1.add(t2);
        l1.add(t3);
        t1 = new TimerEntity(1, "timer1", 0, 0, true, true);
        t2 = new TimerEntity(2, "timer2", 0, 0, true, true);
        t3 = new TimerEntity(3, "timer3", 0, 0, true, true);
        l2.add(t1);
        l2.add(t2);
        l2.add(t3);
        Integer indexes[] = MyCollectionUtils.indexesOfChangedElements(l1, l2);
        assertEquals(0, indexes.length);
    }

    @Test
    public void differentElements() {
        ArrayList<TimerEntity> l1 = new ArrayList<>();
        ArrayList<TimerEntity> l2 = new ArrayList<>();
        TimerEntity t1 = new TimerEntity(1, "timer1", 0, 0, true, true);
        TimerEntity t2 = new TimerEntity(2, "timer2", 0, 0, true, true);
        TimerEntity t3 = new TimerEntity(3, "timer3", 0, 0, true, true);
        l1.add(t1);
        l1.add(t2);
        l1.add(t3);
        t1 = new TimerEntity(1, "timer4", 0, 0, true, true);
        t2 = new TimerEntity(2, "timer5", 0, 0, true, true);
        t3 = new TimerEntity(3, "timer6", 0, 0, true, true);
        l2.add(t1);
        l2.add(t2);
        l2.add(t3);
        Integer indexes[] = MyCollectionUtils.indexesOfChangedElements(l1, l2);
        assertEquals(3, indexes.length);
    }

    @Test
    public void someDifferentElements() {
        ArrayList<TimerEntity> l1 = new ArrayList<>();
        ArrayList<TimerEntity> l2 = new ArrayList<>();
        TimerEntity t1 = new TimerEntity(1, "timer1", 0, 0, true, true);
        TimerEntity t2 = new TimerEntity(2, "timer2", 0, 0, true, true);
        TimerEntity t3 = new TimerEntity(3, "timer3", 0, 0, true, true);
        l1.add(t1);
        l1.add(t2);
        l1.add(t3);
        t1 = new TimerEntity(1, "timer1", 0, 0, true, true);
        t2 = new TimerEntity(2, "timer5", 0, 0, true, true);
        t3 = new TimerEntity(3, "timer3", 0, 0, true, true);
        l2.add(t1);
        l2.add(t2);
        l2.add(t3);
        Integer indexes[] = MyCollectionUtils.indexesOfChangedElements(l1, l2);
        assertEquals(1, indexes.length);
        assertEquals(1, (int) indexes[0]);
    }

    @Test
    public void differentSizes() {
        ArrayList<TimerEntity> l1 = new ArrayList<>();
        ArrayList<TimerEntity> l2 = new ArrayList<>();
        TimerEntity t1 = new TimerEntity(1, "timer1", 0, 0, true, true);
        TimerEntity t2 = new TimerEntity(2, "timer2", 0, 0, true, true);
        TimerEntity t3 = new TimerEntity(3, "timer3", 0, 0, true, true);
        l1.add(t1);
        l1.add(t2);
        l1.add(t3);
        l2.add(t1);
        boolean success = true;
        try {
            MyCollectionUtils.indexesOfChangedElements(l1, l2);
        } catch (IllegalArgumentException e) {
            success = false;
        }

        assertFalse(success);
    }
}