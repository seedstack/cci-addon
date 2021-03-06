/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.cci;

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
public @interface InteractionOutput {
    /**
     * The name of the transaction the annotated class serves as output.
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
