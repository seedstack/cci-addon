/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.addons.cci.internal;

import com.google.inject.assistedinject.Assisted;
import javax.inject.Inject;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import javax.resource.cci.ResourceWarning;
import org.seedstack.addons.cci.ManagedInteraction;

public class InteractionAdapter implements Interaction {
    private final ManagedInteraction<Record, Record> managedInteraction;

    @Inject
    InteractionAdapter(@Assisted InteractionDef<Record, Record> interactionDef, CciFactory cciFactory) {
        this.managedInteraction = cciFactory.createManagedInteractionImpl(interactionDef);
    }

    @Override
    public void close() throws ResourceException {
        throw new UnsupportedOperationException("Closing is not supported on managed interactions");
    }

    @Override
    public Connection getConnection() {
        throw new UnsupportedOperationException("Accessing connection is not supported on managed interactions");
    }

    @Override
    public boolean execute(InteractionSpec ispec, Record input, Record output) throws ResourceException {
        return managedInteraction.execute(ispec, input, output);
    }

    @Override
    public Record execute(InteractionSpec ispec, Record input) throws ResourceException {
        return managedInteraction.execute(ispec, input);
    }

    @Override
    public ResourceWarning getWarnings() throws ResourceException {
        return managedInteraction.getWarnings();
    }

    @Override
    public void clearWarnings() throws ResourceException {
        managedInteraction.clearWarnings();
    }
}
