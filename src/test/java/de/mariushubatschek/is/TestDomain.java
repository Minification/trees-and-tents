package de.mariushubatschek.is;

import de.mariushubatschek.is.modeling.Domain;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDomain {

    @Test
    public void testValueInDomain() {
        String[] values = new String[] {"a", "b", "c"};
        Domain domain = new Domain(values);
        for (int i = 0; i < values.length; i++) {
            assertTrue(domain.isValueInDom(i));
        }
        assertEquals(Arrays.asList(0, 1, 2), domain.getValues());
        assertEquals(3, domain.size());
    }

    @Test
    public void testRemoveValue() {
        String[] values = new String[] {"a", "b", "c"};
        Domain domain = new Domain(values);
        domain.removeValue(1, 3);
        assertEquals(Arrays.asList(0, 2), domain.getValues());
        assertEquals(2, domain.size());
    }

    @Test
    public void testRemoveAddValue() {
        String[] values = new String[] {"a", "b", "c"};
        Domain domain = new Domain(values);
        domain.removeValue(1, 3);
        domain.addValue(1);
        assertEquals(Arrays.asList(0, 1, 2), domain.getValues());
        assertEquals(3, domain.size());
    }

    @Test
    public void testReduceTo() {
        String[] values = new String[] {"a", "b", "c"};
        Domain domain = new Domain(values);
        domain.reduceTo(1, 3);
        assertEquals(Collections.singletonList(1), domain.getValues());
        assertEquals(1, domain.size());
    }

    @Test
    public void testRestoreTo() {
        String[] values = new String[] {"a", "b", "c"};
        Domain domain = new Domain(values);
        domain.reduceTo(1, 3);
        domain.restoreUpTo(3);
        assertEquals(Arrays.asList(0, 1, 2), domain.getValues());
        assertEquals(3, domain.size());
    }

}
