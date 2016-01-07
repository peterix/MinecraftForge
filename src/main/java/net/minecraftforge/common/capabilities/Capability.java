package net.minecraftforge.common.capabilities;

import com.google.common.base.Throwables;

import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class Capability<T>
{
    public static interface IStorage<T>
    {
        NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side, TileEntity te);
        void readNBT(Capability<T> capability, T instance, EnumFacing side, TileEntity te, NBTBase nbt);
    }

    private final String name;
    private final IStorage<T> storage;
    private final Class<? extends T> implementation;

    Capability(String name, IStorage<T> storage, Class<? extends T> implementation)
    {
        this.name = name;
        this.storage = storage;
        this.implementation = implementation;
    }

    public String getName() { return name; }
    public IStorage<T> getStorage() { return storage; }

    public T getDefaultInstance() //Do we need extra args? Side? World?
    {
        try {
            return (T)this.implementation.newInstance();
        } catch (InstantiationException e) {
            Throwables.propagate(e);
        } catch (IllegalAccessException e) {
            Throwables.propagate(e);
        }
        return null; // We'll never get here, but compile!
    }
}
