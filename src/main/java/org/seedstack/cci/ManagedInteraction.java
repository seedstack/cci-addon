/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.cci;

import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import javax.resource.cci.ResourceWarning;

/**
 * Provides the ability to execute EIS functions that have been detected by scanning classes annotated with
 * {@link InteractionInput}, optionally paired with a class annotated with an {@link InteractionOutput} annotation
 * with the same value.
 *
 * <p>
 * Consider the following input/output classes:
 * </p>
 * <pre>
 * {@literal @}InteractionInput("someInteraction")
 *  public class TestInputRecord implements Record {...}
 *
 * {@literal @}InteractionOutput("someInteraction")
 *  public class TestOutputRecord implements Record {...}
 * </pre>
 *
 * <p>
 * The managed interaction can be injected as:
 * </p>
 * <pre>
 * public class SomeClass {
 *   {@literal @}Inject
 *   {@literal @}Named("someInteraction")
 *    private ManagedInteraction&lt;TestInputRecord, TestOutputRecord&gt; someInteraction;
 * }
 * </pre>
 *
 * <p>
 * The managed interaction can also be injected as the plain {@link javax.resource.cci.Interaction} interface:
 * </p>
 * <pre>
 * public class SomeClass {
 *   {@literal @}Inject
 *   {@literal @}Named("someInteraction")
 *    private Interaction someInteraction;
 * }
 * </pre>
 *
 * @param <I> the input type of the EIS function.
 * @param <O> the output type of the EIS function.
 */
public interface ManagedInteraction<I extends Record, O extends Record> {
    /**
     * Executes the EIS function with the specified input and output.
     *
     * @param ispec  the properties driving the interaction.
     * @param input  the input object.
     * @param output the output object.
     * @return true if the interaction has succeeded, false otherwise.
     */
    boolean execute(InteractionSpec ispec, I input, O output);

    /**
     * Executes the EIS function with the specified input.
     *
     * @param ispec the properties driving the interaction.
     * @param input the input object.
     * @return the output object.
     */
    O execute(InteractionSpec ispec, I input);

    /**
     * Gets the first ResourceWarning from the chain of warnings associated with this Interaction instance.
     *
     * @return the first warning of the chain.
     */
    ResourceWarning getWarnings();

    /**
     * Clears all the warning reported by this Interaction instance. After a call to this method, the method
     * {@link #getWarnings()}  will return null until a new warning is reported for this Interaction.
     */
    void clearWarnings();
}
