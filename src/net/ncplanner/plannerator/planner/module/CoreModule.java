package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.ConditionAnd;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.ConditionEqual;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.ConditionGreater;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.ConditionGreaterEqual;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.ConditionLess;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.ConditionLessEqual;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.ConditionNot;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.ConditionNotEqual;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.ConditionOr;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.RandomQuantityMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.SingleMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstFloat;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstRandom;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorAddition;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorDivision;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorFloor;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorMaximum;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorMinimum;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorMultiplication;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorSubtraction;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingBoolean;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingFloat;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingPercent;
import net.ncplanner.plannerator.ncpf.element.NCPFBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFBlockTagElement;
import net.ncplanner.plannerator.ncpf.element.NCPFFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFFluidTagElement;
import net.ncplanner.plannerator.ncpf.element.NCPFItemElement;
import net.ncplanner.plannerator.ncpf.element.NCPFItemTagElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyItemElement;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.ncpf.element.NCPFOredictElement;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.ncpf.module.AirModule;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.GeneratorSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.module.MetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
import net.ncplanner.plannerator.planner.tutorial.TutorialFileReader;
public class CoreModule<T> extends Module<T>{
    public CoreModule(){
        super("core", true);
    }
    @Override
    public String getDisplayName(){
        return "Core";
    }
    @Override
    public String getDescription(){
        return "Contains general features appliccable to all multiblocks";
    }
    @Override
    public void registerNCPF(){
        registerNCPFElement(NCPFLegacyBlockElement::new);
        registerNCPFElement(NCPFLegacyItemElement::new);
        registerNCPFElement(NCPFLegacyFluidElement::new);
        registerNCPFElement(NCPFOredictElement::new);
        registerNCPFElement(NCPFBlockElement::new);
        registerNCPFElement(NCPFItemElement::new);
        registerNCPFElement(NCPFFluidElement::new);
        registerNCPFElement(NCPFBlockTagElement::new);
        registerNCPFElement(NCPFItemTagElement::new);
        registerNCPFElement(NCPFFluidTagElement::new);
        registerNCPFElement(NCPFModuleElement::new);
        
        registerNCPFModule(NCPFBlockRecipesModule::new);

        registerNCPFModule(MetadataModule::new);
        registerNCPFModule(ConfigurationMetadataModule::new);
        
        registerNCPFModule(DisplayNameModule::new);
        registerNCPFModule(TextureModule::new);
        
        registerNCPFModule(AirModule::new);
        
        registerNCPFModule(GeneratorSettingsModule::new);
        
        registerGeneratorMutator(SingleMutator::new);
        registerGeneratorMutator(RandomQuantityMutator::new);
        
        registerOperator(OperatorAddition::new);
        registerOperator(OperatorSubtraction::new);
        registerOperator(OperatorMultiplication::new);
        registerOperator(OperatorDivision::new);
        registerOperator(OperatorMinimum::new);
        registerOperator(OperatorMaximum::new);
        registerOperator(OperatorFloor::new);
        
        registerConstant(ConstInt::new);
        registerConstant(ConstFloat::new);
        registerConstant(ConstRandom::new);
        
        registerCondition(ConditionOr::new);
        registerCondition(ConditionNotEqual::new);
        registerCondition(ConditionNot::new);
        registerCondition(ConditionLessEqual::new);
        registerCondition(ConditionLess::new);
        registerCondition(ConditionGreaterEqual::new);
        registerCondition(ConditionGreater::new);
        registerCondition(ConditionEqual::new);
        registerCondition(ConditionAnd::new);
        
        registerParameter(SettingInt::new);
        registerParameter(SettingFloat::new);
        registerParameter(SettingPercent::new);
        registerParameter(SettingBoolean::new);
    }
    @Override
    public void addTutorials(){
        Tutorial.addTutorials("Planner",
                TutorialFileReader.read("tutorials/planner/basics.ncpt"),
                TutorialFileReader.read("tutorials/planner/editing.ncpt"));
    }
    private final EditorOverlay invalidBlocksOverlay = new EditorOverlay("Invalid Blocks", "Highlights invalid blocks with a red outline", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, AbstractBlock block, Multiblock multiblock){
            if(!block.isValid()){
                block.drawOutline(renderer, x, y, width, height, Core.theme.getBlockColorOutlineInvalid());
            }
        }
    };
    @Override
    public void getEditorOverlays(Multiblock multiblock, ArrayList<EditorOverlay> overlays){
        overlays.add(invalidBlocksOverlay);
    }
}