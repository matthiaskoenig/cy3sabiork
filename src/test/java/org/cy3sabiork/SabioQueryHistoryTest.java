package org.cy3sabiork;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testing the QueryHistory.
 */
public class SabioQueryHistoryTest {

    @Test
    public void add() throws Exception {
        SabioQueryHistory h = new SabioQueryHistory();
        int size = h.getAll().size();
        h.add("test1");
        int size2 = h.getAll().size();
        assertEquals(size+1, size2);
        assertEquals("test1", h.get(0));

        // only added if not already in list
        h.add("test1");
        int size3 = h.getAll().size();
        assertEquals(size2, size3);
    }

    @Test
    public void get() throws Exception {
        SabioQueryHistory h = new SabioQueryHistory();
        int size = h.getAll().size();
        h.add("test2");
        assertEquals("test2", h.get(0));
    }

    @Test
    public void getAll() throws Exception {
        SabioQueryHistory h = new SabioQueryHistory();
        assertNotNull(h.getAll());
    }
}