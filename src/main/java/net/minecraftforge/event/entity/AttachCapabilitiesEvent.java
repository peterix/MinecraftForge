package net.minecraftforge.event.entity;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.Event;

public class AttachCapabilitiesEvent extends Event
{
    private final Object obj;
    private final Map<ResourceLocation, ICapabilityProvider> caps = Maps.newHashMap();
    private final Map<ResourceLocation, ICapabilityProvider> view = Collections.unmodifiableMap(caps);
    public AttachCapabilitiesEvent(Object obj)
    {
        this.obj = obj;
    }

    public Object getObject()
    {
        return this.obj;
    }

    public void addCapability(ResourceLocation key, ICapabilityProvider cap)
    {
        if (caps.containsKey(key))
            throw new IllegalStateException("Duplicate Capability Key: " + key  + " " + cap);
        this.caps.put(key, cap);
    }

    public Map<ResourceLocation, ICapabilityProvider> getCapabilities()
    {
        return view;
    }
}
