/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.addons.cci.internal;

import static com.google.common.base.Preconditions.checkState;

import com.google.inject.assistedinject.Assisted;
import java.util.function.Function;
import javax.inject.Inject;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import javax.resource.cci.ResourceWarning;
import org.seedstack.addons.cci.ManagedInteraction;
import org.seedstack.seed.SeedException;
import org.seedstack.shed.exception.Throwing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedInteractionImpl<I extends Record, O extends Record> implements ManagedInteraction<I, O> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedInteractionImpl.class);
    private final InteractionDef<I, O> interactionDef;
    private ResourceWarning warnings;

    @Inject
    ManagedInteractionImpl(@Assisted InteractionDef<I, O> interactionDef) {
        this.interactionDef = interactionDef;
    }

    @Override
    public boolean execute(InteractionSpec ispec, I input, O output) {
        return doWithInteraction(
                (Throwing.Function<Interaction, Boolean, ResourceException>) interaction -> {
                    checkInput(input);
                    checkOutput(output);
                    boolean result = interaction.execute(ispec, input, output);
                    warnings = interaction.getWarnings();
                    return result;
                });
    }

    @Override
    @SuppressWarnings("unchecked")
    public O execute(InteractionSpec ispec, I input) {
        return doWithInteraction(
                (Throwing.Function<Interaction, O, ResourceException>) interaction -> {
                    checkInput(input);
                    O output = (O) interaction.execute(ispec, input);
                    warnings = interaction.getWarnings();
                    return output;
                });
    }

    @Override
    public ResourceWarning getWarnings() {
        return warnings;
    }

    @Override
    public void clearWarnings() {
        warnings = null;
    }

    private void checkInput(I input) {
        checkState(interactionDef.getInputClass().isAssignableFrom(input.getClass()),
                "The input object for interaction " + interactionDef.getName() + " must be assignable to "
                        + interactionDef.getInputClass().getName());
    }

    private void checkOutput(O output) {
        checkState(interactionDef.getOutputClass().isAssignableFrom(output.getClass()),
                "The output object for interaction " + interactionDef.getName() + " must be assignable to "
                        + interactionDef.getOutputClass().getName());
    }

    private <R> R doWithInteraction(Function<Interaction, R> interactionFunction) {
        Connection cxn = null;
        Interaction ixn = null;

        try {
            cxn = interactionDef.getConnectionFactory().getConnection();
            LOGGER.debug("Retrieved connection {} for interaction {}", cxn.hashCode(), interactionDef.getName());
            ixn = cxn.createInteraction();
            LOGGER.debug("Executing interaction {}", interactionDef.getName());
            return interactionFunction.apply(ixn);
        } catch (ResourceException e) {
            throw SeedException.wrap(e, CciErrorCode.INTERACTION_EXECUTION_FAILED)
                    .put("interaction", interactionDef.getName())
                    .put("error", e.getMessage());
        } finally {
            if (ixn != null) {
                try {
                    LOGGER.debug("Closing interaction: ", cxn.hashCode());
                    ixn.close();
                } catch (ResourceException e) {
                    LOGGER.error("Exception while closing interaction : ", e);
                }
            }
            if (cxn != null) {
                try {
                    LOGGER.debug("Closing connection: ", cxn.hashCode());
                    cxn.close();
                } catch (ResourceException e) {
                    LOGGER.error("Exception while closing connection", e);
                }
            }
        }
    }
}
