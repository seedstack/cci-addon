/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.addons.cci.internal;

import org.seedstack.shed.exception.ErrorCode;

enum CciErrorCode implements ErrorCode {
    CANNOT_CREATE_CONNECTION_FACTORY,
    BAD_INTERACTION_DEFINITION,
    MISSING_INPUT_FOR_INTERACTION,
    INTERACTION_EXECUTION_FAILED,
    INTERACTION_HAS_MORE_THAN_ONE_INPUT,
    NO_JNDI_CONTEXT, MISSING_JNDI_CONTEXT, JNDI_LOOKUP_ERROR, UNABLE_TO_CREATE_CONNECTION_FACTORY,
    MISCONFIGURED_CONNECTION_FACTORY, UNRECOGNIZED_CONNECTION_FACTORY, UNABLE_TO_SET_PROPERTY, PROPERTY_NOT_FOUND,
    AMBIGUOUS_CONNECTION_FACTORY, CONNECTION_FACTORY_MISMATCH, UNKNOWN_CONNECTION_FACTORY,
    NO_CONNECTION_FACTORY_CONFIGURED, INTERACTION_HAS_MORE_THAN_ONE_OUTPUT
}
