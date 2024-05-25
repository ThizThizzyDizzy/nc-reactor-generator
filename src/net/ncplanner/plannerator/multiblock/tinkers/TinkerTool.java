package net.ncplanner.plannerator.multiblock.tinkers;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.BlockGrid;
import net.ncplanner.plannerator.multiblock.SimpleMultiblock;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.SetblockAction;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestion;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.MenuEdit;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentEditorGrid;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickEnum;
import net.ncplanner.plannerator.planner.ncpf.design.MultiblockDesign;
public class TinkerTool extends SimpleMultiblock<ToolPart>{
    private ToolType type;
    boolean valid;
    int durability;
    MiningLevel miningLevel;
    double miningSpeed;
    double attack;
    int defense;
    int toughness;
    double accuracy;
    private double range;
    private double drawspeed;
    public TinkerTool(NCPFConfigurationContainer configuration, int type){
        super(configuration, type);
        this.type = ToolType.values()[type];
    }
    @Override
    protected void createBlockGrids(){
        blockGrids.add(new BlockGrid(0, 0, 0, ToolType.values()[dimensions[0]].parts.length, 0, 0));
    }
    @Override
    public String getDefinitionName(){
        return "Tinker Tool";
    }
    @Override
    public TinkerTool newInstance(NCPFConfigurationContainer configuration){
        return new TinkerTool(configuration, 1);
    }
    @Override
    public TinkerTool newInstance(NCPFConfigurationContainer configuration, int... dimensions){
        return new TinkerTool(configuration, dimensions[0]);
    }
    @Override
    public void getAvailableBlocks(List<ToolPart> blocks){
        for(PartType typ : PartType.values()){
            boolean found = false;
            for(PartType typ2 : type.parts){
                if(typ==typ2){
                    found = true;
                    break;
                }
            }
            if(!found)continue;
            for(PartMaterial mat : PartMaterial.values()){
                if(mat.incompatibilities.contains(typ))continue;
                blocks.add(new ToolPart(configuration, 0, 0, 0, typ, mat));
            }
        }
    }
    @Override
    public void genCalcSubtasks(){
    }
    @Override
    public void clearData(List<ToolPart> blocks){
        super.clearData(blocks);
        valid = false;
        miningSpeed = attack = durability = defense = toughness = 0;
        miningLevel = MiningLevel.NONE;
    }
    @Override
    public TinkerTool blankCopy() {
        return newInstance(configuration, dimensions);
    }
    @Override
    public TinkerTool doCopy(){
        TinkerTool copy = blankCopy();
        forEachPosition((x, y, z) -> {
            copy.setBlock(x, y, z, getBlock(x, y, z));
        });
        copy.valid = valid;
        copy.durability = durability;
        copy.miningLevel = miningLevel;
        copy.miningSpeed = miningSpeed;
        copy.attack = attack;
        copy.defense = defense;
        copy.toughness = toughness;
        return copy;
    }
    @Override
    public boolean doCalculationStep(List<ToolPart> blocks, boolean addDecals){
        for(int i = 0; i<type.parts.length; i++){
            if(getBlock(i, 0, 0)==null){
                valid = false;
                return false;
            }
        }
        valid = true;

        double handleMod = 0;
        double fletchingMod = 1;
        double fletchingAcc = 1;
        int handleDurability = 0;
        int handles = 0;
        int headDurability = 0;
        int coreDefense = 0;
        double headSpeed = 0;
        MiningLevel headLevel = MiningLevel.NONE;
        double headAttack = 0;
        int extraDurability = 0;
        int plateToughness = 0;
        int heads = 0;
        double drawspeed = 0;
        double rangeMult = 0;
        for(int i = 0; i<type.cats.length; i++){
            PartMaterial mat = getBlock(i,0,0).material;
            switch(type.cats[i]){
                case HANDLE:
                    handleMod+=mat.handleMod;
                    handleDurability+=mat.handleDurability;
                    handles++;
                    break;
                case HEAD:
                    headDurability+=mat.headDurability;
                    headSpeed+=mat.miningSpeed;
                    headAttack+=mat.attack;
                    headLevel = MiningLevel.values()[Math.max(headLevel.ordinal(), getBlock(i,0,0).material.level.ordinal())];
                    heads++;
                    break;
                case EXTRA:
                    extraDurability+=mat.extraDurability;
                    break;
                    
                case PLATES:
                    handleMod+=mat.plateMod;
                    handleDurability+=mat.plateDurability;
                    plateToughness+=mat.plateToughness;
                    handles++;
                    break;
                case CORE:
                    headDurability+=mat.coreDurability;
                    coreDefense+=mat.coreDef;
                    heads++;
                    break;
                case TRIM:
                    extraDurability+=mat.trimDurability;
                    break;
                    
                case ARROW_SHAFT:
                    handleMod+=mat.arrowShaftMod;
                    handleDurability+=mat.bonusAmmo;
                    break;
                case FLETCHING:
                    fletchingMod*=mat.fletchingModifier;
                    fletchingAcc*=mat.fletchingAccuracy;
                    break;
                    
                case BOW:
                    headDurability+=mat.headDurability;
                    headSpeed+=mat.miningSpeed;
                    headAttack+=mat.attack;
                    headLevel = MiningLevel.values()[Math.max(headLevel.ordinal(), getBlock(i,0,0).material.level.ordinal())];
                    heads++;
                    drawspeed+=mat.bowDrawspeed;
                    rangeMult+=mat.bowRangeMult;
                    attack+=mat.attack;
                    break;
                case BOWSTRING:
                    handleMod+=mat.bowstringMod;
                    handles++;
                    break;
                default:
                    throw new IllegalArgumentException("Haven't added support for this yet: "+type.cats[i].toString());
            }
        }
        handleMod/=handles;
        headSpeed/=heads;
        if(!Double.isFinite(handleMod))handleMod = 1;
        durability = (int) ((handleDurability+headDurability+extraDurability)*handleMod*fletchingMod);
        defense =  coreDefense;
        toughness = plateToughness;
        miningLevel = headLevel;
        miningSpeed = headSpeed;
        attack = headAttack+type.baseAttack;
        accuracy = fletchingAcc;
        this.drawspeed = drawspeed;
        range = rangeMult;
        return false;
    }
    @Override
    public FormattedText getTooltip(boolean full){
        return new FormattedText(valid?"Durability: "+durability
                                    +"\nMining Level: "+miningLevel.toString()
                                    +"\nMining Speed: "+miningSpeed
                                    +"\nAttack: "+attack
                                    +"\nDefense: "+defense
                                    +"\nToughness: "+toughness
                                    +"\nAccuracy: "+accuracy
                                    +"\nDrawspeed: "+drawspeed
                                    +"\nRange: "+range:"Invalid");
    }
    @Override
    public String getDescriptionTooltip(){
        return "A tool from Tinker's Construct (not a multiblock)";
    }
    @Override
    public Menu getResizeMenu(GUI gui, MenuEdit editor){
        new MenuPickEnum<ToolType>(gui, gui.menu, ToolType.values(), (typ)->{
            blockGrids.clear();
            type = typ;
            dimensions[0] = typ.ordinal();
            createBlockGrids();
        }).open();
        return null;
    }
    @Override
    public void getEditorSpaces(ArrayList<EditorSpace<ToolPart>> editorSpaces){
        editorSpaces.add(new EditorSpace<ToolPart>(0, 0, 0, type.parts.length-1, 0, 0){
            @Override
            public boolean isSpaceValid(ToolPart part, int x, int y, int z){
                return part.type==type.parts[x];
            }
            @Override
            public void createComponents(MenuEdit editor, ArrayList<Component> comps, int cellSize){
                comps.add(new MenuComponentEditorGrid(0, 0, cellSize, editor, TinkerTool.this, this, 0, 0, TinkerTool.this.type.parts.length-1, 0, Axis.Y, 0));
            }
        });
    }
    @Override
    public void getSuggestors(ArrayList<Suggestor> suggestors) {
        suggestors.add(new Suggestor<TinkerTool>("Completeness Suggestor", 1000, 1000l) {
            @Override
            public String getDescription(){
                return "Make sure all the parts are actually there";
            }
            @Override
            public void generateSuggestions(TinkerTool multiblock, Suggestor.SuggestionAcceptor suggestor) {
                for(int i = 0; i<type.cats.length; i++){
                    ToolPart part = multiblock.getBlock(i, 0, 0);
                    if(part!=null)continue;
                    List<ToolPart> parts = getAvailableBlocks();
                    for(ToolPart p : parts){
                        if(p.type==type.parts[i])suggestor.suggest(new Suggestion("Add "+p.getName(), new SetblockAction(i, 0, 0, p), null));
                    }
                }
            }
        });
        suggestors.add(new Suggestor<TinkerTool>("Mining Suggestor", 1000, 1000l) {
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<TinkerTool>("Mining Level", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.miningLevel.ordinal()-other.miningLevel.ordinal();
                    }
                });
                priorities.add(new Priority<TinkerTool>("Mining Speed", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.miningSpeed-other.miningSpeed;
                    }
                });
                priorities.add(new Priority<TinkerTool>("Durability", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.durability-other.durability;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Maximize mining level, then mining speed";
            }
            @Override
            public void generateSuggestions(TinkerTool multiblock, Suggestor.SuggestionAcceptor suggestor) {
                for(int i = 0; i<type.cats.length; i++){
                    ToolPart part = multiblock.getBlock(i, 0, 0);
                    List<ToolPart> parts = getAvailableBlocks();
                    for(ToolPart p : parts){
                        if(p.type==type.parts[i])suggestor.suggest(new Suggestion("M:"+(part==null?"+ ":part.getName()+" -> ")+p.getName(), new SetblockAction(i, 0, 0, p), priorities));
                    }
                }
            }
        });
        suggestors.add(new Suggestor<TinkerTool>("Attack Suggestor", 1000, 1000l) {
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<TinkerTool>("Attack", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.attack-other.attack;
                    }
                });
                priorities.add(new Priority<TinkerTool>("Accuracy", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.accuracy-other.accuracy;
                    }
                });
                priorities.add(new Priority<TinkerTool>("Durability", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.durability-other.durability;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Maximize damage";
            }
            @Override
            public void generateSuggestions(TinkerTool multiblock, Suggestor.SuggestionAcceptor suggestor) {
                for(int i = 0; i<type.cats.length; i++){
                    ToolPart part = multiblock.getBlock(i, 0, 0);
                    List<ToolPart> parts = getAvailableBlocks();
                    for(ToolPart p : parts){
                        if(p.type==type.parts[i])suggestor.suggest(new Suggestion("M:"+(part==null?"+ ":part.getName()+" -> ")+p.getName(), new SetblockAction(i, 0, 0, p), priorities));
                    }
                }
            }
        });
        suggestors.add(new Suggestor<TinkerTool>("Range Suggestor", 1000, 1000l) {
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<TinkerTool>("Accuracy", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.accuracy-other.accuracy;
                    }
                });
                priorities.add(new Priority<TinkerTool>("Range", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.range-other.range;
                    }
                });
                priorities.add(new Priority<TinkerTool>("Attack", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.attack-other.attack;
                    }
                });
                priorities.add(new Priority<TinkerTool>("Drawspeed", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return other.drawspeed-main.drawspeed;
                    }
                });
                priorities.add(new Priority<TinkerTool>("Durability", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.durability-other.durability;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Maximize bow range";
            }
            @Override
            public void generateSuggestions(TinkerTool multiblock, Suggestor.SuggestionAcceptor suggestor) {
                for(int i = 0; i<type.cats.length; i++){
                    ToolPart part = multiblock.getBlock(i, 0, 0);
                    List<ToolPart> parts = getAvailableBlocks();
                    for(ToolPart p : parts){
                        if(p.type==type.parts[i])suggestor.suggest(new Suggestion("M:"+(part==null?"+ ":part.getName()+" -> ")+p.getName(), new SetblockAction(i, 0, 0, p), priorities));
                    }
                }
            }
        });
        suggestors.add(new Suggestor<TinkerTool>("DPS Suggestor", 1000, 1000l) {
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<TinkerTool>("Accuracy", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.accuracy-other.accuracy;
                    }
                });
                priorities.add(new Priority<TinkerTool>("Attack", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.attack/main.drawspeed-other.attack/other.drawspeed;
                    }
                });
                priorities.add(new Priority<TinkerTool>("Range", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.range-other.range;
                    }
                });
                priorities.add(new Priority<TinkerTool>("Durability", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.durability-other.durability;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Maximize draw speed";
            }
            @Override
            public void generateSuggestions(TinkerTool multiblock, Suggestor.SuggestionAcceptor suggestor) {
                for(int i = 0; i<type.cats.length; i++){
                    ToolPart part = multiblock.getBlock(i, 0, 0);
                    List<ToolPart> parts = getAvailableBlocks();
                    for(ToolPart p : parts){
                        if(p.type==type.parts[i])suggestor.suggest(new Suggestion("M:"+(part==null?"+ ":part.getName()+" -> ")+p.getName(), new SetblockAction(i, 0, 0, p), priorities));
                    }
                }
            }
        });
        suggestors.add(new Suggestor<TinkerTool>("Defense Suggestor", 1000, 1000l) {
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<TinkerTool>("Defense", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.defense-other.defense;
                    }
                });
                priorities.add(new Priority<TinkerTool>("Toughness", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.toughness-other.toughness;
                    }
                });
                priorities.add(new Priority<TinkerTool>("Durability", true, true){
                    @Override
                    protected double doCompare(TinkerTool main, TinkerTool other){
                        return main.durability-other.durability;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Maximize Defense and Toughness";
            }
            @Override
            public void generateSuggestions(TinkerTool multiblock, Suggestor.SuggestionAcceptor suggestor) {
                for(int i = 0; i<type.cats.length; i++){
                    ToolPart part = multiblock.getBlock(i, 0, 0);
                    List<ToolPart> parts = getAvailableBlocks();
                    for(ToolPart p : parts){
                        if(p.type==type.parts[i])suggestor.suggest(new Suggestion("M:"+(part==null?"+ ":part.getName()+" -> ")+p.getName(), new SetblockAction(i, 0, 0, p), priorities));
                    }
                }
            }
        });
    }
    @Override
    public NCPFConfiguration getSpecificConfiguration(){
        return null;
    }
    @Override
    public MultiblockDesign convertToDesign(){
        return null;
    }
}