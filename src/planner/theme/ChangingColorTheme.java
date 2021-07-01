package planner.theme;
import java.util.function.Supplier;
import simplelibrary.image.Color;
public class ChangingColorTheme extends ColorTheme{
    protected ColorTheme current;
    private final Supplier<ColorTheme> theme;
    public ChangingColorTheme(String name, Supplier<ColorTheme> theme){
        super(name);
        this.theme = theme;
        current = theme.get();
    }
    @Override
    public void onSet(){
        current = theme.get();
    }
    @Override
    public Color getKeywordBackgroundColor(){
        return current.getKeywordBackgroundColor();
    }
    @Override
    public Color getKeywordColorBlind(){
        return current.getKeywordColorBlind();
    }
    @Override
    public Color getKeywordColorUnderhaul(){
        return current.getKeywordColorUnderhaul();
    }
    @Override
    public Color getKeywordColorSymmetry(){
        return current.getKeywordColorSymmetry();
    }
    @Override
    public Color getKeywordColorPriority(){
        return current.getKeywordColorPriority();
    }
    @Override
    public Color getKeywordColorOverhaul(){
        return current.getKeywordColorOverhaul();
    }
    @Override
    public Color getKeywordColorMultiblock(){
        return current.getKeywordColorMultiblock();
    }
    @Override
    public Color getKeywordColorFuel(){
        return current.getKeywordColorFuel();
    }
    @Override
    public Color getKeywordColorFormat(){
        return current.getKeywordColorFormat();
    }
    @Override
    public Color getKeywordColorCuboid(){
        return current.getKeywordColorCuboid();
    }
    @Override
    public Color getKeywordColorCube(){
        return current.getKeywordColorCube();
    }
    @Override
    public Color getKeywordColorConfiguration(){
        return current.getKeywordColorConfiguration();
    }
    @Override
    public Color getKeywordColorBlockRange(){
        return current.getKeywordColorBlockRange();
    }
    @Override
    public Color getDecalColorAdjacentCell(){
        return current.getDecalColorAdjacentCell();
    }
    @Override
    public Color getDecalColorAdjacentModerator(){
        return current.getDecalColorAdjacentModerator();
    }
    @Override
    public Color getDecalColorAdjacentModeratorLine(float efficiency){
        return current.getDecalColorAdjacentModeratorLine(efficiency);
    }
    @Override
    public Color getDecalColorUnderhaulModeratorLine(){
        return current.getDecalColorUnderhaulModeratorLine();
    }
    @Override
    public Color getDecalColorReflectorAdjacentModeratorLine(){
        return current.getDecalColorReflectorAdjacentModeratorLine();
    }
    @Override
    public Color getDecalColorOverhaulModeratorLine(float efficiency){
        return current.getDecalColorOverhaulModeratorLine(efficiency);
    }
    @Override
    public Color getDecalTextColor(){
        return current.getDecalTextColor();
    }
    @Override
    public Color getDecalColorNeutronSourceTarget(){
        return current.getDecalColorNeutronSourceTarget();
    }
    @Override
    public Color getDecalColorNeutronSourceNoTarget(){
        return current.getDecalColorNeutronSourceNoTarget();
    }
    @Override
    public Color getDecalColorNeutronSourceLine(){
        return current.getDecalColorNeutronSourceLine();
    }
    @Override
    public Color getDecalColorNeutronSource(){
        return current.getDecalColorNeutronSource();
    }
    @Override
    public Color getDecalColorModeratorActive(){
        return current.getDecalColorModeratorActive();
    }
    @Override
    public Color getDecalColorMissingCasing(){
        return current.getDecalColorMissingCasing();
    }
    @Override
    public Color getDecalColorMissingBlade(){
        return current.getDecalColorMissingBlade();
    }
    @Override
    public Color getDecalColorIrradiatorAdjacentModeratorLine(){
        return current.getDecalColorIrradiatorAdjacentModeratorLine();
    }
    @Override
    public Color getDecalColorCellFlux(int flux, int criticality){
        return current.getDecalColorCellFlux(flux, criticality);
    }
    @Override
    public Color getDecalColorBlockValid(){
        return current.getDecalColorBlockValid();
    }
    @Override
    public Color getDecalColorBlockInvalid(){
        return current.getDecalColorBlockInvalid();
    }
    @Override
    public Color getBlockColorOutlineInvalid(){
        return current.getBlockColorOutlineInvalid();
    }
    @Override
    public Color getBlockColorOutlineActive(){
        return current.getBlockColorOutlineActive();
    }
    @Override
    public Color getBlockColorSourceCircle(float efficiency, boolean selfPriming){
        return current.getBlockColorSourceCircle(efficiency, selfPriming);
    }
    @Override
    public Color getClusterOverheatingColor(){
        return current.getClusterOverheatingColor();
    }
    @Override
    public Color getClusterOvercoolingColor(){
        return current.getClusterOvercoolingColor();
    }
    @Override
    public Color getClusterDisconnectedColor(){
        return current.getClusterDisconnectedColor();
    }
    @Override
    public Color getClusterInvalidColor(){
        return current.getClusterInvalidColor();
    }
    @Override
    public Color getTooltipInvalidTextColor(){
        return current.getTooltipInvalidTextColor();
    }
    @Override
    public Color getTooltipTextColor(){
        return current.getTooltipTextColor();
    }
    @Override
    public Color getEditorToolTextColor(int index){
        return current.getEditorToolTextColor(index);
    }
    @Override
    public Color getEditorToolBackgroundColor(int index){
        return current.getEditorToolBackgroundColor(index);
    }
    @Override
    public Color getSelectionColor(){
        return current.getSelectionColor();
    }
    @Override
    public Color getEditorBackgroundColor(){
        return current.getEditorBackgroundColor();
    }
    @Override
    public Color getImageExportBackgroundColor(){
        return current.getImageExportBackgroundColor();
    }
    @Override
    public Color getImageExportTextColor(){
        return current.getImageExportTextColor();
    }
    @Override
    public Color getComponentTextColor(int index){
        return current.getComponentTextColor(index);
    }
    @Override
    public Color getMouseoverSelectedComponentColor(int index){
        return current.getMouseoverSelectedComponentColor(index);
    }
    @Override
    public Color getSelectedComponentColor(int index){
        return current.getSelectedComponentColor(index);
    }
    @Override
    public Color getMouseoverComponentColor(int index){
        return current.getMouseoverComponentColor(index);
    }
    @Override
    public Color getComponentColor(int index){
        return current.getComponentColor(index);
    }
    @Override
    public Color getEditorBackgroundMouseoverColor(){
        return current.getEditorBackgroundMouseoverColor();
    }
    @Override
    public Color getEditorGridColor(){
        return current.getEditorGridColor();
    }
    @Override
    public Color getSuggestionOutlineColor(){
        return current.getSuggestionOutlineColor();
    }
    @Override
    public Color getEditorMouseoverLightColor(){
        return current.getEditorMouseoverLightColor();
    }
    @Override
    public Color getEditorMouseoverDarkColor(){
        return current.getEditorMouseoverDarkColor();
    }
    @Override
    public Color getEditorMouseoverLineColor(){
        return current.getEditorMouseoverLineColor();
    }
    @Override
    public Color getEditorListBackgroundMouseoverColor(int index){
        return current.getEditorListBackgroundMouseoverColor(index);
    }
    @Override
    public Color getEditorListBackgroundColor(int index){
        return current.getEditorListBackgroundColor(index);
    }
    @Override
    public Color getEditorListLightSelectedColor(int index){
        return current.getEditorListLightSelectedColor(index);
    }
    @Override
    public Color getEditorListDarkSelectedColor(int index){
        return current.getEditorListDarkSelectedColor(index);
    }
    @Override
    public Color getEditorListLightMouseoverColor(int index){
        return current.getEditorListLightMouseoverColor(index);
    }
    @Override
    public Color getEditorListDarkMouseoverColor(int index){
        return current.getEditorListDarkMouseoverColor(index);
    }
    @Override
    public Color getMultiblockSelectedInputColor(){
        return current.getMultiblockSelectedInputColor();
    }
    @Override
    public Color getMultiblockInvalidInputColor(){
        return current.getMultiblockInvalidInputColor();
    }
    @Override
    public Color getSecondaryComponentColor(int index){
        return current.getSecondaryComponentColor(index);
    }
    @Override
    public Color getProgressBarBackgroundColor(){
        return current.getProgressBarBackgroundColor();
    }
    @Override
    public Color getProgressBarColor(){
        return current.getProgressBarColor();
    }
    @Override
    public Color getMultiblockDisplayBorderColor(){
        return current.getMultiblockDisplayBorderColor();
    }
    @Override
    public Color getMultiblockDisplayBackgroundColor(){
        return current.getMultiblockDisplayBackgroundColor();
    }
    @Override
    public Color getToggleBlockFadeout(int index){
        return current.getToggleBlockFadeout(index);
    }
    @Override
    public Color getTutorialBackgroundColor(){
        return current.getTutorialBackgroundColor();
    }
    @Override
    public Color getScrollbarButtonColor(){
        return current.getScrollbarButtonColor();
    }
    @Override
    public Color getBlockUnknownColor(){
        return current.getBlockUnknownColor();
    }
    @Override
    public Color getBlockTextColor(){
        return current.getBlockTextColor();
    }
    @Override
    public Color getScrollbarBackgroundColor(){
        return current.getScrollbarBackgroundColor();
    }
    @Override
    public Color getComponentPressedColor(int index){
        return current.getComponentPressedColor(index);
    }
    @Override
    public Color getComponentMouseoverColor(int index){
        return current.getComponentMouseoverColor(index);
    }
    @Override
    public Color getComponentDisabledColor(int index){
        return current.getComponentDisabledColor(index);
    }
    @Override
    public Color getSecondaryComponentPressedColor(int index){
        return current.getSecondaryComponentPressedColor(index);
    }
    @Override
    public Color getSecondaryComponentMouseoverColor(int index){
        return current.getSecondaryComponentMouseoverColor(index);
    }
    @Override
    public Color getSecondaryComponentDisabledColor(int index){
        return current.getSecondaryComponentDisabledColor(index);
    }
    @Override
    public Color getSliderColor(){
        return current.getSliderColor();
    }
    @Override
    public Color getSliderPressedColor(){
        return current.getSliderPressedColor();
    }
    @Override
    public Color getSliderMouseoverColor(){
        return current.getSliderMouseoverColor();
    }
    @Override
    public Color getSliderDisabledColor(){
        return current.getSliderDisabledColor();
    }
    @Override
    public Color getSecondarySliderColor(){
        return current.getSecondarySliderColor();
    }
    @Override
    public Color getSecondarySliderPressedColor(){
        return current.getSecondarySliderPressedColor();
    }
    @Override
    public Color getSecondarySliderMouseoverColor(){
        return current.getSecondarySliderMouseoverColor();
    }
    @Override
    public Color getSecondarySliderDisabledColor(){
        return current.getSecondarySliderDisabledColor();
    }
    @Override
    public Color getTextBoxBorderColor(){
        return current.getTextBoxBorderColor();
    }
    @Override
    public Color getTextBoxColor(){
        return current.getTextBoxColor();
    }
    @Override
    public Color getTextViewBackgroundColor(){
        return current.getTextViewBackgroundColor();
    }
    @Override
    public Color getToggleBoxBorderColor(int index){
        return current.getToggleBoxBorderColor(index);
    }
    @Override
    public Color getSecondaryToggleBoxBorderColor(int index){
        return current.getSecondaryToggleBoxBorderColor(index);
    }
    @Override
    public Color getToggleBoxBackgroundColor(int index){
        return current.getToggleBoxBackgroundColor(index);
    }
    @Override
    public Color getToggleBoxSelectedColor(int index){
        return current.getToggleBoxSelectedColor(index);
    }
    @Override
    public Color getToggleBoxMouseoverColor(int index){
        return current.getToggleBoxMouseoverColor(index);
    }
    @Override
    public Color getMouseoverUnselectableComponentColor(int index){
        return current.getMouseoverUnselectableComponentColor(index);
    }
    @Override
    public Color getConfigurationSidebarColor(){
        return current.getConfigurationSidebarColor();
    }
    @Override
    public Color getConfigurationWarningTextColor(){
        return current.getConfigurationWarningTextColor();
    }
    @Override
    public Color getConfigurationDividerColor(){
        return current.getConfigurationDividerColor();
    }
    @Override
    public Color getDialogBorderColor(){
        return current.getDialogBorderColor();
    }
    @Override
    public Color getDialogBackgroundColor(){
        return current.getDialogBackgroundColor();
    }
    @Override
    public Color getCreditsImageColor(){
        return current.getCreditsImageColor();
    }
    @Override
    public Color getCreditsBrightImageColor(){
        return current.getCreditsBrightImageColor();
    }
    @Override
    public Color getCreditsTextColor(){
        return current.getCreditsTextColor();
    }
    @Override
    public Color get3DMultiblockOutlineColor(){
        return current.get3DMultiblockOutlineColor();
    }
    @Override
    public Color get3DDeviceoverOutlineColor(){
        return current.get3DDeviceoverOutlineColor();
    }
    @Override
    public Color getAddButtonTextColor(){
        return current.getAddButtonTextColor();
    }
    @Override
    public Color getDeleteButtonTextColor(){
        return current.getDeleteButtonTextColor();
    }
    @Override
    public Color getConvertButtonTextColor(){
        return current.getConvertButtonTextColor();
    }
    @Override
    public Color getInputsButtonTextColor(){
        return current.getInputsButtonTextColor();
    }
    @Override
    public Color getMetadataPanelBackgroundColor(){
        return current.getMetadataPanelBackgroundColor();
    }
    @Override
    public Color getMetadataPanelHeaderColor(){
        return current.getMetadataPanelHeaderColor();
    }
    @Override
    public Color getMetadataPanelTextColor(){
        return current.getMetadataPanelTextColor();
    }
    @Override
    public Color getMultiblocksListHeaderColor(){
        return current.getMultiblocksListHeaderColor();
    }
    @Override
    public Color getRecoveryModeColor(int index){
        return current.getRecoveryModeColor(index);
    }
    @Override
    public Color getRecoveryModeTextColor(){
        return current.getRecoveryModeTextColor();
    }
    @Override
    public Color getRotateMultiblockTextColor(){
        return current.getRotateMultiblockTextColor();
    }
    @Override
    public Color getResizeMenuTextColor(){
        return current.getResizeMenuTextColor();
    }
    @Override
    public Color getMenuBackgroundColor(){
        return current.getMenuBackgroundColor();
    }
    @Override
    public Color getSettingsSidebarColor(){
        return current.getSettingsSidebarColor();
    }
    @Override
    public Color getWhiteColor(){
        return current.getWhiteColor();
    }
    @Override
    public Color getTutorialTextColor(){
        return current.getTutorialTextColor();
    }
    @Override
    public Color getVRComponentColor(int index){
        return current.getVRComponentColor(index);
    }
    @Override
    public Color getVRDeviceoverComponentColor(int index){
        return current.getVRDeviceoverComponentColor(index);
    }
    @Override
    public Color getVRSelectedOutlineColor(int index){
        return current.getVRSelectedOutlineColor(index);
    }
    @Override
    public Color getVRPanelOutlineColor(){
        return current.getVRPanelOutlineColor();
    }
    @Override
    public Color getVRMultitoolTextColor(){
        return current.getVRMultitoolTextColor();
    }
    @Override
    public void drawThemeButtonBackground(double x, double y, double width, double height, boolean darker, boolean enabled, boolean pressed, boolean mouseOver){
        current.drawThemeButtonBackground(x, y, width, height, darker, enabled, pressed, mouseOver);
    }
    @Override
    public void drawThemeButtonText(double x, double y, double width, double height, double textHeight, String text){
        current.drawThemeButtonText(x, y, width, height, textHeight, text);
    }
    @Override
    public Color getSettingsMergeTextColor(){
        return current.getSettingsMergeTextColor();
    }
}