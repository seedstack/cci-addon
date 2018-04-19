/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.cci;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import org.junit.Test;
import org.seedstack.cci.fixtures.JndiConnectionFactory;
import org.seedstack.cci.fixtures.TestInputRecord;
import org.seedstack.cci.fixtures.TestInteractionSpec;
import org.seedstack.cci.fixtures.TestOutputRecord;
import org.seedstack.cci.fixtures.VendorConnectionFactory;
import org.seedstack.seed.it.AbstractSeedIT;

public class CciIT extends AbstractSeedIT {
    @Inject
    @Named("someInteraction")
    private ManagedInteraction<TestInputRecord, TestOutputRecord> someInteraction;

    @Inject
    @Named("someInteraction")
    private Interaction someLegacyInteraction;

    @Inject
    @Named("vendorCf")
    private ConnectionFactory vendorConnectionFactory;

    @Inject
    @Named("jndiCf")
    private ConnectionFactory jndiConnectionFactory;

    @Test
    public void interactionIsInjectable() throws Exception {
        assertThat(someInteraction).isNotNull();
        assertThat(someLegacyInteraction).isNotNull();
    }

    @Test
    public void connectionFactoryIsInjectable() throws Exception {
        assertThat(vendorConnectionFactory).isInstanceOf(VendorConnectionFactory.class);
        assertThat(((VendorConnectionFactory) vendorConnectionFactory).getHostName()).isEqualTo("localhost");
        assertThat(((VendorConnectionFactory) vendorConnectionFactory).getPort()).isEqualTo(2564);
        assertThat(((VendorConnectionFactory) vendorConnectionFactory).getDataStoreName()).isEqualTo("test");
        assertThat(jndiConnectionFactory).isInstanceOf(JndiConnectionFactory.class);
    }

    @Test
    public void interactionIsWorking() throws Exception {
        assertThat(someInteraction.execute(
                new TestInteractionSpec(),
                new TestInputRecord(),
                new TestOutputRecord())).isTrue();
        assertThat(someInteraction.execute(new TestInteractionSpec(), new TestInputRecord())).isInstanceOf(
                TestOutputRecord.class);
        assertThat(someInteraction.getWarnings().getMessage()).isEqualTo("test");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void interactionCannotBeClosed() throws Exception {
        someLegacyInteraction.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void connectionCannotBeRetrieved() throws Exception {
        someLegacyInteraction.getConnection();
    }
}
