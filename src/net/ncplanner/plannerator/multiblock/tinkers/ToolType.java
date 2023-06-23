package net.ncplanner.plannerator.multiblock.tinkers;
public enum ToolType{
    KATANA(0, PartCategory.HANDLE, PartType.TOUGH_TOOL_ROD, PartCategory.HEAD, PartType.LARGE_SWORD_BLADE, PartCategory.HEAD, PartType.LARGE_SWORD_BLADE, PartCategory.EXTRA, PartType.TOUGH_BINDING),
    PICKAXE(1, PartCategory.HANDLE, PartType.TOOL_ROD, PartCategory.HEAD, PartType.PICKAXE_HEAD, PartCategory.EXTRA, PartType.BINDING),
    SHOVEL(0.7, PartCategory.HANDLE, PartType.TOOL_ROD, PartCategory.HEAD, PartType.SHOVEL_HEAD, PartCategory.EXTRA, PartType.BINDING),
    HATCHET(1.85, PartCategory.HANDLE, PartType.TOOL_ROD, PartCategory.HEAD, PartType.AXE_HEAD, PartCategory.EXTRA, PartType.BINDING),
    MATTOCK(0.4, PartCategory.HANDLE, PartType.TOOL_ROD, PartCategory.HEAD, PartType.AXE_HEAD, PartCategory.HEAD, PartType.SHOVEL_HEAD),
    KAMA(0, PartCategory.HANDLE, PartType.TOOL_ROD, PartCategory.HEAD, PartType.KAMA_HEAD, PartCategory.EXTRA, PartType.BINDING),
    HAMMER(0, PartCategory.HANDLE, PartType.TOUGH_TOOL_ROD, PartCategory.HEAD, PartType.HAMMER_HEAD, PartCategory.HEAD, PartType.LARGE_PLATE, PartCategory.HEAD, PartType.LARGE_PLATE),
    EXCAVATOR(0, PartCategory.HANDLE, PartType.TOUGH_TOOL_ROD, PartCategory.HEAD, PartType.EXCAVATOR_HEAD, PartCategory.HEAD, PartType.LARGE_PLATE, PartCategory.EXTRA, PartType.TOUGH_BINDING),
    LUMBERAXE(0, PartCategory.HANDLE, PartType.TOUGH_TOOL_ROD, PartCategory.HEAD, PartType.BROAD_AXE_HEAD, PartCategory.HEAD, PartType.LARGE_PLATE, PartCategory.EXTRA, PartType.TOUGH_BINDING),
    SCYTHE(0, PartCategory.HANDLE, PartType.TOUGH_TOOL_ROD, PartCategory.HEAD, PartType.SCYTHE_HEAD, PartCategory.EXTRA, PartType.TOUGH_BINDING, PartCategory.HANDLE, PartType.TOUGH_TOOL_ROD),
    BROADSWORD(2, PartCategory.HANDLE, PartType.TOOL_ROD, PartCategory.HEAD, PartType.SWORD_BLADE, PartCategory.EXTRA, PartType.WIDE_GUARD),
    LONGSWORD(0, PartCategory.HANDLE, PartType.TOOL_ROD, PartCategory.HEAD, PartType.SWORD_BLADE, PartCategory.EXTRA, PartType.HAND_GUARD),
    RAPIER(0, PartCategory.HANDLE, PartType.TOOL_ROD, PartCategory.HEAD, PartType.SWORD_BLADE, PartCategory.EXTRA, PartType.CROSS_GUARD),
    FRYPAN(0, PartCategory.HANDLE, PartType.TOOL_ROD, PartCategory.HEAD, PartType.PAN),
    BATTLESIGN(0, PartCategory.HANDLE, PartType.TOOL_ROD, PartCategory.HEAD, PartType.SIGN_PLATE),
    CLEAVER(0, PartCategory.HANDLE, PartType.TOUGH_TOOL_ROD, PartCategory.HEAD, PartType.LARGE_SWORD_BLADE, PartCategory.HEAD, PartType.LARGE_PLATE, PartCategory.EXTRA, PartType.TOUGH_TOOL_ROD),
    SHORTBOW(0, PartCategory.BOW, PartType.BOWLIMB, PartCategory.BOW, PartType.BOWLIMB, PartCategory.BOWSTRING, PartType.BOWSTRING),
    LONGBOW(0, PartCategory.BOW, PartType.BOWLIMB, PartCategory.BOW, PartType.BOWLIMB, PartCategory.EXTRA, PartType.LARGE_PLATE, PartCategory.BOWSTRING, PartType.BOWSTRING),
    ARROW(0, PartCategory.ARROW_SHAFT, PartType.ARROW_SHAFT, PartCategory.HEAD, PartType.ARROW_HEAD, PartCategory.FLETCHING, PartType.FLETCHING),
    CROSSBOW(0, null, PartType.TOUGH_TOOL_ROD, null, PartType.BOWLIMB, null, PartType.TOUGH_BINDING, null, PartType.BOWSTRING),
    BOLT(0, null, PartType.BOLT_CORE, null, PartType.BOLT_CORE, null, PartType.FLETCHING),
    SHURIKEN(0, null, PartType.KNIFE_BLADE, null, PartType.KNIFE_BLADE, null, PartType.KNIFE_BLADE, null, PartType.KNIFE_BLADE),
    HELMET(0, PartCategory.CORE, PartType.HELMET_CORE, PartCategory.PLATES, PartType.ARMOR_PLATES, PartCategory.TRIM, PartType.ARMOR_TRIM),
    CHESTPLATE(0, PartCategory.CORE, PartType.CHESTPLATE_CORE, PartCategory.PLATES, PartType.ARMOR_PLATES, PartCategory.TRIM, PartType.ARMOR_TRIM),
    LEGGINGS(0, PartCategory.CORE, PartType.LEGGINGS_CORE, PartCategory.PLATES, PartType.ARMOR_PLATES, PartCategory.TRIM, PartType.ARMOR_TRIM),
    BOOTS(0, PartCategory.CORE, PartType.BOOTS_CORE, PartCategory.PLATES, PartType.ARMOR_PLATES, PartCategory.TRIM, PartType.ARMOR_TRIM);
    public final PartType[] parts;
    public final PartCategory[] cats;
    public final double baseAttack;
    private ToolType(double baseAttack, Object... stuff){
        this.baseAttack = baseAttack;
        parts = new PartType[stuff.length/2];
        cats = new PartCategory[stuff.length/2];
        for(int i = 0; i<stuff.length-1; i+=2){
            cats[i/2] = (PartCategory) stuff[i];
            parts[i/2] = (PartType) stuff[i+1];
        }
    }
}