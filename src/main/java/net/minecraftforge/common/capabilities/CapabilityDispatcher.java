package net.minecraftforge.common.capabilities;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public final class CapabilityDispatcher implements INBTSerializable<NBTTagCompound>, ICapabilityProvider
{

    private ICapabilityProvider[] caps;
    private INBTSerializable<NBTBase>[] writers;
    private String[] names;

    @SuppressWarnings("unchecked")
    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list)
    {
        List<ICapabilityProvider> lstCaps = Lists.newArrayList();
        List<INBTSerializable<NBTBase>> lstWriters = Lists.newArrayList();
        List<String> lstNames = Lists.newArrayList();

        for (Map.Entry<ResourceLocation, ICapabilityProvider> entry : list.entrySet())
        {
            ICapabilityProvider prov = entry.getValue();
            lstCaps.add(prov);
            if (prov instanceof INBTSerializable)
            {
                lstWriters.add((INBTSerializable<NBTBase>)prov);
                lstNames.add(entry.getKey().toString());
            }
        }

        caps = lstCaps.toArray(new ICapabilityProvider[lstCaps.size()]);
        writers = lstWriters.toArray(new INBTSerializable[lstWriters.size()]);
        names = lstNames.toArray(new String[lstNames.size()]);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        for (ICapabilityProvider cap : caps)
        {
            if (cap.hasCapability(capability, facing))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        for (ICapabilityProvider cap : caps)
        {
            T ret = cap.getCapability(capability, facing);
            if (ret != null)
            {
                return ret;
            }
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        for (int x = 0; x < writers.length; x++)
        {
            nbt.setTag(names[x], writers[x].serializeNBT());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        for (int x = 0; x < writers.length; x++)
        {
            if (nbt.hasKey(names[x]))
            {
                writers[x].deserializeNBT(nbt.getTag(names[x]));
            }
        }
    }
}
