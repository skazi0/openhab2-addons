/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.plclogo.handler;

import static org.openhab.binding.plclogo.PLCLogoBindingConstants.LOGO_MEMORY_BLOCK;

import java.util.Map;
import java.util.Objects;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.plclogo.PLCLogoBindingConstants;
import org.openhab.binding.plclogo.internal.PLCLogoDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link PLCBlockHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Alexander Falkenstern - Initial contribution
 */
public abstract class PLCBlockHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(PLCBlockHandler.class);

    private int inputAddress = -1;
    private int inputBit = -1;

    private int outputAddress = -1;
    private int outputBit = -1;

    /**
     * Constructor.
     */
    public PLCBlockHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        getBridge().getHandler().handleCommand(channelUID, command);
    }

    @Override
    public void initialize() {
        logger.debug("Initialize LOGO! common block handler.");

        final Bridge bridge = getBridge();
        Objects.requireNonNull(bridge, "PLCBlockHandler: Bridge may not be null.");

        String message = "";
        boolean success = false;
        if (hasInputBlock() || hasOutputBlock()) {
            final Map<?, Integer> block = LOGO_MEMORY_BLOCK.get(getLogoFamily());
            if ((0 <= getInputAddress()) && (getInputAddress() <= block.get("SIZE")) && (0 <= getOutputAddress())
                    && (getOutputAddress() <= block.get("SIZE"))) {
                success = true;
                super.initialize();
            } else {
                message = "Can not initialize LOGO! block " + getOutputBlockName() + ".";
            }
        } else {
            message = "Can not initialize LOGO! block. Please check blocks.";
        }

        if (!success) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, message);
        }
    }

    @Override
    public void dispose() {
        logger.debug("Dispose LOGO! common block handler.");
        super.dispose();
        inputAddress = -1;
        inputBit = -1;
        outputAddress = -1;
        outputBit = -1;
    }

    /**
     * Returns if input block is configured.
     *
     * @return True if input block is configure
     */
    public boolean hasInputBlock() {
        return getInputBlockName() != null;
    }

    /**
     * Returns if output block is configured.
     *
     * @return True if output block is configure
     */
    public boolean hasOutputBlock() {
        return getOutputBlockName() != null;
    }

    /**
     * Calculate input memory address for configured block.
     *
     * @return Calculated address
     */
    public int getInputAddress() {
        if (inputAddress == -1) {
            inputAddress = getAddressOfInputBlock();
        }
        return inputAddress;
    }

    /**
     * Calculate input bit within memory address for configured block.
     *
     * @return Calculated bit
     */
    public int getInputBit() {
        if (inputBit == -1) {
            inputBit = getBitOfInputBlock();
        }
        return inputBit;
    }

    /**
     * Calculate output memory address for configured block.
     *
     * @return Calculated address
     */
    public int getOutputAddress() {
        if (outputAddress == -1) {
            outputAddress = getAddressOfOutputBlock();
        }
        return outputAddress;
    }

    /**
     * Calculate input bit within memory address for configured block.
     *
     * @return Calculated bit
     */
    public int getOutputBit() {
        if (outputBit == -1) {
            outputBit = getBitOfOutputBlock();
        }
        return outputBit;
    }

    /**
     * Returns configured input block name.
     *
     * @return Name of configured LOGO! block
     */
    public abstract String getInputBlockName();

    /**
     * Returns configured input block name.
     *
     * @return Name of configured LOGO! block
     */
    public abstract String getOutputBlockName();

    /**
     * Update value channel of current thing with new data.
     *
     * @param data Data value to update with
     */
    public abstract void setData(final byte[] data);

    /**
     * Returns data type accepted by LOGO! block.
     * Can be BIT for digital blocks and WORD/DWORD for analog
     *
     * @return Data type accepted by configured block
     */
    public abstract PLCLogoDataType getBlockDataType();

    /**
     * Returns configured LOGO! family.
     *
     * @see PLCLogoBindingConstants#LOGO_0BA7
     * @see PLCLogoBindingConstants#LOGO_0BA8
     * @return Configured LOGO! family
     */
    public String getLogoFamily() {
        final Bridge bridge = getBridge();
        Objects.requireNonNull(bridge, "PLCBlockHandler: Bridge may not be null.");

        final PLCBridgeHandler handler = (PLCBridgeHandler) bridge.getHandler();
        Objects.requireNonNull(handler, "PLCBlockHandler: Handler may not be null.");

        return handler.getLogoFamily();
    }

    @Override
    protected void updateConfiguration(Configuration configuration) {
        super.updateConfiguration(configuration);
        inputAddress = -1;
        inputBit = -1;
        outputAddress = -1;
        outputBit = -1;
    }

    /**
     * Calculate address for the input block.
     *
     * @return Calculated address offset
     */
    protected abstract int getAddressOfInputBlock();

    /**
     * Calculate bit within address for input block.
     *
     * @return Calculated bit
     */
    protected abstract int getBitOfInputBlock();

    /**
     * Calculate address for the output block.
     *
     * @return Calculated address offset
     */
    protected abstract int getAddressOfOutputBlock();

    /**
     * Calculate bit within address for output block.
     *
     * @return Calculated bit
     */
    protected abstract int getBitOfOutputBlock();

}
