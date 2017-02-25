/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.i2c.handler;

import static org.openhab.binding.i2c.I2CBindingConstants.BUS_ID;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.i2c.discovery.I2CDeviceDiscoveryService;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for the handling of discovered i2c busses.
 *
 * @author Alexander Falkenstern - Initial contribution
 */
public class I2CBridgeHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(I2CBridgeHandler.class);

    private I2CDeviceDiscoveryService service;
    private ServiceRegistration<?> registration;

    public I2CBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    public Integer getBusId() {
        Integer result = null;
        Configuration config = getConfig();
        if (config.containsKey(BUS_ID)) {
            Object entry = config.get(BUS_ID);
            if (entry instanceof Integer) {
                result = (Integer) entry;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleRemoval() {
        for (Thing thing : getThing().getThings()) {
            ThingHandler handler = thing.getHandler();
            handler.handleRemoval();
        }
        super.handleRemoval();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.info("Handle command {} on channel {}", command, channelUID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        if ((getBusId() != null) && (bundleContext != null)) {
            super.initialize();

            service = new I2CDeviceDiscoveryService(this);
            final Dictionary<String, ?> properties = new Hashtable<String, Object>();
            registration = bundleContext.registerService(DiscoveryService.class.getName(), service, properties);
            service.activate();
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "I2C bus id is null");
            dispose();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();

        if (service != null) {
            service.stopScan();
            if ((registration != null) && (bundleContext != null)) {
                ServiceReference<?> reference = registration.getReference();
                ((I2CDeviceDiscoveryService) bundleContext.getService(reference)).deactivate();

                registration.unregister();
                registration = null;
                service = null;
            }
        }
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        super.childHandlerInitialized(childHandler, childThing);
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        super.childHandlerDisposed(childHandler, childThing);
    }
}
