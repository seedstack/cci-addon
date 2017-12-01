/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.cci.fixtures;

import static org.assertj.core.api.Assertions.assertThat;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import javax.resource.cci.ResourceWarning;

public class TestInteraction implements Interaction {
    @Override
    public void close() throws ResourceException {

    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public boolean execute(InteractionSpec ispec, Record input, Record output) throws ResourceException {
        assertThat(input).isInstanceOf(TestInputRecord.class);
        assertThat(output).isInstanceOf(TestOutputRecord.class);
        return true;
    }

    @Override
    public Record execute(InteractionSpec ispec, Record input) throws ResourceException {
        assertThat(input).isInstanceOf(TestInputRecord.class);
        return new TestOutputRecord();
    }

    @Override
    public ResourceWarning getWarnings() throws ResourceException {
        return new ResourceWarning("test");
    }

    @Override
    public void clearWarnings() throws ResourceException {

    }
}
