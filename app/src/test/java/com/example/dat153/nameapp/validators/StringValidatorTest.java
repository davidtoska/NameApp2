package com.example.dat153.nameapp.validators;

import org.junit.Test;

import static com.example.dat153.nameapp.validators.StringValidator.validName;
import static org.junit.Assert.*;

/**
 * Created by cecilie on 08.02.2018.
 */

public class StringValidatorTest {
    @Test
    public void testvalidName() throws Exception {
        assertTrue(validName("Cecilie Gjørøy"));
        assertFalse(validName("David3 Toska"));
        assertFalse(validName("!Thomas Reite"));
        assertFalse(validName("Ce"));
    }

}