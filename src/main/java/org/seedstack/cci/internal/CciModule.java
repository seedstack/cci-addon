/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.cci.internal;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import java.util.Map;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import javax.resource.cci.Record;
import org.seedstack.cci.ManagedInteraction;

class CciModule extends AbstractModule {
    private final Map<String, ConnectionFactory> connectionFactories;
    private final Map<String, InteractionDef<Record, Record>> interactionsDefs;

    CciModule(Map<String, ConnectionFactory> connectionFactories,
            Map<String, InteractionDef<Record, Record>> interactionsDefs) {
        this.connectionFactories = connectionFactories;
        this.interactionsDefs = interactionsDefs;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void configure() {
        install(new FactoryModuleBuilder().build(CciFactory.class));

        for (Map.Entry<String, ConnectionFactory> entry : connectionFactories.entrySet()) {
            bind(ConnectionFactory.class).annotatedWith(Names.named(entry.getKey())).toInstance(entry.getValue());
        }

        for (InteractionDef<Record, Record> interactionDef : interactionsDefs.values()) {
            bind((TypeLiteral<ManagedInteraction>) TypeLiteral.get(
                    Types.newParameterizedType(ManagedInteraction.class,
                            interactionDef.getInputClass(),
                            interactionDef.getOutputClass())))
                    .annotatedWith(Names.named(interactionDef.getName()))
                    .toProvider(new ManagedInteractionProvider(interactionDef));

            bind(Interaction.class)
                    .annotatedWith(Names.named(interactionDef.getName()))
                    .toProvider(new InteractionProvider(interactionDef));
        }
    }
}
