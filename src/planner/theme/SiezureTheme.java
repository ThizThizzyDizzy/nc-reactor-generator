package planner.theme;
import java.util.Objects;
import java.util.Random;
import planner.Core;
import planner.Main;
import planner.menu.dialog.MenuSiezureTheme;
import static planner.theme.Theme.themes;
import simplelibrary.image.Color;
public class SiezureTheme extends ColorTheme{
    private Random rand = new Random();
    private Boolean siezureAllowed = null;
    public SiezureTheme(String name){
        super(name);
    }
    @Override
    public void onSet(){
        if(Main.isBot)siezureAllowed = false;
        Thread t = new Thread(() -> {
            if(Core.gui==null)siezureAllowed = true;
            if(siezureAllowed==null){
                Core.gui.menu = new MenuSiezureTheme(Core.gui, Core.gui.menu, () -> {
                    siezureAllowed = true;
                }, () -> {
                    siezureAllowed = false;
                });
                while(siezureAllowed==null){
                    try{
                        Thread.sleep(5);//too lazy to use object.wait
                    }catch(InterruptedException ex){}
                }
                if(!siezureAllowed){
                    Core.setTheme(themes.get(0));
                    themes.remove(this);
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
    @Override
    public Color getKeywordBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordBackgroundColor();
        return rand();
    }
    @Override
    public Color getKeywordColorBlind(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordColorBlind();
        return rand();
    }
    @Override
    public Color getKeywordColorUnderhaul(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordColorUnderhaul();
        return rand();
    }
    @Override
    public Color getKeywordColorSymmetry(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordColorSymmetry();
        return rand();
    }
    @Override
    public Color getKeywordColorPriority(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordColorPriority();
        return rand();
    }
    @Override
    public Color getKeywordColorOverhaul(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordColorOverhaul();
        return rand();
    }
    @Override
    public Color getKeywordColorMultiblock(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordColorMultiblock();
        return rand();
    }
    @Override
    public Color getKeywordColorFuel(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordColorFuel();
        return rand();
    }
    @Override
    public Color getKeywordColorFormat(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordColorFormat();
        return rand();
    }
    @Override
    public Color getKeywordColorCuboid(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordColorCuboid();
        return rand();
    }
    @Override
    public Color getKeywordColorCube(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordColorCube();
        return rand();
    }
    @Override
    public Color getKeywordColorConfiguration(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordColorConfiguration();
        return rand();
    }
    @Override
    public Color getKeywordColorBlockRange(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getKeywordColorBlockRange();
        return rand();
    }
    @Override
    public Color getDecalColorAdjacentCell(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorAdjacentCell();
        return rand();
    }
    @Override
    public Color getDecalColorAdjacentModerator(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorAdjacentModerator();
        return rand();
    }
    @Override
    public Color getDecalColorAdjacentModeratorLine(float efficiency){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorAdjacentModeratorLine(efficiency);
        return rand();
    }
    @Override
    public Color getDecalColorUnderhaulModeratorLine(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorUnderhaulModeratorLine();
        return rand();
    }
    @Override
    public Color getDecalColorReflectorAdjacentModeratorLine(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorReflectorAdjacentModeratorLine();
        return rand();
    }
    @Override
    public Color getDecalColorOverhaulModeratorLine(float efficiency){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorOverhaulModeratorLine(efficiency);
        return rand();
    }
    @Override
    public Color getDecalTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalTextColor();
        return rand();
    }
    @Override
    public Color getDecalColorNeutronSourceTarget(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorNeutronSourceTarget();
        return rand();
    }
    @Override
    public Color getDecalColorNeutronSourceNoTarget(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorNeutronSourceNoTarget();
        return rand();
    }
    @Override
    public Color getDecalColorNeutronSourceLine(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorNeutronSourceLine();
        return rand();
    }
    @Override
    public Color getDecalColorNeutronSource(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorNeutronSource();
        return rand();
    }
    @Override
    public Color getDecalColorModeratorActive(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorModeratorActive();
        return rand();
    }
    @Override
    public Color getDecalColorMissingCasing(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorMissingCasing();
        return rand();
    }
    @Override
    public Color getDecalColorMissingBlade(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorMissingBlade();
        return rand();
    }
    @Override
    public Color getDecalColorIrradiatorAdjacentModeratorLine(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorIrradiatorAdjacentModeratorLine();
        return rand();
    }
    @Override
    public Color getDecalColorCellFlux(int flux, int criticality){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorCellFlux(flux, criticality);
        return rand();
    }
    @Override
    public Color getDecalColorBlockValid(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorBlockValid();
        return rand();
    }
    @Override
    public Color getDecalColorBlockInvalid(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDecalColorBlockInvalid();
        return rand();
    }
    @Override
    public Color getBlockColorOutlineInvalid(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getBlockColorOutlineInvalid();
        return rand();
    }
    @Override
    public Color getBlockColorOutlineActive(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getBlockColorOutlineActive();
        return rand();
    }
    @Override
    public Color getBlockColorSourceCircle(float efficiency, boolean selfPriming){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getBlockColorSourceCircle(efficiency, selfPriming);
        return rand();
    }
    @Override
    public Color getClusterOverheatingColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getClusterOverheatingColor();
        return rand();
    }
    @Override
    public Color getClusterOvercoolingColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getClusterOvercoolingColor();
        return rand();
    }
    @Override
    public Color getClusterDisconnectedColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getClusterDisconnectedColor();
        return rand();
    }
    @Override
    public Color getClusterInvalidColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getClusterInvalidColor();
        return rand();
    }
    @Override
    public Color getTooltipInvalidTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getTooltipInvalidTextColor();
        return rand();
    }
    @Override
    public Color getTooltipTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getTooltipTextColor();
        return rand();
    }
    @Override
    public Color getEditorToolTextColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorToolTextColor(index);
        return rand();
    }
    @Override
    public Color getEditorToolBackgroundColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorToolBackgroundColor(index);
        return rand();
    }
    @Override
    public Color getSelectionColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT. getSelectionColor();
        return rand();
    }
    @Override
    public Color getEditorBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorBackgroundColor();
        return rand();
    }
    @Override
    public Color getImageExportBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getImageExportBackgroundColor();
        return rand();
    }
    @Override
    public Color getImageExportTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getImageExportTextColor();
        return rand();
    }
    @Override
    public Color getComponentTextColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getComponentTextColor(index);
        return rand();
    }
    @Override
    public Color getMouseoverSelectedComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getMouseoverSelectedComponentColor(index);
        return rand();
    }
    @Override
    public Color getSelectedComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSelectedComponentColor(index);
        return rand();
    }
    @Override
    public Color getMouseoverComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getMouseoverComponentColor(index);
        return rand();
    }
    @Override
    public Color getComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT. getComponentColor(index);
        return rand();
    }
    @Override
    public Color getEditorBackgroundMouseoverColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorBackgroundMouseoverColor();
        return rand();
    }
    @Override
    public Color getEditorGridColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorGridColor();
        return rand();
    }
    @Override
    public Color getSuggestionOutlineColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSuggestionOutlineColor();
        return rand();
    }
    @Override
    public Color getEditorMouseoverLightColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorMouseoverLightColor();
        return rand();
    }
    @Override
    public Color getEditorMouseoverDarkColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorMouseoverDarkColor();
        return rand();
    }
    @Override
    public Color getEditorMouseoverLineColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorMouseoverLineColor();
        return rand();
    }
    @Override
    public Color getEditorListBackgroundMouseoverColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorListBackgroundMouseoverColor(index);
        return rand();
    }
    @Override
    public Color getEditorListBackgroundColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorListBackgroundColor(index);
        return rand();
    }
    @Override
    public Color getEditorListLightSelectedColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorListLightSelectedColor(index);
        return rand();
    }
    @Override
    public Color getEditorListDarkSelectedColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorListDarkSelectedColor(index);
        return rand();
    }
    @Override
    public Color getEditorListLightMouseoverColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorListLightMouseoverColor(index);
        return rand();
    }
    @Override
    public Color getEditorListDarkMouseoverColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getEditorListDarkMouseoverColor(index);
        return rand();
    }
    @Override
    public Color getMultiblockSelectedInputColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getMultiblockSelectedInputColor();
        return rand();
    }
    @Override
    public Color getMultiblockInvalidInputColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getMultiblockInvalidInputColor();
        return rand();
    }
    @Override
    public Color getSecondaryComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSecondaryComponentColor(index);
        return rand();
    }
    @Override
    public Color getProgressBarBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getProgressBarBackgroundColor();
        return rand();
    }
    @Override
    public Color getProgressBarColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getProgressBarColor();
        return rand();
    }
    @Override
    public Color getMultiblockDisplayBorderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getMultiblockDisplayBorderColor();
        return rand();
    }
    @Override
    public Color getMultiblockDisplayBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getMultiblockDisplayBackgroundColor();
        return rand();
    }
    @Override
    public Color getToggleBlockFadeout(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getToggleBlockFadeout(index);
        return rand();
    }
    @Override
    public Color getTutorialBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getTutorialBackgroundColor();
        return rand();
    }
    @Override
    public Color getScrollbarButtonColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getScrollbarButtonColor();
        return rand();
    }
    @Override
    public Color getBlockUnknownColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getBlockUnknownColor();
        return rand();
    }
    @Override
    public Color getBlockTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getBlockTextColor();
        return rand();
    }
    @Override
    public Color getScrollbarBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getScrollbarBackgroundColor();
        return rand();
    }
    @Override
    public Color getComponentPressedColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getComponentPressedColor(index);
        return rand();
    }
    @Override
    public Color getComponentMouseoverColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getComponentMouseoverColor(index);
        return rand();
    }
    @Override
    public Color getComponentDisabledColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getComponentDisabledColor(index);
        return rand();
    }
    @Override
    public Color getSecondaryComponentPressedColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSecondaryComponentPressedColor(index);
        return rand();
    }
    @Override
    public Color getSecondaryComponentMouseoverColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSecondaryComponentMouseoverColor(index);
        return rand();
    }
    @Override
    public Color getSecondaryComponentDisabledColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSecondaryComponentDisabledColor(index);
        return rand();
    }
    @Override
    public Color getSliderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT. getSliderColor();
        return rand();
    }
    @Override
    public Color getSliderPressedColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSliderPressedColor();
        return rand();
    }
    @Override
    public Color getSliderMouseoverColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSliderMouseoverColor();
        return rand();
    }
    @Override
    public Color getSliderDisabledColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSliderDisabledColor();
        return rand();
    }
    @Override
    public Color getSecondarySliderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSecondarySliderColor();
        return rand();
    }
    @Override
    public Color getSecondarySliderPressedColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSecondarySliderPressedColor();
        return rand();
    }
    @Override
    public Color getSecondarySliderMouseoverColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSecondarySliderMouseoverColor();
        return rand();
    }
    @Override
    public Color getSecondarySliderDisabledColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSecondarySliderDisabledColor();
        return rand();
    }
    @Override
    public Color getTextBoxBorderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getTextBoxBorderColor();
        return rand();
    }
    @Override
    public Color getTextBoxColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getTextBoxColor();
        return rand();
    }
    @Override
    public Color getTextViewBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getTextViewBackgroundColor();
        return rand();
    }
    @Override
    public Color getToggleBoxBorderColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getToggleBoxBorderColor(index);
        return rand();
    }
    @Override
    public Color getSecondaryToggleBoxBorderColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getToggleBoxBorderColor(index);
        return rand();
    }
    @Override
    public Color getToggleBoxBackgroundColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getToggleBoxBackgroundColor(index);
        return rand();
    }
    @Override
    public Color getToggleBoxSelectedColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getToggleBoxSelectedColor(index);
        return rand();
    }
    @Override
    public Color getToggleBoxMouseoverColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getToggleBoxMouseoverColor(index);
        return rand();
    }
    @Override
    public Color getMouseoverUnselectableComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getMouseoverUnselectableComponentColor(index);
        return rand();
    }
    @Override
    public Color getConfigurationSidebarColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getConfigurationSidebarColor();
        return rand();
    }
    @Override
    public Color getConfigurationWarningTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getConfigurationWarningTextColor();
        return rand();
    }
    @Override
    public Color getConfigurationDividerColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getConfigurationDividerColor();
        return rand();
    }
    @Override
    public Color getDialogBorderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDialogBorderColor();
        return rand();
    }
    @Override
    public Color getDialogBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDialogBackgroundColor();
        return rand();
    }
    @Override
    public Color getCreditsImageColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getCreditsImageColor();
        return rand();
    }
    @Override
    public Color getCreditsBrightImageColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getCreditsBrightImageColor();
        return rand();
    }
    @Override
    public Color getCreditsTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getCreditsTextColor();
        return rand();
    }
    @Override
    public Color get3DMultiblockOutlineColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.get3DMultiblockOutlineColor();
        return rand();
    }
    @Override
    public Color get3DDeviceoverOutlineColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.get3DDeviceoverOutlineColor();
        return rand();
    }
    @Override
    public Color getAddButtonTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getAddButtonTextColor();
        return rand();
    }
    @Override
    public Color getDeleteButtonTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getDeleteButtonTextColor();
        return rand();
    }
    @Override
    public Color getConvertButtonTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getConvertButtonTextColor();
        return rand();
    }
    @Override
    public Color getInputsButtonTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getInputsButtonTextColor();
        return rand();
    }
    @Override
    public Color getMetadataPanelBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getMetadataPanelBackgroundColor();
        return rand();
    }
    @Override
    public Color getMetadataPanelHeaderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getMetadataPanelHeaderColor();
        return rand();
    }
    @Override
    public Color getMetadataPanelTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getMetadataPanelTextColor();
        return rand();
    }
    @Override
    public Color getMultiblocksListHeaderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getMultiblocksListHeaderColor();
        return rand();
    }
    @Override
    public Color getRecoveryModeColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getRecoveryModeColor(index);
        return rand();
    }
    @Override
    public Color getRecoveryModeTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getRecoveryModeTextColor();
        return rand();
    }
    @Override
    public Color getRotateMultiblockTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getRotateMultiblockTextColor();
        return rand();
    }
    @Override
    public Color getResizeMenuTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getResizeMenuTextColor();
        return rand();
    }
    @Override
    public Color getMenuBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getMenuBackgroundColor();
        return rand();
    }
    @Override
    public Color getSettingsSidebarColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getSettingsSidebarColor();
        return rand();
    }
    @Override
    public Color getWhiteColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT. getWhiteColor();
        return rand();
    }
    @Override
    public Color getTutorialTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getTutorialTextColor();
        return rand();
    }
    @Override
    public Color getVRComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getVRComponentColor(index);
        return rand();
    }
    @Override
    public Color getVRDeviceoverComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getVRDeviceoverComponentColor(index);
        return rand();
    }
    @Override
    public Color getVRSelectedOutlineColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getVRSelectedOutlineColor(index);
        return rand();
    }
    @Override
    public Color getVRPanelOutlineColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getVRPanelOutlineColor();
        return rand();
    }
    @Override
    public Color getVRMultitoolTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.LIGHT.getVRMultitoolTextColor();
        return rand();
    }
    private Color rand(){
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }
}