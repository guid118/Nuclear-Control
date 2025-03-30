package shedar.mods.ic2.nuclearcontrol.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class DataSorter {

    private List<Integer> customOrder;

    /**
     * Non-default constructor, use only if you don't know the size of the list yet.
     */
    public DataSorter() {
        this.customOrder = new ArrayList<>();
    }

    /**
     * Default constructor. If you don't know the size of the list, use the constructor without parameters.
     *
     * @param size size of the list to be sorted
     */
    public DataSorter(int size) {
        resetOrder(size);
    }

    public DataSorter(int[] order) {
        if (order != null) this.customOrder = Arrays.stream(order).boxed().collect(Collectors.toList());
        else this.customOrder = new ArrayList<>();
    }

    /**
     * Save a custom order, and completely overwrite the one currently stored.
     *
     * @param newOrder the new custom order
     */
    public void saveCustomOrder(List<Integer> newOrder) {
        this.customOrder = new ArrayList<>(newOrder);
    }

    /**
     * Reset the order of the list, whilst keeping the original size.
     */
    public void resetOrder() {
        int size = customOrder.size();
        customOrder = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            customOrder.add(i);
        }
    }

    /**
     * Reset the order and set it to a specific size.
     *
     * @param size size to be set
     */
    public void resetOrder(int size) {
        customOrder = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            customOrder.add(i);
        }
    }

    /**
     * Sort a list that is of equal or greater size to the custom order. If the size of the given list is greater than
     * the stored order, it will not touch any indexes above the stored order's size
     *
     * @param data to be sorted
     * @param <T>  any type
     */
    public <T> void sortList(List<T> data) {
        if (customOrder.isEmpty()) {
            resetOrder(data.size()); // Initialize default order if none exists
        }

        if (customOrder.size() > data.size()) {
            return; // Prevent sorting if the stored order is bigger than the given data list
        }

        List<T> reordered = new ArrayList<>(data.size());

        for (int index : customOrder) {
            reordered.add(data.get(index));
        }

        for (int i = customOrder.size(); i < data.size(); i++) {
            reordered.add(data.get(i));
        }

        // Replace the original list with the sorted version
        data.clear();
        data.addAll(reordered);
    }

    /**
     * Computes the order needed to sort listB into the order of listA. Both lists must contain the same elements in a
     * different order.
     */
    public <T> void computeSortOrder(List<T> listA, List<T> listB) {
        if (listA.size() != listB.size()) {
            throw new IllegalArgumentException("Lists must be the same size and contain the same elements.");
        }

        Map<T, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < listA.size(); i++) {
            indexMap.put(listA.get(i), i);
        }

        List<Integer> sortOrder = new ArrayList<>();
        for (T item : listB) {
            sortOrder.add(indexMap.get(item));
        }

        this.customOrder = sortOrder;
    }

    public int[] getArray() {
        return this.customOrder.stream().mapToInt(Integer::intValue).toArray();
    }

    public static void setDataSorter(ItemStack stack, DataSorter dataSorter) {
        NBTTagCompound compound = stack.getTagCompound();
        compound.setIntArray("DataSorter", dataSorter.getArray());
    }

    public static DataSorter getDataSorter(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        return new DataSorter(compound.getIntArray("DataSorter"));
    }
}
