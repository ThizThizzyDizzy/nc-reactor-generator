package net.ncplanner.plannerator.multiblock.tinkers;
import java.util.ArrayList;
public enum PartMaterial{
    WOOD(35, MiningLevel.STONE, 2.4, 1.5, 1, 25, 15, 1, 1, 0, 1, 0, 3, 2.4, 1, 1, 0, 1),
    STONE(90, MiningLevel.IRON, 3, 3, 0.5, -87, 15, 0.2, 0.4, -1, 9, 4, 0.5, 0, 0, 1),
    FLINT(150, MiningLevel.IRON, 6, 2.17, 0.6, 0, 40, 5, 0.4, -1, 10, 3.6, 0.6, 0, 0.8, 3),
    COPPER(210, MiningLevel.IRON, 6.36, 2.25, 1.05, 30, 100, 1.67, 1.45, 5, 12, 6.4, 1.05, 2, 0, 8),
    CERTUS_QUARTZ(250, MiningLevel.DIAMOND, 7.68, 3.38, 0.8, 80, 70, 5, 0.4, -1, 11, 16.2, 20.25, 8, 0, 8),
    BONE(200, MiningLevel.IRON, 6.11, 1.88, 1.1, 50, 65, 1.05, 1.15, 0, 0.9, 5, 12, 3.2, 1.1, 4, 0.8, 5),
    BRONZE(430, MiningLevel.DIAMOND, 8.16, 2.62, 1.1, 70, 80, 1.82, 1.5, 6, 16, 9.6, 1.1, 6, 1, 7),
    IRON(204, MiningLevel.DIAMOND, 7.2, 3, .85, 60, 50, 2, 1.5, 7, 12, 12, 0.85, 5, 0, 4),
    OBSIDIAN(139, MiningLevel.COBALT, 8.48, 3.15, 0.9, 0, 90, 5, 0.4, -1, 10, 12.96, 0.9, 0, 2.8, 7),
    COBALT(780, MiningLevel.COBALT, 14.4, 3.08, 0.9, 100, 300, 1.33, 1.3, 3, 20, 12.48, 0.9, 8, 0, 14),
    ARDITE(990, MiningLevel.COBALT, 4.2, 2.7, 1.4, 0, 450, 2.22, 0.8, 1, 21, 10.4, 1.4, 0, 3.2, 16),
    MANYULLYN(820, MiningLevel.COBALT, 8.42, 6.54, 0.5, 250, 50, 1.54, 1.2, 4, 20, 16, 1, 13, 2.4, 4),
    SIGNALUM(690, MiningLevel.OBSIDIAN, 9, 3.9, 1.2, 0, 55, 0.83, 1.6, 4.4, 32, 18.72, 23.4, 6, 0, 6),
    CONDUCTIVE_IRON(106, MiningLevel.DIAMOND, 8.1, 0.94, 1.25, 100, 250, 0.67, 0.9, 1.25, 4, 4.5, 5.62, 28, 0, 28),
    ELECTRUM(50, MiningLevel.IRON, 14.4, 2.25, 1.1, 0, 250, 0.67, 1, 4, 4, 6.48, 1.1, 0, 0, 13),
    TAR(800, MiningLevel.STONE, 3.6, 2.25, 0.6, 50, 400, 2, 2.3, 6, 37, 10.8, 12.71, 44, 0, 44),
    DARK_STEEL(550, MiningLevel.COBALT, 8.4, 4.5, 0.9, 150, 250, 3.33, 2.5, 9, 25, 21.6, 27, 28, 0, 28),
    PROSPERITY_SHARD(36, MiningLevel.STONE, 1, 0.08, 0.23, 8, 20, 0.71, 0.63, -1.2, 1, 0.36, 0.45, 2, 0, 2),
    LEAF(1.5, 0.5),
    FEATHER(1, 1d),
    REEDS(1.5, 20),
    STRING(1);
    public int headDurability;
    public MiningLevel level = MiningLevel.NONE;
    public double miningSpeed;
    public double attack;
    public double handleMod;
    public int handleDurability;
    public int extraDurability;
    public double bowDrawspeed;
    public double bowRangeMult;
    public double bonusDamage;
    public double arrowShaftMod;
    public int bonusAmmo;
    public int coreDurability;
    public double coreDef;
    public double plateMod;
    public int plateDurability;
    public double plateToughness;
    public int trimDurability;
    public ArrayList<PartType> incompatibilities = new ArrayList<>();
    public double fletchingModifier;
    public double fletchingAccuracy;
    public double bowstringMod;
    private PartMaterial(int headDurability, MiningLevel level, double miningspeed, double attack, double handleMod, int handleDurability, int extraDurability, double bowDrawspeed, double bowRangeMult, double bonusDamage, double arrowShaftMod, int bonusAmmo, int coreDurability, double coreDef, double plateMod, int plateDurability, double plateToughness, int trimDurability){
        this.headDurability = headDurability;
        this.level = level;
        this.miningSpeed = miningspeed;
        this.attack = attack;
        this.handleMod = handleMod;
        this.handleDurability = handleDurability;
        this.extraDurability = extraDurability;
        this.bowDrawspeed = bowDrawspeed;
        this.bowRangeMult = bowRangeMult;
        this.bonusDamage = bonusDamage;
        this.arrowShaftMod = arrowShaftMod;
        this.bonusAmmo = bonusAmmo;
        this.coreDurability = coreDurability;
        this.coreDef = coreDef;
        this.plateMod = plateMod;
        this.plateDurability = plateDurability;
        this.plateToughness = plateToughness;
        this.trimDurability = trimDurability;
        incompatibilities.add(PartType.BOWSTRING);
        incompatibilities.add(PartType.FLETCHING);
    }
    private PartMaterial(int headDurability, MiningLevel level, double miningspeed, double attack, double handleMod, int handleDurability, int extraDurability, double bowDrawspeed, double bowRangeMult, double bonusDamage, int coreDurability, double coreDef, double plateMod, int plateDurability, double plateToughness, int trimDurability){
        this.headDurability = headDurability;
        this.level = level;
        this.miningSpeed = miningspeed;
        this.attack = attack;
        this.handleMod = handleMod;
        this.handleDurability = handleDurability;
        this.extraDurability = extraDurability;
        this.bowDrawspeed = bowDrawspeed;
        this.bowRangeMult = bowRangeMult;
        this.bonusDamage = bonusDamage;
        this.coreDurability = coreDurability;
        this.coreDef = coreDef;
        this.plateMod = plateMod;
        this.plateDurability = plateDurability;
        this.plateToughness = plateToughness;
        this.trimDurability = trimDurability;
        incompatibilities.add(PartType.ARROW_SHAFT);
        incompatibilities.add(PartType.BOWSTRING);
        incompatibilities.add(PartType.FLETCHING);
    }
    private PartMaterial(double fletchingModifier, double fletchingAccuracy){
        this.fletchingModifier = fletchingModifier;
        this.fletchingAccuracy = fletchingAccuracy;
        for(PartType t : PartType.values())incompatibilities.add(t);
        incompatibilities.remove(PartType.FLETCHING);
    }
    private PartMaterial(double arrowShaftMod, int bonusAmmo){
        this.arrowShaftMod = arrowShaftMod;
        this.bonusAmmo = bonusAmmo;
        for(PartType t : PartType.values())incompatibilities.add(t);
        incompatibilities.remove(PartType.ARROW_SHAFT);
    }
    private PartMaterial(double bowstringMod){
        this.bowstringMod = bowstringMod;
        for(PartType t : PartType.values())incompatibilities.add(t);
        incompatibilities.remove(PartType.BOWSTRING);
    }

    public String getTooltips(PartType type) {
        String tooltips = "";
        for(PartCategory tag : type.tags){
            switch(tag){
                case HEAD:
                    tooltips+="\nHead"
                            + "\nDurability: "+headDurability
                            + "\nMining Level: "+level
                            + "\nMining Speed: "+miningSpeed
                            + "\nAttack: "+attack;
                    break;
                case HANDLE:
                    tooltips+="\nHandle"
                            + "\nModifier: "+handleMod
                            + "\nDurability: "+handleDurability;
                    break;
                case EXTRA:
                    tooltips+="\nExtra"
                            + "\nDurability: "+extraDurability;
                    break;
                case BOW:
                    tooltips+="\nBow"
                            + "\nDrawspeed: "+bowDrawspeed
                            + "\nRange Multiplier: "+bowRangeMult
                            + "\nBonus Damage: "+bonusDamage;
                    break;
                case ARROW_SHAFT:
                    tooltips+="\nArrow Shaft"
                            + "\nModifier: "+arrowShaftMod
                            + "\nBonus-Ammo: "+bonusAmmo;
                    break;
                case CORE:
                    tooltips+="\nCore"
                            + "\nDurability: "+coreDurability
                            + "\nDefense: "+coreDef;
                    break;
                case PLATES:
                    tooltips+="\nPlates"
                            + "\nModifier: "+plateMod
                            + "\nDurability: "+plateDurability
                            + "\nToughness: "+plateToughness;
                    break;
                case TRIM:
                    tooltips+="\nTrim"
                            + "\nDurability: "+trimDurability;
                    break;
            }
        }
        return tooltips;
    }
}