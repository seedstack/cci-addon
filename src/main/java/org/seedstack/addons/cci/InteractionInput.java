/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Creation : 3 juin 2016
 */

package org.seedstack.addons.cci;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class to be used as a CCI interaction input.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface InteractionInput {
    /**
     * The name of the transaction the annotated class serves as input.
     *
     * @return the transaction name.
     */
    String[] value();

    /**
     * The name of the connection factory to use for the interaction(s).
     *
     * @return the name of the connection factory.
     */
    String connectionFactory() default "";
}
