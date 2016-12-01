package org.cy3sabiork;

import org.cy3sabiork.rest.SabioQuery;
import org.cy3sabiork.rest.SabioQueryUniRest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class SabioQueryUniRestTest {
    private SabioQueryUniRest unirest;

    @Before
    public void setUp() {
        unirest = new SabioQueryUniRest();
    }

    @After
    public void tearDown() {
        unirest = null;
    }

    @Test
    public void performQuery() throws Exception {
        String query = "searchKineticLaws/sbml?q=EntryID:\"123\"";
        SabioQueryResult result = unirest.performQuery(query);
        assertNotNull(result);
    }

    @Test
    public void performCountQuery() throws Exception {
        String query = "searchKineticLaws/sbml?q=EntryID:\"123\"";
        Integer count = unirest.performCountQuery(query);
        assertEquals("Testing count query", new Integer(1), count);
    }

    @Test
    public void getSabioStatus() throws Exception {
        String status = unirest.getSabioStatus();
        assertEquals("Test sabio status", SabioQuery.STATUS_UP, status);
    }

    @Test
    public void escapeTest01() throws Exception {
        String query = "searchKineticLaws/sbml?q=AnyRole:\" [(1->6)-alpha-d-xylo]-(1->4)-beta-d-glucan glucanohydrolase\"";
        Integer count = unirest.performCountQuery(query);
        assertTrue("Testing count query", count>0);
    }


}