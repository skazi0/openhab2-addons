/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.i2c.internal;

import static org.openhab.binding.i2c.I2CBindingConstants.*;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.i2c.handler.I2CBridgeHandler;
import org.osgi.service.component.ComponentContext;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

/**
 * The {@link I2CHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Alexander Falkenstern - Initial contribution
 */
public class I2CHandlerFactory extends BaseThingHandlerFactory {

    private final static GpioController controller = GpioFactory.getInstance();

    public I2CHandlerFactory() {
        super();
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return BINDING_ID.equals(thingTypeUID.getBindingId());
    }

    @Override
    protected void deactivate(ComponentContext componentContext) {
        super.deactivate(componentContext);
        controller.removeAllTriggers();
        controller.unexportAll();
        controller.shutdown();
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        if (THING_TYPE_BUS.equals(thing.getThingTypeUID()) && (thing instanceof Bridge)) {
            return new I2CBridgeHandler((Bridge) thing);
        }

        return null;
    }

    @Override
    protected void removeHandler(ThingHandler thingHandler) {
        super.removeHandler(thingHandler);
    }
}
