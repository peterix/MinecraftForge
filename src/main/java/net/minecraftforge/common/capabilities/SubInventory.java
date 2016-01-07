package net.minecraftforge.common.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

public class SubInventory implements IInventory
{
    private int start, length;
    private IInventory parent;
    public SubInventory(IInventory parent, int start, int length)
    {
        this.parent = parent;
        this.start = start;
        this.length = length;
    }
    @Override public String getName() { return this.parent.getName(); }
    @Override public boolean hasCustomName() { return this.parent.hasCustomName(); }
    @Override public IChatComponent getDisplayName() { return this.parent.getDisplayName(); }
    @Override public int getSizeInventory() { return length; }
    @Override public ItemStack getStackInSlot(int index) { return this.parent.getStackInSlot(start + index); }
    @Override public ItemStack decrStackSize(int index, int count) { return this.parent.decrStackSize(start + index, count); }
    @Override public ItemStack removeStackFromSlot(int index) { return this.parent.removeStackFromSlot(start + index); }
    @Override public void setInventorySlotContents(int index, ItemStack stack) { this.parent.setInventorySlotContents(start + index, stack); }
    @Override public int getInventoryStackLimit() { return this.parent.getInventoryStackLimit(); }
    @Override public void markDirty() { this.parent.markDirty(); }
    @Override public boolean isUseableByPlayer(EntityPlayer player) { return this.parent.isUseableByPlayer(player); }
    @Override public void openInventory(EntityPlayer player) { this.parent.openInventory(player); }
    @Override public void closeInventory(EntityPlayer player) { this.parent.closeInventory(player); }
    @Override public boolean isItemValidForSlot(int index, ItemStack stack) { return this.parent.isItemValidForSlot(start + index, stack); }
    @Override public int getField(int id) { return this.parent.getField(id); }
    @Override public void setField(int id, int value) { this.parent.setField(id, value); }
    @Override public int getFieldCount() { return this.parent.getFieldCount(); }
    @Override
    public void clear()
    {
        for (int x = start; x < start + length; x++)
        {
            this.parent.setInventorySlotContents(x, null);
        }
    }
}