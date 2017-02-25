/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.i2c.handler;

import static org.openhab.binding.i2c.I2CBindingConstants.BUS_ID;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.i2c.discovery.I2CDeviceDiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enum class for the different bridge types.
 *
 * @author Alexander Falkenstern - Initial contribution
 */
public class I2CBridgeHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(I2CBridgeHandler.class);
    private static Map<Object, I2CDeviceDiscoveryService> services = new HashMap<Object, I2CDeviceDiscoveryService>();

    public I2CBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleRemoval() {
        super.handleRemoval();
        Configuration config = getConfig();
        if (config.containsKey(BUS_ID)) {
            Object entry = config.get(BUS_ID);
            if (services.containsKey(entry)) {
                services.remove(entry).deactivate();
            }
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.info("Handle command {} on channel {}", command, channelUID);
    }

    @Override
    public void initialize() {
        super.initialize();
        Configuration config = getConfig();
        if (config.containsKey(BUS_ID)) {
            Object id = config.get(BUS_ID);
            if ((id instanceof Integer) && !services.containsKey(id)) {
                services.put(id, new I2CDeviceDiscoveryService((Integer) id));
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

    @Override
    public void thingUpdated(Thing thing) {
        // TODO Auto-generated method stub
        super.thingUpdated(thing);
    }
}
