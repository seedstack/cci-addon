/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.addons.cci.fixtures;

import javax.resource.cci.Record;
import org.seedstack.addons.cci.InteractionInput;

@InteractionInput("someInteraction")
public class TestInputRecord implements Record {
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
