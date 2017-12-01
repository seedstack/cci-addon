/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.cci.internal;

import org.seedstack.shed.exception.ErrorCode;

enum CciErrorCode implements ErrorCode {
    AMBIGUOUS_CONNECTION_FACTORY,
    CONNECTION_FACTORY_MISMATCH,
    INTERACTION_EXECUTION_FAILED,
    INTERACTION_HAS_MORE_THAN_ONE_INPUT,
    INTERACTION_HAS_MORE_THAN_ONE_OUTPUT,
    JNDI_LOOKUP_ERROR,
    MISCONFIGURED_CONNECTION_FACTORY,
    MISSING_INPUT_FOR_INTERACTION,
    MISSING_JNDI_CONTEXT,
    NO_CONNECTION_FACTORY_CONFIGURED,
    NO_JNDI_CONTEXT,
    PROPERTY_NOT_FOUND,
    UNABLE_TO_CREATE_CONNECTION_FACTORY,
    UNABLE_TO_SET_PROPERTY,
    UNKNOWN_CONNECTION_FACTORY,
    UNRECOGNIZED_CONNECTION_FACTORY
}
