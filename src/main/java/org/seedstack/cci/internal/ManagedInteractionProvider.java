/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.cci.internal;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.resource.cci.Record;
import org.seedstack.cci.ManagedInteraction;

class ManagedInteractionProvider implements Provider<ManagedInteraction> {
    private final InteractionDef<Record, Record> interactionDef;
    @Inject
    private CciFactory cciFactory;

    ManagedInteractionProvider(InteractionDef<Record, Record> interactionDef) {
        this.interactionDef = interactionDef;
    }

    @Override
    public ManagedInteraction<Record, Record> get() {
        return cciFactory.createManagedInteractionImpl(interactionDef);
    }
}
