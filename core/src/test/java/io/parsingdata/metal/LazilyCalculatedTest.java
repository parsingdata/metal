/*
* Copyright 2017 SWAT.engineering BV
*
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package io.parsingdata.metal;


import org.junit.Test;

import java.lang.ref.Reference;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class LazilyCalculatedTest {

    // as concurrent collections from null values, we have to wrap them
    private static class NullableObjectReference {
        private final Object value;

        public NullableObjectReference(Object value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            return ((NullableObjectReference)obj).value == this.value;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(value);
        }
    }

    private Collection<Object> getMultithreaded(LazilyCalculated<Object> target, int threads, int tries) {
        Semaphore wait = new Semaphore(0);
        Semaphore done = new Semaphore(0);
        final Collection<NullableObjectReference> result = new ConcurrentLinkedDeque<>();
        for (int t = 0; t < threads; t++) {
            new Thread(() -> {
                try {
                    wait.acquire();
                    for (int i = 0; i < tries; i ++) {
                        result.add(new NullableObjectReference(target.get()));
                    }
                }
                catch (InterruptedException e) { }
                finally {
                    done.release();
                }
            }).start();
        }
        try {
            wait.release(threads); // start all threads at the same time
            done.acquire(threads); // wait for all threads to finish
            assertEquals(threads * tries, result.size());
            return result.stream().map(o -> o.value).collect(Collectors.toList());
        }
        catch (InterruptedException e) {
            fail(e.toString());
            return null; // dead code as fail throws exception
        }
    }

    @Test
    public void nonLockingCodeWorksWithMultipleThreads() {
        Set<Object> seen = new HashSet<>(getMultithreaded(Lazily.calculate(Object::new), 50, 10));
        assertFalse("The lazily loading value should never return null", seen.contains(null));
        assertTrue("There should be at least one result", seen.size() > 0);
    }

    @Test
    public void nonLockingCodeOnlyReturnsOneResult() {
        Set<Object> seen = new HashSet<>(getMultithreaded(Lazily.calculateOnlyOnce(Object::new), 50, 10));
        assertFalse("The lazily loading value should never return null", seen.contains(null));
        assertTrue("There should be exactly one result", seen.size() == 1);
    }

}
