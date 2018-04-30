/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.cci.fixtures;

import javax.resource.cci.Record;
import org.seedstack.cci.InteractionOutput;

@InteractionOutput(value = "someInteraction", connectionFactory = "vendorCf")
public class TestOutputRecord implements Record {
    @Override
    public String getRecordName() {
        return null;
    }

    @Override
    public void setRecordName(String name) {

    }

    @Override
    public String getRecordShortDescription() {
        return null;
    }

    @Override
    public void setRecordShortDescription(String description) {

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return null;
    }
}
