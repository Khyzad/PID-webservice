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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception used to display how many permutations actually remain and the
 * requested amount that caused an error.
 *
 * @author lruffin
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason
        = "Requested amount exceeds possible number "
        + "of permutations")
public class NotEnoughPermutationsException extends RuntimeException {

    private long remainingPermutations_;
    private long requestedAmount_;

    public NotEnoughPermutationsException(long remaining, long requested) {
        this.remainingPermutations_ = remaining;
        this.requestedAmount_ = requested;
    }

    /**
     * Creates a new instance of <code>TooManyPermutationsException</code>
     * without detail message.
     */
    public NotEnoughPermutationsException() {
    }

    @Override
    public String getMessage() {
        return String.format("%d ids were requested but only %d can be created using given format",
                requestedAmount_, remainingPermutations_);
    }
}
