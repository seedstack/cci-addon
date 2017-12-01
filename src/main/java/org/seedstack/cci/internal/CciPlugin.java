/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.cci.internal;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Record;
import jodd.bean.BeanUtil;
import jodd.bean.BeanUtilBean;
import org.seedstack.cci.CciConfig;
import org.seedstack.cci.InteractionInput;
import org.seedstack.cci.InteractionOutput;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.seedstack.seed.core.internal.jndi.JndiPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This plugin provides CCI support.
 */
public class CciPlugin extends AbstractSeedPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(CciPlugin.class);
    private final Map<String, ConnectionFactory> connectionFactories = new HashMap<>();
    private final Map<String, InteractionDef<Record, Record>> interactionsDefs = new HashMap<>();
    private Map<String, Context> jndiContexts;

    @Override
    public String name() {
        return "cci";
    }

    @Override
    protected Collection<Class<?>> dependencies() {
        return Lists.newArrayList(JndiPlugin.class);
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return classpathScanRequestBuilder()
                .annotationType(InteractionInput.class)
                .annotationType(InteractionOutput.class)
                .build();
    }

    @Override
    public InitState initialize(InitContext initContext) {
        CciConfig cciConfig = getConfiguration(CciConfig.class);
        jndiContexts = initContext.dependency(JndiPlugin.class).getJndiContexts();
        configureConnectionFactories(cciConfig);
        configureInteractions(
                initContext.scannedClassesByAnnotationClass().get(InteractionInput.class),
                initContext.scannedClassesByAnnotationClass().get(InteractionOutput.class)
        );
        return InitState.INITIALIZED;
    }

    private void configureConnectionFactories(CciConfig cciConfig) {
        for (Map.Entry<String, CciConfig.ConnectionFactoryConfig> entry : cciConfig.getConnectionFactories()
                .entrySet()) {
            CciConfig.ConnectionFactoryConfig cfConfig = entry.getValue();
            String cfName = entry.getKey();
            Object connectionFactory;

            if (cfConfig.isConfiguredWithJndi()) {
                connectionFactory = lookupJndiConnectionFactory(cfName, cfConfig.getJndiContext(),
                        cfConfig.getJndiName());
            } else if (cfConfig.isConfiguredForVendor()) {
                connectionFactory = createVendorConnectionFactory(cfName, cfConfig.getVendorClass(),
                        cfConfig.getProperties());
            } else {
                throw SeedException.createNew(CciErrorCode.MISCONFIGURED_CONNECTION_FACTORY)
                        .put("connectionFactoryName", cfName);
            }

            if (!ConnectionFactory.class.isAssignableFrom(connectionFactory.getClass())) {
                throw SeedException.createNew(CciErrorCode.UNRECOGNIZED_CONNECTION_FACTORY)
                        .put("class", connectionFactory.getClass());
            }

            connectionFactories.put(cfName, (ConnectionFactory) connectionFactory);
        }
    }

    private Object createVendorConnectionFactory(String cfName, Class<? extends ConnectionFactory> cfClass,
            Properties properties) {
        try {
            ConnectionFactory connectionFactory = cfClass.newInstance();
            setProperties(connectionFactory, properties);
            return connectionFactory;
        } catch (Exception e) {
            throw SeedException.wrap(e, CciErrorCode.UNABLE_TO_CREATE_CONNECTION_FACTORY)
                    .put("connectionFactoryName", cfName);
        }
    }

    private Object lookupJndiConnectionFactory(String cfName, String jndiContext,
            String jndiName) {
        try {
            if (this.jndiContexts == null || this.jndiContexts.isEmpty()) {
                throw SeedException.createNew(CciErrorCode.NO_JNDI_CONTEXT)
                        .put("connectionFactoryName", cfName);
            }

            Context context = this.jndiContexts.get(jndiContext);
            if (context == null) {
                throw SeedException.createNew(CciErrorCode.MISSING_JNDI_CONTEXT)
                        .put("contextName", jndiContext)
                        .put("connectionFactoryName", cfName);
            }

            return context.lookup(jndiName);
        } catch (NamingException e) {
            throw SeedException.wrap(e, CciErrorCode.JNDI_LOOKUP_ERROR)
                    .put("connectionFactoryName", cfName);
        }
    }

    private void setProperties(Object bean, Properties properties) {
        BeanUtil beanUtil = new BeanUtilBean();
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            if (!beanUtil.hasProperty(bean, key)) {
                throw SeedException.createNew(CciErrorCode.PROPERTY_NOT_FOUND)
                        .put("property", key)
                        .put("class", bean.getClass().getCanonicalName());
            }
            try {
                beanUtil.setProperty(bean, key, value);
            } catch (Exception e) {
                throw SeedException.wrap(e, CciErrorCode.UNABLE_TO_SET_PROPERTY)
                        .put("property", key)
                        .put("class", bean.getClass().getCanonicalName())
                        .put("value", value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void configureInteractions(Collection<Class<?>> inputClasses, Collection<Class<?>> outputClasses) {
        for (Class<?> inputClass : inputClasses) {
            if (Record.class.isAssignableFrom(inputClass)) {
                InteractionInput input = inputClass.getAnnotation(InteractionInput.class);
                if (input != null) {
                    for (String interaction : input.value()) {
                        if (interactionsDefs.put(interaction, new InteractionDef(
                                interaction,
                                inputClass,
                                getConnectionFactory(inputClass, input.connectionFactory()))) != null) {
                            throw SeedException.createNew(CciErrorCode.INTERACTION_HAS_MORE_THAN_ONE_INPUT)
                                    .put("class", inputClass)
                                    .put("interaction", interaction);
                        }
                    }
                }
            }
        }

        for (Class<?> outputClass : outputClasses) {
            if (Record.class.isAssignableFrom(outputClass)) {
                InteractionOutput output = outputClass.getAnnotation(InteractionOutput.class);
                if (output != null) {
                    for (String interaction : output.value()) {
                        InteractionDef inputOutput = interactionsDefs.get(interaction);
                        if (inputOutput == null) {
                            throw SeedException.createNew(CciErrorCode.MISSING_INPUT_FOR_INTERACTION)
                                    .put("interaction", interaction);
                        }
                        inputOutput.validateConnectionFactory(
                                getConnectionFactory(outputClass, output.connectionFactory()));
                        inputOutput.setOutputClass(outputClass);
                    }
                }
            }
        }

        for (InteractionDef<Record, Record> interactionDef : interactionsDefs.values()) {
            LOGGER.info("Registered CCI interaction {} with [{}] as input and [{}] as output",
                    interactionDef.getName(),
                    interactionDef.getInputClass().getName(),
                    interactionDef.getOutputClass().getName());
        }
    }

    private ConnectionFactory getConnectionFactory(Class<?> someClass, String explicitCfName) {
        if (connectionFactories.isEmpty()) {
            throw SeedException.createNew(CciErrorCode.NO_CONNECTION_FACTORY_CONFIGURED);
        }
        if (Strings.isNullOrEmpty(explicitCfName)) {
            if (connectionFactories.size() == 1) {
                return connectionFactories.values().iterator().next();
            } else {
                throw SeedException.createNew(CciErrorCode.AMBIGUOUS_CONNECTION_FACTORY)
                        .put("class", someClass);
            }
        } else {
            ConnectionFactory connectionFactory = connectionFactories.get(explicitCfName);
            if (connectionFactory == null) {
                throw SeedException.createNew(CciErrorCode.UNKNOWN_CONNECTION_FACTORY)
                        .put("class", someClass)
                        .put("connectionFactoryName", explicitCfName);
            }
            return connectionFactory;
        }
    }

    @Override
    public Object nativeUnitModule() {
        return new CciModule(interactionsDefs);
    }

}
