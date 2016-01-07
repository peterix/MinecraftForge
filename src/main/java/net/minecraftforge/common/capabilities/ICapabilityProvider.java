package net.minecraftforge.common.capabilities;

import net.minecraft.util.EnumFacing;

public interface ICapabilityProvider
{
    boolean hasCapability(Capability<?> capability, EnumFacing facing);
    <T> T getCapability(Capability<T> capability, EnumFacing facing);
}
