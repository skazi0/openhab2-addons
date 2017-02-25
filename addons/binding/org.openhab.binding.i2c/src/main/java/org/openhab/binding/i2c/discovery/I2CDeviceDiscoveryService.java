/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.i2c.discovery;

import static org.openhab.binding.i2c.I2CBindingConstants.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.i2c.handler.I2CBridgeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 * Discovery service to scan for I2C devices.
 *
 * @author Alexander Falkenstern - Initial contribution
 */
public class I2CDeviceDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(I2CDeviceDiscoveryService.class);

    private static final int DISCOVERY_TIMEOUT = 30;
    private static final Set<ThingTypeUID> DISCOVERABLE_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_DEVICE);

    private I2CBridgeHandler bridge = null;

    public I2CDeviceDiscoveryService(I2CBridgeHandler bridge) {
        super(DISCOVERABLE_THING_TYPES_UIDS, DISCOVERY_TIMEOUT);
        this.bridge = bridge;
    }

    /**
     * Called on component activation, if the implementation of this class is an
     * OSGi declarative service and does not override the method. The method
     * implementation calls {@link AbstractDiscoveryService#startBackgroundDiscovery()} if background
     * discovery is enabled by default and not overridden by the configuration.
     */
    public void activate() {
        super.activate(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate() {
        super.deactivate();
        removeOlderResults(getTimestampOfLastScan());
    }

    @Override
    protected void startScan() {
        logger.debug("Starting scan for I2C devices.");

        I2CBus bus = null;
        try {
            bus = I2CFactory.getInstance(bridge.getBusId().intValue());
        } catch (IOException exception) {
            logger.error("I/O error {}.", exception.getMessage());
            bus = null;
        } catch (UnsupportedBusNumberException exception) {
            logger.error("Unsupported bus error {}.", exception.getMessage());
            bus = null;
        }

        if (bus != null) {
            for (int address = 0; address < 128; address++) {
                I2CDevice device = null;
                try {
                    device = bus.getDevice(address);
                    device.write((byte) 0);
                } catch (Exception exception) {
                    device = null;
                }
                if (device != null) {
                    String name = "DEVICE_" + String.valueOf(address);
                    ThingUID thingUID = new ThingUID(THING_TYPE_DEVICE, name);
                    DiscoveryResultBuilder builder = DiscoveryResultBuilder.create(thingUID);

                    Map<String, Object> properties = new HashMap<>();
                    properties.put(DEVICE_ADDRESS, Integer.valueOf(address));
                    builder = builder.withProperties(properties);
                    builder = builder.withBridge(bridge.getThing().getUID());
                    builder = builder.withLabel(name);
                    thingDiscovered(builder.build());
                }
            }

        }
    }

    @Override
    public synchronized void stopScan() {
        logger.info("Stopping scan for I2C devices");
        super.stopScan();
    }

    @Override
    protected void startBackgroundDiscovery() {
        logger.trace("Starting background scan for I2C devices");
        startScan();
    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.trace("Stopping background scan for I2C devices");
        stopScan();
    }
}
