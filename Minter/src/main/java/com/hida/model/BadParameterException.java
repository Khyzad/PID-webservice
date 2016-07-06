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

/**
 * An exception that was created to display what parameter was incorrect and the value
 * that caused an error.
 * @author lruffin
 */
public class BadParameterException extends Exception {
    private Object Parameter;
    private String ParameterType;
    
    /**
     * Creates a new instance of <code>BadParameterException</code> without
     * detail message.
     */
    public BadParameterException() {
    }

    public BadParameterException(Object parameter, String parameterType){        
        Parameter = parameter ;
        ParameterType = parameterType;
    }        
    
    @Override
    public String getMessage(){
        return String.format("An invalid '%s' was detected: %s", ParameterType, Parameter);
    }
}
