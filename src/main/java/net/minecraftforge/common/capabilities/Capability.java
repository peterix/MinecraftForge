package net.minecraftforge.common.capabilities;

import java.util.concurrent.Callable;

import com.google.common.base.Throwables;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;

public class Capability<T>
{
    public static interface IStorage<T>
    {
        NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side);
        void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt);
    }

    private final String name;
    private final IStorage<T> storage;
    private final Callable<? extends T> factory;

    Capability(String name, IStorage<T> storage, Callable<? extends T> factory)
    {
        this.name = name;
        this.storage = storage;
        this.factory = factory;
    }

    public String getName() { return name; }
    public IStorage<T> getStorage() { return storage; }

    public T getDefaultInstance() //Do we need extra args? Side? World?
    {
        try
        {
            return this.factory.call();
        }
        catch (Exception e)
        {
            Throwables.propagate(e);
        }
        return null;
    }
}
