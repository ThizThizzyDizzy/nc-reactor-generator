package planner.theme;
import java.util.Objects;
import java.util.Random;
import planner.Core;
import planner.Main;
import planner.menu.dialog.MenuSiezureTheme;
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
                    Core.setTheme(Theme.QUESTIONQUESTIONQUESTION);
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
    @Override
    public Color getKeywordBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordBackgroundColor();
        return rand();
    }
    @Override
    public Color getKeywordColorBlind(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordColorBlind();
        return rand();
    }
    @Override
    public Color getKeywordColorUnderhaul(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordColorUnderhaul();
        return rand();
    }
    @Override
    public Color getKeywordColorSymmetry(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordColorSymmetry();
        return rand();
    }
    @Override
    public Color getKeywordColorPriority(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordColorPriority();
        return rand();
    }
    @Override
    public Color getKeywordColorOverhaul(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordColorOverhaul();
        return rand();
    }
    @Override
    public Color getKeywordColorMultiblock(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordColorMultiblock();
        return rand();
    }
    @Override
    public Color getKeywordColorFuel(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordColorFuel();
        return rand();
    }
    @Override
    public Color getKeywordColorFormat(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordColorFormat();
        return rand();
    }
    @Override
    public Color getKeywordColorCuboid(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordColorCuboid();
        return rand();
    }
    @Override
    public Color getKeywordColorCube(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordColorCube();
        return rand();
    }
    @Override
    public Color getKeywordColorConfiguration(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordColorConfiguration();
        return rand();
    }
    @Override
    public Color getKeywordColorBlockRange(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getKeywordColorBlockRange();
        return rand();
    }
    @Override
    public Color getDecalColorAdjacentCell(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorAdjacentCell();
        return rand();
    }
    @Override
    public Color getDecalColorAdjacentModerator(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorAdjacentModerator();
        return rand();
    }
    @Override
    public Color getDecalColorAdjacentModeratorLine(float efficiency){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorAdjacentModeratorLine(efficiency);
        return rand();
    }
    @Override
    public Color getDecalColorUnderhaulModeratorLine(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorUnderhaulModeratorLine();
        return rand();
    }
    @Override
    public Color getDecalColorReflectorAdjacentModeratorLine(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorReflectorAdjacentModeratorLine();
        return rand();
    }
    @Override
    public Color getDecalColorOverhaulModeratorLine(float efficiency){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorOverhaulModeratorLine(efficiency);
        return rand();
    }
    @Override
    public Color getDecalTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalTextColor();
        return rand();
    }
    @Override
    public Color getDecalColorNeutronSourceTarget(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorNeutronSourceTarget();
        return rand();
    }
    @Override
    public Color getDecalColorNeutronSourceNoTarget(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorNeutronSourceNoTarget();
        return rand();
    }
    @Override
    public Color getDecalColorNeutronSourceLine(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorNeutronSourceLine();
        return rand();
    }
    @Override
    public Color getDecalColorNeutronSource(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorNeutronSource();
        return rand();
    }
    @Override
    public Color getDecalColorModeratorActive(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorModeratorActive();
        return rand();
    }
    @Override
    public Color getDecalColorMissingCasing(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorMissingCasing();
        return rand();
    }
    @Override
    public Color getDecalColorMissingBlade(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorMissingBlade();
        return rand();
    }
    @Override
    public Color getDecalColorIrradiatorAdjacentModeratorLine(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorIrradiatorAdjacentModeratorLine();
        return rand();
    }
    @Override
    public Color getDecalColorCellFlux(int flux, int criticality){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorCellFlux(flux, criticality);
        return rand();
    }
    @Override
    public Color getDecalColorBlockValid(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorBlockValid();
        return rand();
    }
    @Override
    public Color getDecalColorBlockInvalid(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDecalColorBlockInvalid();
        return rand();
    }
    @Override
    public Color getBlockColorOutlineInvalid(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getBlockColorOutlineInvalid();
        return rand();
    }
    @Override
    public Color getBlockColorOutlineActive(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getBlockColorOutlineActive();
        return rand();
    }
    @Override
    public Color getBlockColorSourceCircle(float efficiency, boolean selfPriming){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getBlockColorSourceCircle(efficiency, selfPriming);
        return rand();
    }
    @Override
    public Color getClusterOverheatingColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getClusterOverheatingColor();
        return rand();
    }
    @Override
    public Color getClusterOvercoolingColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getClusterOvercoolingColor();
        return rand();
    }
    @Override
    public Color getClusterDisconnectedColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getClusterDisconnectedColor();
        return rand();
    }
    @Override
    public Color getClusterInvalidColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getClusterInvalidColor();
        return rand();
    }
    @Override
    public Color getTooltipInvalidTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getTooltipInvalidTextColor();
        return rand();
    }
    @Override
    public Color getTooltipTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getTooltipTextColor();
        return rand();
    }
    @Override
    public Color getEditorToolTextColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorToolTextColor(index);
        return rand();
    }
    @Override
    public Color getEditorToolBackgroundColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorToolBackgroundColor(index);
        return rand();
    }
    @Override
    public Color getSelectionColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD. getSelectionColor();
        return rand();
    }
    @Override
    public Color getEditorBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorBackgroundColor();
        return rand();
    }
    @Override
    public Color getImageExportBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getImageExportBackgroundColor();
        return rand();
    }
    @Override
    public Color getImageExportTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getImageExportTextColor();
        return rand();
    }
    @Override
    public Color getComponentTextColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getComponentTextColor(index);
        return rand();
    }
    @Override
    public Color getMouseoverSelectedComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getMouseoverSelectedComponentColor(index);
        return rand();
    }
    @Override
    public Color getSelectedComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSelectedComponentColor(index);
        return rand();
    }
    @Override
    public Color getMouseoverComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getMouseoverComponentColor(index);
        return rand();
    }
    @Override
    public Color getComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD. getComponentColor(index);
        return rand();
    }
    @Override
    public Color getEditorBackgroundMouseoverColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorBackgroundMouseoverColor();
        return rand();
    }
    @Override
    public Color getEditorGridColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorGridColor();
        return rand();
    }
    @Override
    public Color getSuggestionOutlineColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSuggestionOutlineColor();
        return rand();
    }
    @Override
    public Color getEditorMouseoverLightColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorMouseoverLightColor();
        return rand();
    }
    @Override
    public Color getEditorMouseoverDarkColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorMouseoverDarkColor();
        return rand();
    }
    @Override
    public Color getEditorMouseoverLineColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorMouseoverLineColor();
        return rand();
    }
    @Override
    public Color getEditorListBackgroundMouseoverColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorListBackgroundMouseoverColor(index);
        return rand();
    }
    @Override
    public Color getEditorListBackgroundColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorListBackgroundColor(index);
        return rand();
    }
    @Override
    public Color getEditorListLightSelectedColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorListLightSelectedColor(index);
        return rand();
    }
    @Override
    public Color getEditorListDarkSelectedColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorListDarkSelectedColor(index);
        return rand();
    }
    @Override
    public Color getEditorListLightMouseoverColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorListLightMouseoverColor(index);
        return rand();
    }
    @Override
    public Color getEditorListDarkMouseoverColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getEditorListDarkMouseoverColor(index);
        return rand();
    }
    @Override
    public Color getMultiblockSelectedInputColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getMultiblockSelectedInputColor();
        return rand();
    }
    @Override
    public Color getMultiblockInvalidInputColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getMultiblockInvalidInputColor();
        return rand();
    }
    @Override
    public Color getSecondaryComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSecondaryComponentColor(index);
        return rand();
    }
    @Override
    public Color getProgressBarBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getProgressBarBackgroundColor();
        return rand();
    }
    @Override
    public Color getProgressBarColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getProgressBarColor();
        return rand();
    }
    @Override
    public Color getMultiblockDisplayBorderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getMultiblockDisplayBorderColor();
        return rand();
    }
    @Override
    public Color getMultiblockDisplayBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getMultiblockDisplayBackgroundColor();
        return rand();
    }
    @Override
    public Color getToggleBlockFadeout(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getToggleBlockFadeout(index);
        return rand();
    }
    @Override
    public Color getTutorialBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getTutorialBackgroundColor();
        return rand();
    }
    @Override
    public Color getScrollbarButtonColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getScrollbarButtonColor();
        return rand();
    }
    @Override
    public Color getBlockUnknownColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getBlockUnknownColor();
        return rand();
    }
    @Override
    public Color getBlockTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getBlockTextColor();
        return rand();
    }
    @Override
    public Color getScrollbarBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getScrollbarBackgroundColor();
        return rand();
    }
    @Override
    public Color getComponentPressedColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getComponentPressedColor(index);
        return rand();
    }
    @Override
    public Color getComponentMouseoverColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getComponentMouseoverColor(index);
        return rand();
    }
    @Override
    public Color getComponentDisabledColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getComponentDisabledColor(index);
        return rand();
    }
    @Override
    public Color getSecondaryComponentPressedColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSecondaryComponentPressedColor(index);
        return rand();
    }
    @Override
    public Color getSecondaryComponentMouseoverColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSecondaryComponentMouseoverColor(index);
        return rand();
    }
    @Override
    public Color getSecondaryComponentDisabledColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSecondaryComponentDisabledColor(index);
        return rand();
    }
    @Override
    public Color getSliderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD. getSliderColor();
        return rand();
    }
    @Override
    public Color getSliderPressedColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSliderPressedColor();
        return rand();
    }
    @Override
    public Color getSliderMouseoverColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSliderMouseoverColor();
        return rand();
    }
    @Override
    public Color getSliderDisabledColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSliderDisabledColor();
        return rand();
    }
    @Override
    public Color getSecondarySliderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSecondarySliderColor();
        return rand();
    }
    @Override
    public Color getSecondarySliderPressedColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSecondarySliderPressedColor();
        return rand();
    }
    @Override
    public Color getSecondarySliderMouseoverColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSecondarySliderMouseoverColor();
        return rand();
    }
    @Override
    public Color getSecondarySliderDisabledColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSecondarySliderDisabledColor();
        return rand();
    }
    @Override
    public Color getTextBoxBorderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getTextBoxBorderColor();
        return rand();
    }
    @Override
    public Color getTextBoxColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getTextBoxColor();
        return rand();
    }
    @Override
    public Color getTextViewBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getTextViewBackgroundColor();
        return rand();
    }
    @Override
    public Color getToggleBoxBorderColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getToggleBoxBorderColor(index);
        return rand();
    }
    @Override
    public Color getSecondaryToggleBoxBorderColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getToggleBoxBorderColor(index);
        return rand();
    }
    @Override
    public Color getToggleBoxBackgroundColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getToggleBoxBackgroundColor(index);
        return rand();
    }
    @Override
    public Color getToggleBoxSelectedColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getToggleBoxSelectedColor(index);
        return rand();
    }
    @Override
    public Color getToggleBoxMouseoverColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getToggleBoxMouseoverColor(index);
        return rand();
    }
    @Override
    public Color getMouseoverUnselectableComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getMouseoverUnselectableComponentColor(index);
        return rand();
    }
    @Override
    public Color getConfigurationSidebarColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getConfigurationSidebarColor();
        return rand();
    }
    @Override
    public Color getConfigurationWarningTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getConfigurationWarningTextColor();
        return rand();
    }
    @Override
    public Color getConfigurationDividerColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getConfigurationDividerColor();
        return rand();
    }
    @Override
    public Color getDialogBorderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDialogBorderColor();
        return rand();
    }
    @Override
    public Color getDialogBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDialogBackgroundColor();
        return rand();
    }
    @Override
    public Color getCreditsImageColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getCreditsImageColor();
        return rand();
    }
    @Override
    public Color getCreditsBrightImageColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getCreditsBrightImageColor();
        return rand();
    }
    @Override
    public Color getCreditsTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getCreditsTextColor();
        return rand();
    }
    @Override
    public Color get3DMultiblockOutlineColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.get3DMultiblockOutlineColor();
        return rand();
    }
    @Override
    public Color get3DDeviceoverOutlineColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.get3DDeviceoverOutlineColor();
        return rand();
    }
    @Override
    public Color getAddButtonTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getAddButtonTextColor();
        return rand();
    }
    @Override
    public Color getDeleteButtonTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getDeleteButtonTextColor();
        return rand();
    }
    @Override
    public Color getConvertButtonTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getConvertButtonTextColor();
        return rand();
    }
    @Override
    public Color getInputsButtonTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getInputsButtonTextColor();
        return rand();
    }
    @Override
    public Color getMetadataPanelBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getMetadataPanelBackgroundColor();
        return rand();
    }
    @Override
    public Color getMetadataPanelHeaderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getMetadataPanelHeaderColor();
        return rand();
    }
    @Override
    public Color getMetadataPanelTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getMetadataPanelTextColor();
        return rand();
    }
    @Override
    public Color getMultiblocksListHeaderColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getMultiblocksListHeaderColor();
        return rand();
    }
    @Override
    public Color getRecoveryModeColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getRecoveryModeColor(index);
        return rand();
    }
    @Override
    public Color getRecoveryModeTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getRecoveryModeTextColor();
        return rand();
    }
    @Override
    public Color getRotateMultiblockTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getRotateMultiblockTextColor();
        return rand();
    }
    @Override
    public Color getResizeMenuTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getResizeMenuTextColor();
        return rand();
    }
    @Override
    public Color getMenuBackgroundColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getMenuBackgroundColor();
        return rand();
    }
    @Override
    public Color getSettingsSidebarColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSettingsSidebarColor();
        return rand();
    }
    @Override
    public Color getWhiteColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD. getWhiteColor();
        return rand();
    }
    @Override
    public Color getTutorialTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getTutorialTextColor();
        return rand();
    }
    @Override
    public Color getVRComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getVRComponentColor(index);
        return rand();
    }
    @Override
    public Color getVRDeviceoverComponentColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getVRDeviceoverComponentColor(index);
        return rand();
    }
    @Override
    public Color getVRSelectedOutlineColor(int index){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getVRSelectedOutlineColor(index);
        return rand();
    }
    @Override
    public Color getVRPanelOutlineColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getVRPanelOutlineColor();
        return rand();
    }
    @Override
    public Color getVRMultitoolTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getVRMultitoolTextColor();
        return rand();
    }
    private Color rand(){
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }
    @Override
    public boolean shouldContantlyUpdateBackground(){
        return true;
    }
    @Override
    public Color getSettingsMergeTextColor(){
        if(!Objects.equals(siezureAllowed, Boolean.TRUE))return Theme.STANDARD.getSettingsMergeTextColor();
        return rand();
    }
}