package org.cy3sabiork;

import org.apache.commons.io.IOUtils;
import org.cy3sabiork.rest.SabioQuery;
import org.cy3sabiork.rest.SabioQueryUniRest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.*;


public class SabioKineticLawTest {
    private SabioKineticLaw sabioLaw;

    @Before
    public void setUp() {
        sabioLaw = new SabioKineticLaw(0, 123, "homo sapiens", "liver", "galactokinase");
    }

    @After
    public void tearDown() {
        sabioLaw = null;
    }


    @Test
    public void getCount() throws Exception {
        Integer count = sabioLaw.getCount();
        assertEquals("Test index of SabioKineticLaw", new Integer(0), count);
    }

    @Test
    public void getId() throws Exception {
        Integer id = sabioLaw.getId();
        assertEquals("Test id of SabioKineticLaw", new Integer(123), id);
    }

    @Test
    public void getOrganism() throws Exception {
        String organism = sabioLaw.getOrganism();
        assertEquals("Test organism of SabioKineticLaw", "homo sapiens", organism);
    }

    @Test
    public void getTissue() throws Exception {
        String tissue = sabioLaw.getTissue();
        assertEquals("Test tissue of SabioKineticLaw", "liver", tissue);
    }

    @Test
    public void getReaction() throws Exception {
        String reaction = sabioLaw.getReaction();
        assertEquals("Test reaction of SabioKineticLaw", "galactokinase", reaction);
    }

    @Test
    public void parseIds() throws Exception {
        String text = "123 234,4;10";
        HashSet<Integer> ids = SabioKineticLaw.parseIds(text);

        assertEquals(4, ids.size());
        assertTrue(ids.contains(123));
        assertTrue(ids.contains(234));
        assertTrue(ids.contains(4));
        assertTrue(ids.contains(10));
    }

    @Test
    public void parseKineticLaws() throws Exception {
        String sbml = IOUtils.toString(
                this.getClass().getResourceAsStream("kineticLaw123.xml"),
                "UTF-8"
        );
        ArrayList<SabioKineticLaw> list = SabioKineticLaw.parseKineticLaws(sbml);

        assertNotNull(list);
        assertEquals(1, list.size());

        SabioKineticLaw sabioLaw = list.get(0);
        assertEquals(new Integer(1), sabioLaw.getCount());
        assertEquals(new Integer(123), sabioLaw.getId());
        assertEquals("562", sabioLaw.getOrganism());
    }

}