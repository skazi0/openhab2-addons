/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.i2c.discovery;

import static org.openhab.binding.i2c.I2CBindingConstants.THING_TYPE_BUS;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 * Discovery service to scan for I2C devices.
 *
 * @author Alexander Falkenstern - Initial contribution
 */
public class I2CBridgeDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(I2CDeviceDiscoveryService.class);

    private static final int DISCOVERY_TIMEOUT = 30;
    private static final Set<ThingTypeUID> DISCOVERABLE_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_BUS);

    public I2CBridgeDiscoveryService() {
        super(DISCOVERABLE_THING_TYPES_UIDS, DISCOVERY_TIMEOUT);
    }

    @Override
    protected void startScan() {
        logger.debug("Starting scan for I2C bridge.");

        for (int number = I2CBus.BUS_0; number < I2CBus.BUS_17; ++number) {
            I2CBus bus = null;
            try {
                bus = I2CFactory.getInstance(number);
            } catch (IOException exception) {
                logger.info("I/O error on I2C bus {} occurred.", number);
                bus = null;
            } catch (UnsupportedBusNumberException exception) {
                logger.info("Unsupported I2C bus {} required.", number);
                bus = null;
            }

            if (bus != null) {
                String name = "BUS_" + String.valueOf(bus.getBusNumber());
                ThingUID thingUID = new ThingUID(THING_TYPE_BUS, name);
                DiscoveryResultBuilder builder = DiscoveryResultBuilder.create(thingUID);

                Map<String, Object> properties = new HashMap<>();
                properties.put(name, Integer.valueOf(bus.getBusNumber()));
                builder = builder.withProperties(properties);
                /*
                 * DiscoveryResult discoveryResult =
                 * builder.withRepresentationProperty(AllPlayBindingConstants.DEVICE_ID);
                 */
                builder = builder.withLabel(name);
                thingDiscovered(builder.build());

                try {
                    bus.close();
                } catch (IOException exception) {
                    logger.info("Can not close I2C bus {}.", number);
                }
            } else {
                File file = new File("/dev/i2c-" + String.valueOf(number));
                file.delete();
            }

        }
    }

    @Override
    protected void stopScan() {
        logger.info("Stopping scan for I2C bridge.");
        super.stopScan();
    }

    @Override
    protected void startBackgroundDiscovery() {
        logger.trace("Starting background scan for I2C bridge.");
        startScan();
    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.trace("Stopping background scan for I2C bridge.");
        stopScan();
    }

    @Override
    public void deactivate() {
        removeOlderResults(getTimestampOfLastScan());
    }

}
