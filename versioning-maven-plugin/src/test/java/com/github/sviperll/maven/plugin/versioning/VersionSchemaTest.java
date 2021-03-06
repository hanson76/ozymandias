/*
 * Copyright (c) 2015, Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.sviperll.maven.plugin.versioning;

import edu.emory.mathcs.backport.java.util.Arrays;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
public class VersionSchemaTest {
    
    /**
     * Test of isPredecessorSuffix method, of class VersionSchema.
     */
    @Test
    public void testFinalVersion() {
        VersionSchema schema = createTestSchema();
        assertEquals("", schema.getCanonicalFinalSuffix());
        assertEquals("final", schema.getNonEmptyFinalSuffix());

        assertEquals("", schema.finalVersionComponent().getSuffixString());
        assertTrue(schema.finalVersionComponent().isFinalComponent());
        assertFalse(schema.finalVersionComponent().allowsMoreComponents());
        assertArrayEqualsWhenSorted(new String[] {"Final", "GA", "final"}, schema.getSuffixVariants(""));
        assertArrayEqualsWhenSorted(new String[] {"Final", "GA", "final"}, schema.getSuffixVariants("final"));
    }

    @Test
    public void testComparisons() {
        VersionSchema schema = createTestSchema();
        assertTrue(schema.version("1.0-alpha1").compareTo(schema.version("1.0-alpha1")) == 0);
        assertTrue(schema.version("1.0-alpha1").compareTo(schema.version("1.0-alpha2")) < 0);
        assertTrue(schema.version("1.0-alpha2").compareTo(schema.version("1.0-alpha1")) > 0);
        assertTrue(schema.version("1.0-beta1").compareTo(schema.version("1.0-alpha2")) > 0);
    }

    @Test
    public void testPrompt() {
        VersionSchema schema = createTestSchema();
        String baseVersion = "2.0-beta-SNAPSHOT";
        VersionComponentScanner scanner = schema.createScanner(baseVersion);
        VersionComponentInstance numbers = scanner.getNextComponentInstance();
        VersionComponentInstance suffix = scanner.getNextComponentInstance();
        VersionComponentInstance suffixExtention = scanner.getNextComponentInstance();
        Version base = schema.version(baseVersion);
        VersionComponent requiredSuffix = schema.suffixComponent("Beta");
        Version candidate = schema.versionOf(numbers, requiredSuffix.withTheSameSeparator(suffix));

        assertTrue(schema.compareSuffixes("beta", "Beta") == 0);
        assertTrue(candidate.compareTo(base) >= 0);
    }

    @Test
    public void testCompareSuffixes() {
        VersionSchema schema = createTestSchema();
        assertTrue(schema.compareSuffixes("beta", "Beta") == 0);
    }

    @Test
    public void testCanonicalSuffix() {
        VersionSchema schema = createTestSchema();
        assertEquals(schema.getCanonicalSuffix("beta"), schema.getCanonicalSuffix("Beta"));
        assertEquals(schema.getCanonicalSuffix("b"), schema.getCanonicalSuffix("Beta"));
    }

    private VersionSchema createTestSchema() {
        VersionSchema.Builder builder = new VersionSchema.Builder();

        VersionSchema.SuffixBuilder suffix = builder.getSuffixBuilder("Alpha");
        suffix.addVariant("a");
        suffix.addVariant("alpha");
        suffix.setCanonicalString("alpha");
        suffix.setPredecessor(true);
        suffix.setOrderingIndex(1);

        suffix = builder.getSuffixBuilder("Beta");
        suffix.addVariant("b");
        suffix.addVariant("beta");
        suffix.setCanonicalString("beta");
        suffix.setPredecessor(true);
        suffix.setOrderingIndex(2);

        suffix = builder.getSuffixBuilder("CR");
        suffix.addVariant("RC");
        suffix.addVariant("rc");
        suffix.setCanonicalString("rc");
        suffix.setPredecessor(true);
        suffix.setOrderingIndex(4);

        suffix = builder.getSuffixBuilder("SNAPSHOT");
        suffix.setPredecessor(true);
        suffix.setOrderingIndex(5);

        suffix = builder.getFinalSuffixBuilder();
        suffix.addVariant("final");
        suffix.addVariant("Final");
        suffix.addVariant("GA");

        return builder.build();
    }

    private void assertArrayEqualsWhenSorted(String[] expected, String[] actual) {
        Arrays.sort(expected);
        Arrays.sort(actual);
        assertArrayEquals(expected, actual);
    }
}
