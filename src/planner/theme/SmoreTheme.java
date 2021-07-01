package planner.theme;
import planner.Core;
import simplelibrary.image.Color;
import static simplelibrary.opengl.Renderer2D.*;
public class SmoreTheme extends ColorTheme{
    private final ColorTheme cracker;
    private final ColorTheme chocolate;
    private final ColorTheme marshmallow;
    public SmoreTheme(String name, ColorTheme cracker, ColorTheme chocolate, ColorTheme marshmallow){
        super(name);
        this.cracker = cracker;
        this.chocolate = chocolate;
        this.marshmallow = marshmallow;
    }
    @Override
    public Color getKeywordBackgroundColor(){
        return cracker.getKeywordBackgroundColor();
    }
    @Override
    public Color getKeywordColorBlind(){
        return cracker.getKeywordColorBlind();
    }
    @Override
    public Color getKeywordColorUnderhaul(){
        return cracker.getKeywordColorUnderhaul();
    }
    @Override
    public Color getKeywordColorSymmetry(){
        return cracker.getKeywordColorSymmetry();
    }
    @Override
    public Color getKeywordColorPriority(){
        return cracker.getKeywordColorPriority();
    }
    @Override
    public Color getKeywordColorOverhaul(){
        return cracker.getKeywordColorOverhaul();
    }
    @Override
    public Color getKeywordColorMultiblock(){
        return cracker.getKeywordColorMultiblock();
    }
    @Override
    public Color getKeywordColorFuel(){
        return cracker.getKeywordColorFuel();
    }
    @Override
    public Color getKeywordColorFormat(){
        return cracker.getKeywordColorFormat();
    }
    @Override
    public Color getKeywordColorCuboid(){
        return cracker.getKeywordColorCuboid();
    }
    @Override
    public Color getKeywordColorCube(){
        return cracker.getKeywordColorCube();
    }
    @Override
    public Color getKeywordColorConfiguration(){
        return cracker.getKeywordColorConfiguration();
    }
    @Override
    public Color getKeywordColorBlockRange(){
        return cracker.getKeywordColorBlockRange();
    }
    @Override
    public Color getDecalColorAdjacentCell(){
        return cracker.getDecalColorAdjacentCell();
    }
    @Override
    public Color getDecalColorAdjacentModerator(){
        return cracker.getDecalColorAdjacentModerator();
    }
    @Override
    public Color getDecalColorAdjacentModeratorLine(float efficiency){
        return cracker.getDecalColorAdjacentModeratorLine(efficiency);
    }
    @Override
    public Color getDecalColorUnderhaulModeratorLine(){
        return cracker.getDecalColorUnderhaulModeratorLine();
    }
    @Override
    public Color getDecalColorReflectorAdjacentModeratorLine(){
        return cracker.getDecalColorReflectorAdjacentModeratorLine();
    }
    @Override
    public Color getDecalColorOverhaulModeratorLine(float efficiency){
        return cracker.getDecalColorOverhaulModeratorLine(efficiency);
    }
    @Override
    public Color getDecalTextColor(){
        return chocolate.getDecalTextColor();
    }
    @Override
    public Color getDecalColorNeutronSourceTarget(){
        return cracker.getDecalColorNeutronSourceTarget();
    }
    @Override
    public Color getDecalColorNeutronSourceNoTarget(){
        return cracker.getDecalColorNeutronSourceNoTarget();
    }
    @Override
    public Color getDecalColorNeutronSourceLine(){
        return cracker.getDecalColorNeutronSourceLine();
    }
    @Override
    public Color getDecalColorNeutronSource(){
        return cracker.getDecalColorNeutronSource();
    }
    @Override
    public Color getDecalColorModeratorActive(){
        return cracker.getDecalColorModeratorActive();
    }
    @Override
    public Color getDecalColorMissingCasing(){
        return cracker.getDecalColorMissingCasing();
    }
    @Override
    public Color getDecalColorMissingBlade(){
        return cracker.getDecalColorMissingBlade();
    }
    @Override
    public Color getDecalColorIrradiatorAdjacentModeratorLine(){
        return cracker.getDecalColorIrradiatorAdjacentModeratorLine();
    }
    @Override
    public Color getDecalColorCellFlux(int flux, int criticality){
        return cracker.getDecalColorCellFlux(flux, criticality);
    }
    @Override
    public Color getDecalColorBlockValid(){
        return cracker.getDecalColorBlockValid();
    }
    @Override
    public Color getDecalColorBlockInvalid(){
        return cracker.getDecalColorBlockInvalid();
    }
    @Override
    public Color getBlockColorOutlineInvalid(){
        return cracker.getBlockColorOutlineInvalid();
    }
    @Override
    public Color getBlockColorOutlineActive(){
        return cracker.getBlockColorOutlineActive();
    }
    @Override
    public Color getBlockColorSourceCircle(float efficiency, boolean selfPriming){
        return cracker.getBlockColorSourceCircle(efficiency, selfPriming);
    }
    @Override
    public Color getClusterOverheatingColor(){
        return cracker.getClusterOverheatingColor();
    }
    @Override
    public Color getClusterOvercoolingColor(){
        return cracker.getClusterOvercoolingColor();
    }
    @Override
    public Color getClusterDisconnectedColor(){
        return cracker.getClusterDisconnectedColor();
    }
    @Override
    public Color getClusterInvalidColor(){
        return cracker.getClusterInvalidColor();
    }
    @Override
    public Color getTooltipInvalidTextColor(){
        return chocolate.getTooltipInvalidTextColor();
    }
    @Override
    public Color getTooltipTextColor(){
        return chocolate.getTooltipTextColor();
    }
    @Override
    public Color getEditorToolTextColor(int index){
        return pickTheme(index).getEditorToolTextColor(index);
    }
    @Override
    public Color getEditorToolBackgroundColor(int index){
        return pickTheme(index).getEditorToolBackgroundColor(index);
    }
    @Override
    public Color getSelectionColor(){
        return cracker.getSelectionColor();
    }
    @Override
    public Color getEditorBackgroundColor(){
        return marshmallow.getEditorBackgroundColor();
    }
    @Override
    public Color getImageExportBackgroundColor(){
        return cracker.getImageExportBackgroundColor();
    }
    @Override
    public Color getImageExportTextColor(){
        return chocolate.getImageExportTextColor();
    }
    @Override
    public Color getComponentTextColor(int index){
        return pickTheme(index).getComponentTextColor(index);
    }
    @Override
    public Color getMouseoverSelectedComponentColor(int index){
        return pickTheme(index).getMouseoverSelectedComponentColor(index);
    }
    @Override
    public Color getSelectedComponentColor(int index){
        return pickTheme(index).getSelectedComponentColor(index);
    }
    @Override
    public Color getMouseoverComponentColor(int index){
        return pickTheme(index).getMouseoverComponentColor(index);
    }
    @Override
    public Color getComponentColor(int index){
        return pickTheme(index).getComponentColor(index);
    }
    @Override
    public Color getEditorBackgroundMouseoverColor(){
        return cracker.getEditorBackgroundMouseoverColor();
    }
    @Override
    public Color getEditorGridColor(){
        return cracker.getEditorGridColor();
    }
    @Override
    public Color getSuggestionOutlineColor(){
        return cracker.getSuggestionOutlineColor();
    }
    @Override
    public Color getEditorMouseoverLightColor(){
        return cracker.getEditorMouseoverLightColor();
    }
    @Override
    public Color getEditorMouseoverDarkColor(){
        return cracker.getEditorMouseoverDarkColor();
    }
    @Override
    public Color getEditorMouseoverLineColor(){
        return cracker.getEditorMouseoverLineColor();
    }
    @Override
    public Color getEditorListBackgroundMouseoverColor(int index){
        return pickTheme(index).getEditorListBackgroundMouseoverColor(index);
    }
    @Override
    public Color getEditorListBackgroundColor(int index){
        return pickTheme(index).getEditorListBackgroundColor(index);
    }
    @Override
    public Color getEditorListLightSelectedColor(int index){
        return pickTheme(index).getEditorListLightSelectedColor(index);
    }
    @Override
    public Color getEditorListDarkSelectedColor(int index){
        return pickTheme(index).getEditorListDarkSelectedColor(index);
    }
    @Override
    public Color getEditorListLightMouseoverColor(int index){
        return pickTheme(index).getEditorListLightMouseoverColor(index);
    }
    @Override
    public Color getEditorListDarkMouseoverColor(int index){
        return pickTheme(index).getEditorListDarkMouseoverColor(index);
    }
    @Override
    public Color getMultiblockSelectedInputColor(){
        return cracker.getMultiblockSelectedInputColor();
    }
    @Override
    public Color getMultiblockInvalidInputColor(){
        return cracker.getMultiblockInvalidInputColor();
    }
    @Override
    public Color getSecondaryComponentColor(int index){
        return pickTheme(index).getSecondaryComponentColor(index);
    }
    @Override
    public Color getProgressBarBackgroundColor(){
        return cracker.getProgressBarBackgroundColor();
    }
    @Override
    public Color getProgressBarColor(){
        return cracker.getProgressBarColor();
    }
    @Override
    public Color getMultiblockDisplayBorderColor(){
        return cracker.getMultiblockDisplayBorderColor();
    }
    @Override
    public Color getMultiblockDisplayBackgroundColor(){
        return cracker.getMultiblockDisplayBackgroundColor();
    }
    @Override
    public Color getToggleBlockFadeout(int index){
        return pickTheme(index).getToggleBlockFadeout(index);
    }
    @Override
    public Color getTutorialBackgroundColor(){
        return cracker.getTutorialBackgroundColor();
    }
    @Override
    public Color getScrollbarButtonColor(){
        return cracker.getScrollbarButtonColor();
    }
    @Override
    public Color getBlockUnknownColor(){
        return cracker.getBlockUnknownColor();
    }
    @Override
    public Color getBlockTextColor(){
        return chocolate.getBlockTextColor();
    }
    @Override
    public Color getScrollbarBackgroundColor(){
        return cracker.getScrollbarBackgroundColor();
    }
    @Override
    public Color getComponentPressedColor(int index){
        return pickTheme(index).getComponentPressedColor(index);
    }
    @Override
    public Color getComponentMouseoverColor(int index){
        return pickTheme(index).getComponentMouseoverColor(index);
    }
    @Override
    public Color getComponentDisabledColor(int index){
        return pickTheme(index).getComponentDisabledColor(index);
    }
    @Override
    public Color getSecondaryComponentPressedColor(int index){
        return pickTheme(index).getSecondaryComponentPressedColor(index);
    }
    @Override
    public Color getSecondaryComponentMouseoverColor(int index){
        return pickTheme(index).getSecondaryComponentMouseoverColor(index);
    }
    @Override
    public Color getSecondaryComponentDisabledColor(int index){
        return pickTheme(index).getSecondaryComponentDisabledColor(index);
    }
    @Override
    public Color getSliderColor(){
        return cracker.getSliderColor();
    }
    @Override
    public Color getSliderPressedColor(){
        return cracker.getSliderPressedColor();
    }
    @Override
    public Color getSliderMouseoverColor(){
        return cracker.getSliderMouseoverColor();
    }
    @Override
    public Color getSliderDisabledColor(){
        return cracker.getSliderDisabledColor();
    }
    @Override
    public Color getSecondarySliderColor(){
        return cracker.getSecondarySliderColor();
    }
    @Override
    public Color getSecondarySliderPressedColor(){
        return cracker.getSecondarySliderPressedColor();
    }
    @Override
    public Color getSecondarySliderMouseoverColor(){
        return cracker.getSecondarySliderMouseoverColor();
    }
    @Override
    public Color getSecondarySliderDisabledColor(){
        return cracker.getSecondarySliderDisabledColor();
    }
    @Override
    public Color getTextBoxBorderColor(){
        return cracker.getTextBoxBorderColor();
    }
    @Override
    public Color getTextBoxColor(){
        return cracker.getTextBoxColor();
    }
    @Override
    public Color getTextViewBackgroundColor(){
        return cracker.getTextViewBackgroundColor();
    }
    @Override
    public Color getToggleBoxBorderColor(int index){
        return pickTheme(index).getToggleBoxBorderColor(index);
    }
    @Override
    public Color getSecondaryToggleBoxBorderColor(int index){
        return pickTheme(index).getSecondaryToggleBoxBorderColor(index);
    }
    @Override
    public Color getToggleBoxBackgroundColor(int index){
        return pickTheme(index).getToggleBoxBackgroundColor(index);
    }
    @Override
    public Color getToggleBoxSelectedColor(int index){
        return pickTheme(index).getToggleBoxSelectedColor(index);
    }
    @Override
    public Color getToggleBoxMouseoverColor(int index){
        return pickTheme(index).getToggleBoxMouseoverColor(index);
    }
    @Override
    public Color getMouseoverUnselectableComponentColor(int index){
        return pickTheme(index).getMouseoverUnselectableComponentColor(index);
    }
    @Override
    public Color getConfigurationSidebarColor(){
        return cracker.getConfigurationSidebarColor();
    }
    @Override
    public Color getConfigurationWarningTextColor(){
        return chocolate.getConfigurationWarningTextColor();
    }
    @Override
    public Color getConfigurationDividerColor(){
        return cracker.getConfigurationDividerColor();
    }
    @Override
    public Color getDialogBorderColor(){
        return cracker.getDialogBorderColor();
    }
    @Override
    public Color getDialogBackgroundColor(){
        return cracker.getDialogBackgroundColor();
    }
    @Override
    public Color getCreditsImageColor(){
        return cracker.getCreditsImageColor();
    }
    @Override
    public Color getCreditsBrightImageColor(){
        return cracker.getCreditsBrightImageColor();
    }
    @Override
    public Color getCreditsTextColor(){
        return chocolate.getCreditsTextColor();
    }
    @Override
    public Color get3DMultiblockOutlineColor(){
        return cracker.get3DMultiblockOutlineColor();
    }
    @Override
    public Color get3DDeviceoverOutlineColor(){
        return cracker.get3DDeviceoverOutlineColor();
    }
    @Override
    public Color getAddButtonTextColor(){
        return chocolate.getAddButtonTextColor();
    }
    @Override
    public Color getDeleteButtonTextColor(){
        return chocolate.getDeleteButtonTextColor();
    }
    @Override
    public Color getConvertButtonTextColor(){
        return chocolate.getConvertButtonTextColor();
    }
    @Override
    public Color getInputsButtonTextColor(){
        return chocolate.getInputsButtonTextColor();
    }
    @Override
    public Color getMetadataPanelBackgroundColor(){
        return cracker.getMetadataPanelBackgroundColor();
    }
    @Override
    public Color getMetadataPanelHeaderColor(){
        return cracker.getMetadataPanelHeaderColor();
    }
    @Override
    public Color getMetadataPanelTextColor(){
        return chocolate.getMetadataPanelTextColor();
    }
    @Override
    public Color getMultiblocksListHeaderColor(){
        return cracker.getMultiblocksListHeaderColor();
    }
    @Override
    public Color getRecoveryModeColor(int index){
        return pickTheme(index).getComponentColor(0);
    }
    @Override
    public Color getRecoveryModeTextColor(){
        return chocolate.getRecoveryModeTextColor();
    }
    @Override
    public Color getRotateMultiblockTextColor(){
        return chocolate.getRotateMultiblockTextColor();
    }
    @Override
    public Color getResizeMenuTextColor(){
        return chocolate.getResizeMenuTextColor();
    }
    @Override
    public Color getMenuBackgroundColor(){
        return chocolate.getMenuBackgroundColor();
    }
    @Override
    public Color getSettingsSidebarColor(){
        return chocolate.getSettingsSidebarColor();
    }
    @Override
    public Color getWhiteColor(){
        return marshmallow.getWhiteColor();
    }
    @Override
    public Color getTutorialTextColor(){
        return chocolate.getTutorialTextColor();
    }
    @Override
    public Color getVRComponentColor(int index){
        return pickTheme(index).getVRComponentColor(index);
    }
    @Override
    public Color getVRDeviceoverComponentColor(int index){
        return pickTheme(index).getVRDeviceoverComponentColor(index);
    }
    @Override
    public Color getVRSelectedOutlineColor(int index){
        return pickTheme(index).getVRSelectedOutlineColor(index);
    }
    @Override
    public Color getVRPanelOutlineColor(){
        return cracker.getVRPanelOutlineColor();
    }
    @Override
    public Color getVRMultitoolTextColor(){
        return chocolate.getVRMultitoolTextColor();
    }
    @Override
    public void drawThemeButtonBackground(double x, double y, double width, double height, boolean darker, boolean enabled, boolean pressed, boolean mouseOver){
        for(int i = 0; i<4; i++){
            Color col;
            if(darker){
                 col = getSecondaryComponentColor(i);
                if(enabled){
                    if(pressed)col = getSecondaryComponentPressedColor(i);
                    else if(mouseOver)col = getSecondaryComponentMouseoverColor(i);
                }else{
                    col = getSecondaryComponentDisabledColor(i);
                }
            }else{
                col = getComponentColor(i);
                if(enabled){
                    if(pressed)col = getComponentPressedColor(i);
                    else if(mouseOver)col = getComponentMouseoverColor(i);
                }else{
                    col = getComponentDisabledColor(i);
                }
            }
            Core.applyColor(col);
            drawRect(x, y+height*i/4d, x+width, y+height*(i+1)/4d, 0);
        }
    }
    @Override
    public void drawThemeButtonText(double x, double y, double width, double height, double textHeight, String text){
        for(int i = 0; i<4; i++){
            Core.applyColor(getComponentTextColor(i));
            drawCenteredTextWithBounds(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, x, y+height*i/4d, x+width, y+height*(i+1)/4d, text);
        }
    }
    private ColorTheme pickTheme(int index){
        if(index%3==0)return cracker;
        if(index%3==1)return chocolate;
        if(index%3==2)return marshmallow;
        return cracker;
    }
    @Override
    public boolean shouldContantlyUpdateBackground(){
        return chocolate.shouldContantlyUpdateBackground();
    }
    @Override
    public Color getSettingsMergeTextColor(){
        return marshmallow.getSettingsMergeTextColor();
    }
}