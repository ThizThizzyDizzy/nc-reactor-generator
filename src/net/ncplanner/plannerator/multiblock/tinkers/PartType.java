package net.ncplanner.plannerator.multiblock.tinkers;
import java.util.Arrays;
import java.util.List;
public enum PartType{
    PICKAXE_HEAD(PartCategory.HEAD),
    SHOVEL_HEAD(PartCategory.HEAD),
    AXE_HEAD(PartCategory.HEAD),
    BROAD_AXE_HEAD(PartCategory.HEAD),
    SWORD_BLADE(PartCategory.HEAD),
    LARGE_SWORD_BLADE(PartCategory.HEAD),
    EXCAVATOR_HEAD(PartCategory.HEAD),
    KAMA_HEAD(PartCategory.HEAD),
    SCYTHE_HEAD(PartCategory.HEAD),
    PAN(PartCategory.HEAD),
    SIGN_PLATE(PartCategory.HEAD),
    TOOL_ROD(PartCategory.HANDLE),
    TOUGH_TOOL_ROD(PartCategory.HANDLE),
    BINDING(PartCategory.EXTRA),
    TOUGH_BINDING(PartCategory.EXTRA),
    WIDE_GUARD(PartCategory.EXTRA),
    HAND_GUARD(PartCategory.EXTRA),
    CROSS_GUARD(PartCategory.EXTRA),
    LARGE_PLATE(PartCategory.HEAD, PartCategory.EXTRA),
    KNIFE_BLADE(PartCategory.HEAD),
    BOWLIMB(PartCategory.BOW),
    BOWSTRING(PartCategory.BOWSTRING),
    ARROW_HEAD(PartCategory.HEAD),
    ARROW_SHAFT(PartCategory.ARROW_SHAFT),
    FLETCHING(PartCategory.FLETCHING),
    HELMET_CORE(PartCategory.CORE),
    CHESTPLATE_CORE(PartCategory.CORE),
    LEGGINGS_CORE(PartCategory.CORE),
    BOOTS_CORE(PartCategory.CORE),
    ARMOR_PLATES(PartCategory.PLATES),
    ARMOR_TRIM(PartCategory.TRIM),
    HAMMER_HEAD(PartCategory.HEAD),
    BOLT_CORE;
    public final List<PartCategory> tags;
    private PartType(PartCategory... tags){
        this.tags = Arrays.asList(tags);
    }
}