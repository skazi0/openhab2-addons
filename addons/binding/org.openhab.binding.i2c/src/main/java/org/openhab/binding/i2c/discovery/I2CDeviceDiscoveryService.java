/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.i2c.discovery;

import static org.openhab.binding.i2c.I2CBindingConstants.THING_TYPE_MCP23017;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
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
public class I2CDeviceDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(I2CDeviceDiscoveryService.class);

    private static final int DISCOVERY_TIMEOUT = 30;
    private static final Set<ThingTypeUID> DISCOVERABLE_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_MCP23017);

    private I2CBus bus = null;

    public I2CDeviceDiscoveryService(Integer busId) {
        super(DISCOVERABLE_THING_TYPES_UIDS, DISCOVERY_TIMEOUT);
        try {
            this.bus = I2CFactory.getInstance(busId.intValue());
        } catch (IOException exception) {
            logger.error("I/O error {}.", exception.getMessage());
            bus = null;
        } catch (UnsupportedBusNumberException exception) {
            logger.error("Unsupported bus error {}.", exception.getMessage());
            bus = null;
        }
    }

    @Override
    protected void startScan() {
        logger.debug("Starting scan for I2C devices.");
        /*
         * List<Integer> validAddresses = new ArrayList<Integer>();
         * for (int i = 1; i < 128; i++) {
         * try {
         * I2CDevice device = bus.getDevice(i);
         * device.write((byte)0);
         * validAddresses.add(i);
         * } catch (Exception ignore) { }
         * }
         *
         * System.out.println("Found: ---");
         * for (int a : validAddresses) {
         * System.out.println("Address: " + Integer.toHexString(a));
         * }
         *
         * ThingUID thingUID = new ThingUID(AllPlayBindingConstants.SPEAKER_THING_TYPE, speaker.getId());
         * Map<String, Object> properties = new HashMap<>();
         * properties.put(AllPlayBindingConstants.DEVICE_ID, speaker.getId());
         * properties.put(AllPlayBindingConstants.DEVICE_NAME, speaker.getName());
         *
         * DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
         * .withRepresentationProperty(AllPlayBindingConstants.DEVICE_ID).withLabel(speaker.getName()).build();
         * thingDiscovered(discoveryResult);
         */
    }

    @Override
    protected void stopScan() {
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

    @Override
    public void deactivate() {
        removeOlderResults(getTimestampOfLastScan());
    }

}
