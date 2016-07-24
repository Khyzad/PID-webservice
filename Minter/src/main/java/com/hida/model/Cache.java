/*
 * Copyright 2016 Lawrence Ruffin, Leland Lopez, Brittany Cruz, Stephen Anspach
 *
 * Developed in collaboration with the Hawaii State Digital Archives.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.hida.model;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A collection of unique values.
 *
 * @author lruffin
 * @param <T>
 */
public class Cache<T> {

    private long size_;
    private Set<T> set_ = new LinkedHashSet<>();

    public boolean add(T value) {
        if (!set_.contains(value)) {
            size_++;
            return set_.add(value);
        }
        else {
            return false;
        }
    }

    public long getSize() {
        return this.size_;
    }

    public boolean isEmpty() {
        return size_ == 0;
    }

    public Set<T> peek(long amount) {
        Set<T> set = new LinkedHashSet<>();

        int i = 0;
        Iterator<T> iter = set_.iterator();
        while (iter.hasNext() && i < amount) {
            set.add(iter.next());
        }

        return set;
    }

    public Set<T> collect(long amount) {
        return null;
    }

    public void removeAll() {

    }

}
