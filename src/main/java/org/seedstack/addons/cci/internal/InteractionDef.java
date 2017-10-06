/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.addons.cci.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Record;
import org.seedstack.seed.SeedException;

class InteractionDef<I extends Record, O extends Record> {
    private final String name;
    private final Class<I> inputClass;
    private final ConnectionFactory connectionFactory;
    private Class<O> outputClass;

    InteractionDef(String name, Class<I> inputClass, ConnectionFactory connectionFactory) {
        this.name = name;
        this.inputClass = checkNotNull(inputClass);
        this.connectionFactory = checkNotNull(connectionFactory);
    }

    void validateConnectionFactory(ConnectionFactory connectionFactory) {
        if (this.connectionFactory != connectionFactory) {
            throw SeedException.createNew(CciErrorCode.CONNECTION_FACTORY_MISMATCH)
                    .put("class", outputClass)
                    .put("interaction", name)
                    .put("first", this.connectionFactory)
                    .put("second", connectionFactory);
        }
    }

    String getName() {
        return name;
    }

    Class<I> getInputClass() {
        return inputClass;
    }

    Class<O> getOutputClass() {
        return outputClass;
    }

    void setOutputClass(Class<O> outputClass) {
        if (this.outputClass != null) {
            throw SeedException.createNew(CciErrorCode.INTERACTION_HAS_MORE_THAN_ONE_OUTPUT)
                    .put("class", outputClass)
                    .put("interaction", name);
        }
        this.outputClass = checkNotNull(outputClass);
    }

    ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}
