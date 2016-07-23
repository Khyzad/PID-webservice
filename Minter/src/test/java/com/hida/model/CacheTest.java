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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the functionality of the cache data structure
 * @author lruffin
 */
public class CacheTest {
       
    @Test(dependsOnMethods = {"testAdd"})
    public void testIsEmpty(){
        Assert.fail("unimplemented");
    }
    
    @Test(dependsOnMethods = {"testAdd"})
    public void testGetSize(){
        Assert.fail("unimplemented");
    }
    
    @Test
    public void testAdd(){
        Cache<Integer> c = new Cache<>(); 
        
        Assert.assertEquals(c.add(0), true);
        Assert.assertEquals(c.add(0), false);
    }
    
    @Test(dependsOnMethods = {"testAdd"})
    public void testPeek(){
        Assert.fail("unimplemented");
    }
    
    @Test(dependsOnMethods = {"testAdd"})
    public void testCollect(){
        Assert.fail("unimplemented");
    }
    
    @Test(dependsOnMethods = {"testAdd"})
    public void testRemoveAll(){
        Assert.fail("unimplemented");
    }
}
