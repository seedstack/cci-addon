/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.cci;

import com.google.common.base.Strings;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.resource.cci.ConnectionFactory;
import org.seedstack.coffig.Config;

@Config("cci")
public class CciConfig {
    private Map<String, ConnectionFactoryConfig> connectionFactories = new HashMap<>();

    public Map<String, ConnectionFactoryConfig> getConnectionFactories() {
        return Collections.unmodifiableMap(connectionFactories);
    }

    public CciConfig addConnectionFactory(String name, ConnectionFactoryConfig connectionFactoryConfig) {
        this.connectionFactories.put(name, connectionFactoryConfig);
        return this;
    }

    public static class ConnectionFactoryConfig {
        private String jndiName;
        private String jndiContext;
        private Class<? extends ConnectionFactory> vendorClass;
        private Properties properties = new Properties();

        public boolean isConfiguredWithJndi() {
            return !Strings.isNullOrEmpty(jndiName);
        }

        public boolean isConfiguredForVendor() {
            return vendorClass != null;
        }

        public String getJndiName() {
            return jndiName;
        }

        public ConnectionFactoryConfig setJndiName(String jndiName) {
            this.jndiName = jndiName;
            return this;
        }

        public String getJndiContext() {
            return jndiContext;
        }

        public ConnectionFactoryConfig setJndiContext(String jndiContext) {
            this.jndiContext = jndiContext;
            return this;
        }

        public Class<? extends ConnectionFactory> getVendorClass() {
            return vendorClass;
        }

        public ConnectionFactoryConfig setVendorClass(Class<? extends ConnectionFactory> vendorClass) {
            this.vendorClass = vendorClass;
            return this;
        }

        public Properties getProperties() {
            return properties;
        }

        public ConnectionFactoryConfig addProperty(String name, String value) {
            this.properties.setProperty(name, value);
            return this;
        }
    }
}
