package net.minecraftforge.test.capabilitytest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="forge.testcapmod",version="1.0")
public class TestCapabilityMod
{
    @CapabilityInject(IExampleCapability.class)
    private static final Capability<IExampleCapability> TEST_CAP = null;
    @CapabilityInject(IInventory.class)
    private static final Capability<IInventory> INV_CAP = null;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt)
    {
        CapabilityManager.INSTANCE.register(IExampleCapability.class, new Storage(),    DefaultImpl.class);
        CapabilityManager.INSTANCE.register(IInventory.class,         new InvStorage(), DefaultInv.class );
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event)
    {
        if (event.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return;
        if (event.entityPlayer.getHeldItem() == null) return;
        if (event.entityPlayer.getHeldItem().getItem() != Items.stick) return;

        TileEntity te = event.world.getTileEntity(event.pos);
        if (te != null && te.hasCapability(INV_CAP, event.face))
        {
            event.setCanceled(true);
            IInventory inv = te.getCapability(INV_CAP, event.face);
            for (int x = 0; x < inv.getSizeInventory(); x++)
            {
                ItemStack slot = inv.getStackInSlot(x);
                if (slot != null)
                {
                    event.entityPlayer.addChatMessage(new ChatComponentText(slot.toString()));
                }
            }
        }
    }


    @CapabilityInject(IExampleCapability.class)
    private static void capRegistered(Capability<IExampleCapability> cap)
    {
        System.out.println("IExampleCapability was registered wheeeeee!");
    }

    public static class Storage implements IStorage<IExampleCapability>
    {
        @Override
        public NBTBase writeNBT(Capability<IExampleCapability> capability, IExampleCapability instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<IExampleCapability> capability, IExampleCapability instance, EnumFacing side, NBTBase nbt) {
        }
    }

    public static class DefaultImpl implements IExampleCapability {
        @Override
        public void doSomething(String arg) {
            System.out.println(arg);
        }
    }

    public static class InvStorage implements IStorage<IInventory>
    {

        @Override
        public NBTBase writeNBT(Capability<IInventory> capability, IInventory instance, EnumFacing side)
        {
            NBTTagList nbt = new NBTTagList();

            for (int index = 0; index < instance.getSizeInventory(); ++index)
            {
                if (instance.getStackInSlot(index) != null)
                {
                    NBTTagCompound comp = new NBTTagCompound();
                    comp.setByte("Slot", (byte)index);
                    instance.getStackInSlot(index).writeToNBT(comp);
                    nbt.appendTag(comp);
                }
            }

            return nbt;
        }

        @Override
        public void readNBT(Capability<IInventory> capability, IInventory instance, EnumFacing side, NBTBase nbt)
        {
            NBTTagList list = (NBTTagList)nbt; //.getTagList("Items", 10);
            for (int i = 0; i < list.tagCount(); ++i)
            {
                NBTTagCompound comp = list.getCompoundTagAt(i);
                int slot = comp.getByte("Slot");
                if (slot >= 0 && slot < instance.getSizeInventory())
                {
                    instance.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(comp));
                }
            }
        }
    }

    public static class DefaultInv implements IInventory
    {
        @Override public String getName() { return null; }
        @Override public boolean hasCustomName() { return false; }
        @Override public IChatComponent getDisplayName() { return null; }
        @Override public int getSizeInventory() { return 0; }
        @Override public ItemStack getStackInSlot(int index) { return null; }
        @Override public ItemStack decrStackSize(int index, int count) { return null; }
        @Override public ItemStack removeStackFromSlot(int index) { return null; }
        @Override public void setInventorySlotContents(int index, ItemStack stack) {}
        @Override public int getInventoryStackLimit() { return 0; }
        @Override public void markDirty() { }
        @Override public boolean isUseableByPlayer(EntityPlayer player) { return false; }
        @Override public void openInventory(EntityPlayer player) { }
        @Override public void closeInventory(EntityPlayer player) { }
        @Override public boolean isItemValidForSlot(int index, ItemStack stack) { return false; }
        @Override public int getField(int id) { return 0; }
        @Override public void setField(int id, int value) { }
        @Override public int getFieldCount() { return 0; }
        @Override public void clear() { }
    }
}