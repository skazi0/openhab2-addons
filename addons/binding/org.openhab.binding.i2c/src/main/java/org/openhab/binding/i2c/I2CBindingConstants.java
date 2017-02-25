/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.i2c;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link I2CBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Alexander Falkenstern - Initial contribution
 */
public class I2CBindingConstants {

    public static final String BINDING_ID = "i2c";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_BUS = new ThingTypeUID(BINDING_ID, "bus");

    public final static ThingTypeUID THING_TYPE_MCP23017 = new ThingTypeUID(BINDING_ID, "MCP23017");
    public final static ThingTypeUID THING_TYPE_MCP3424 = new ThingTypeUID(BINDING_ID, "MCP3424");

    // Bridge config properties
    public static final String BUS_ID = "id";

    // Device config properties
    public static final String DEVICE_TYPE = "type";
    public static final String DEVICE_ADDRESS = "address";
    public static final String REFRESH_INTERVAL = "refresh";

    // List of all Channel ids
    public final static String DI00 = "DI00";

    public final static String DO00 = "DO00";
}
