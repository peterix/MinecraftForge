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

    public static class TileEntity extends AttachCapabilitiesEvent
    {
        private final net.minecraft.tileentity.TileEntity te;
        public TileEntity(net.minecraft.tileentity.TileEntity te)
        {
            super(te);
            this.te = te;
        }
        public net.minecraft.tileentity.TileEntity getTileEntity()
        {
            return this.te;
        }
    }

    public static class Entity extends AttachCapabilitiesEvent
    {
        private final net.minecraft.entity.Entity entity;
        public Entity(net.minecraft.entity.Entity entity)
        {
            super(entity);
            this.entity = entity;
        }
        public net.minecraft.entity.Entity getEntity()
        {
            return this.entity;
        }
    }

    public static class Item extends AttachCapabilitiesEvent
    {
        private final net.minecraft.item.ItemStack stack;
        private final net.minecraft.item.Item item;
        public Item(net.minecraft.item.Item item, net.minecraft.item.ItemStack stack)
        {
            super(item);
            this.item = item;
            this.stack = stack;
        }
        public net.minecraft.item.Item getItem()
        {
            return this.item;
        }
        public net.minecraft.item.ItemStack getItemStack()
        {
            return this.stack;
        }
    }
}
