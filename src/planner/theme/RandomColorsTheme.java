package planner.theme;
import java.util.HashMap;
import java.util.Random;
import simplelibrary.image.Color;
public class RandomColorsTheme extends ColorTheme{
    private Random rand = new Random();
    private Color keywordBackgroundColor;
    private Color keywordColorBlind;
    private Color keywordColorUnderhaul;
    private Color keywordColorSymmetry;
    private Color keywordColorPriority;
    private Color keywordColorOverhaul;
    private Color keywordColorMultiblock;
    private Color keywordColorFuel;
    private Color keywordColorFormat;
    private Color keywordColorCuboid;
    private Color keywordColorCube;
    private Color keywordColorConfiguration;
    private Color keywordColorBlockRange;
    private Color decalColorAdjacentCell;
    private Color decalColorAdjacentModerator;
    private HashMap<Float, Color> decalColorAdjacentModeratorLine = new HashMap<>();
    private Color decalColorUnderhaulModeratorLine;
    private Color decalColorReflectorAdjacentModeratorLine;
    private HashMap<Float, Color> decalColorOverhaulModeratorLine = new HashMap<>();
    private Color decalTextColor;
    private Color decalColorNeutronSourceTarget;
    private Color decalColorNeutronSourceNoTarget;
    private Color decalColorNeutronSourceLine;
    private Color decalColorNeutronSource;
    private Color decalColorModeratorActive;
    private Color decalColorMissingCasing;
    private Color decalColorMissingBlade;
    private Color decalColorIrradiatorAdjacentModeratorLine;
    private HashMap<Integer, HashMap<Integer, Color>> decalColorCellFlux = new HashMap<>();
    private Color decalColorBlockValid;
    private Color decalColorBlockInvalid;
    private Color blockColorOutlineInvalid;
    private Color blockColorOutlineActive;
    private HashMap<Float, HashMap<Boolean, Color>> blockColorSourceCircle = new HashMap<>();
    private Color clusterOverheatingColor;
    private Color clusterOvercoolingColor;
    private Color clusterDisconnectedColor;
    private Color clusterInvalidColor;
    private Color tooltipInvalidTextColor;
    private Color tooltipTextColor;
    private Color editorToolTextColor;
    private Color editorToolBackgroundColor;
    private Color selectionColor;
    private Color editorBackgroundColor;
    private Color imageExportBackgroundColor;
    private Color imageExportTextColor;
    private Color componentTextColor;
    private Color mouseoverSelectedComponentColor;
    private Color selectedComponentColor;
    private Color mouseoverComponentColor;
    private Color componentColor;
    private Color editorBackgroundMouseoverColor;
    private Color editorGridColor;
    private Color suggestionOutlineColor;
    private Color editorMouseoverLightColor;
    private Color editorMouseoverDarkColor;
    private Color editorMouseoverLineColor;
    private Color editorListBackgroundMouseoverColor;
    private Color editorListBackgroundColor;
    private Color editorListLightSelectedColor;
    private Color editorListDarkSelectedColor;
    private Color editorListLightMouseoverColor;
    private Color editorListDarkMouseoverColor;
    private Color multiblockSelectedInputColor;
    private Color multiblockInvalidInputColor;
    private Color secondaryComponentColor;
    private Color progressBarBackgroundColor;
    private Color progressBarColor;
    private Color multiblockDisplayBorderColor;
    private Color multiblockDisplayBackgroundColor;
    private Color toggleBlockFadeout;
    private Color tutorialBackgroundColor;
    private Color scrollbarButtonColor;
    private Color blockUnknownColor;
    private Color blockTextColor;
    private Color scrollbarBackgroundColor;
    private Color componentPressedColor;
    private Color componentMouseoverColor;
    private Color componentDisabledColor;
    private Color secondaryComponentPressedColor;
    private Color secondaryComponentMouseoverColor;
    private Color secondaryComponentDisabledColor;
    private Color sliderColor;
    private Color sliderPressedColor;
    private Color sliderMouseoverColor;
    private Color sliderDisabledColor;
    private Color secondarySliderColor;
    private Color secondarySliderPressedColor;
    private Color secondarySliderMouseoverColor;
    private Color secondarySliderDisabledColor;
    private Color textBoxBorderColor;
    private Color textBoxColor;
    private Color textViewBackgroundColor;
    private Color toggleBoxBorderColor;
    private Color secondaryToggleBoxBorderColor;
    private Color toggleBoxBackgroundColor;
    private Color toggleBoxSelectedColor;
    private Color toggleBoxMouseoverColor;
    private Color mouseoverUnselectableComponentColor;
    private Color configurationSidebarColor;
    private Color configurationWarningTextColor;
    private Color configurationDividerColor;
    private Color dialogBorderColor;
    private Color dialogBackgroundColor;
    private Color creditsImageColor;
    private Color creditsBrightImageColor;
    private Color creditsTextColor;
    private Color multiblockOutlineColor;
    private Color deviceoverOutlineColor;
    private Color addButtonTextColor;
    private Color deleteButtonTextColor;
    private Color convertButtonTextColor;
    private Color inputsButtonTextColor;
    private Color metadataPanelBackgroundColor;
    private Color metadataPanelHeaderColor;
    private Color metadataPanelTextColor;
    private Color multiblocksListHeaderColor;
    private HashMap<Integer, Color> recoveryModeColor = new HashMap<>();
    private Color recoveryModeTextColor;
    private Color rotateMultiblockTextColor;
    private Color resizeMenuTextColor;
    private Color menuBackgroundColor;
    private Color settingsSidebarColor;
    private Color whiteColor;
    private Color tutorialTextColor;
    private Color vrComponentColor;
    private Color vrDeviceoverComponentColor;
    private Color vrSelectedOutlineColor;
    private Color vrPanelOutlineColor;
    private Color vrMultitoolTextColor;
    private Color settingsMergeTextColor;
    public RandomColorsTheme(String name){
        super(name);
    }
    @Override
    public Color getKeywordBackgroundColor(){
        return keywordBackgroundColor = gen(keywordBackgroundColor);
    }
    @Override
    public Color getKeywordColorBlind(){
        return keywordColorBlind = gen(keywordColorBlind);
    }
    @Override
    public Color getKeywordColorUnderhaul(){
        return keywordColorUnderhaul = gen(keywordColorUnderhaul);
    }
    @Override
    public Color getKeywordColorSymmetry(){
        return keywordColorSymmetry = gen(keywordColorSymmetry);
    }
    @Override
    public Color getKeywordColorPriority(){
        return keywordColorPriority = gen(keywordColorPriority);
    }
    @Override
    public Color getKeywordColorOverhaul(){
        return keywordColorOverhaul = gen(keywordColorOverhaul);
    }
    @Override
    public Color getKeywordColorMultiblock(){
        return keywordColorMultiblock = gen(keywordColorMultiblock);
    }
    @Override
    public Color getKeywordColorFuel(){
        return keywordColorFuel = gen(keywordColorFuel);
    }
    @Override
    public Color getKeywordColorFormat(){
        return keywordColorFormat = gen(keywordColorFormat);
    }
    @Override
    public Color getKeywordColorCuboid(){
        return keywordColorCuboid = gen(keywordColorCuboid);
    }
    @Override
    public Color getKeywordColorCube(){
        return keywordColorCube = gen(keywordColorCube);
    }
    @Override
    public Color getKeywordColorConfiguration(){
        return keywordColorConfiguration = gen(keywordColorConfiguration);
    }
    @Override
    public Color getKeywordColorBlockRange(){
        return keywordColorBlockRange = gen(keywordColorBlockRange);
    }
    @Override
    public Color getDecalColorAdjacentCell(){
        return decalColorAdjacentCell = gen(decalColorAdjacentCell);
    }
    @Override
    public Color getDecalColorAdjacentModerator(){
        return decalColorAdjacentModerator = gen(decalColorAdjacentModerator);
    }
    @Override
    public Color getDecalColorAdjacentModeratorLine(float efficiency){
        if(decalColorAdjacentModeratorLine.containsKey(efficiency))return decalColorAdjacentModeratorLine.get(efficiency);
        Color c = gen(null);
        decalColorAdjacentModeratorLine.put(efficiency, c);
        return c;
    }
    @Override
    public Color getDecalColorUnderhaulModeratorLine(){
        return decalColorUnderhaulModeratorLine = gen(decalColorUnderhaulModeratorLine);
    }
    @Override
    public Color getDecalColorReflectorAdjacentModeratorLine(){
        return decalColorReflectorAdjacentModeratorLine = gen(decalColorReflectorAdjacentModeratorLine);
    }
    @Override
    public Color getDecalColorOverhaulModeratorLine(float efficiency){
        if(decalColorOverhaulModeratorLine.containsKey(efficiency))return decalColorOverhaulModeratorLine.get(efficiency);
        Color c = gen(null);
        decalColorOverhaulModeratorLine.put(efficiency, c);
        return c;
    }
    @Override
    public Color getDecalTextColor(){
        return decalTextColor = gen(decalTextColor);
    }
    @Override
    public Color getDecalColorNeutronSourceTarget(){
        return decalColorNeutronSourceTarget = gen(decalColorNeutronSourceTarget);
    }
    @Override
    public Color getDecalColorNeutronSourceNoTarget(){
        return decalColorNeutronSourceNoTarget = gen(decalColorNeutronSourceNoTarget);
    }
    @Override
    public Color getDecalColorNeutronSourceLine(){
        return decalColorNeutronSourceLine = gen(decalColorNeutronSourceLine);
    }
    @Override
    public Color getDecalColorNeutronSource(){
        return decalColorNeutronSource = gen(decalColorNeutronSource);
    }
    @Override
    public Color getDecalColorModeratorActive(){
        return decalColorModeratorActive = gen(decalColorModeratorActive);
    }
    @Override
    public Color getDecalColorMissingCasing(){
        return decalColorMissingCasing = gen(decalColorMissingCasing);
    }
    @Override
    public Color getDecalColorMissingBlade(){
        return decalColorMissingBlade = gen(decalColorMissingBlade);
    }
    @Override
    public Color getDecalColorIrradiatorAdjacentModeratorLine(){
        return decalColorIrradiatorAdjacentModeratorLine = gen(decalColorIrradiatorAdjacentModeratorLine);
    }
    @Override
    public Color getDecalColorCellFlux(int flux, int criticality){
        if(decalColorCellFlux.containsKey(flux)){
            HashMap<Integer, Color> critMap = decalColorCellFlux.get(flux);
            if(critMap.containsKey(criticality))return critMap.get(criticality);
            Color c = gen(null);
            critMap.put(criticality, c);
            return c;
        }
        Color c = gen(null);
        HashMap<Integer, Color> critMap = new HashMap<>();
        critMap.put(criticality, c);
        decalColorCellFlux.put(flux, critMap);
        return c;
    }
    @Override
    public Color getDecalColorBlockValid(){
        return decalColorBlockValid = gen(decalColorBlockValid);
    }
    @Override
    public Color getDecalColorBlockInvalid(){
        return decalColorBlockInvalid = gen(decalColorBlockInvalid);
    }
    @Override
    public Color getBlockColorOutlineInvalid(){
        return blockColorOutlineInvalid = gen(blockColorOutlineInvalid);
    }
    @Override
    public Color getBlockColorOutlineActive(){
        return blockColorOutlineActive = gen(blockColorOutlineActive);
    }
    @Override
    public Color getBlockColorSourceCircle(float efficiency, boolean selfPriming){
        if(blockColorSourceCircle.containsKey(efficiency)){
            HashMap<Boolean, Color> critMap = blockColorSourceCircle.get(efficiency);
            if(critMap.containsKey(selfPriming))return critMap.get(selfPriming);
            Color c = gen(null);
            critMap.put(selfPriming, c);
            return c;
        }
        Color c = gen(null);
        HashMap<Boolean, Color> critMap = new HashMap<>();
        critMap.put(selfPriming, c);
        blockColorSourceCircle.put(efficiency, critMap);
        return c;
    }
    @Override
    public Color getClusterOverheatingColor(){
        return clusterOverheatingColor = gen(clusterOverheatingColor);
    }
    @Override
    public Color getClusterOvercoolingColor(){
        return clusterOvercoolingColor = gen(clusterOvercoolingColor);
    }
    @Override
    public Color getClusterDisconnectedColor(){
        return clusterDisconnectedColor = gen(clusterDisconnectedColor);
    }
    @Override
    public Color getClusterInvalidColor(){
        return clusterInvalidColor = gen(clusterInvalidColor);
    }
    @Override
    public Color getTooltipInvalidTextColor(){
        return tooltipInvalidTextColor = gen(tooltipInvalidTextColor);
    }
    @Override
    public Color getTooltipTextColor(){
        return tooltipTextColor = gen(tooltipTextColor);
    }
    @Override
    public Color getEditorToolTextColor(int index){
        return editorToolTextColor = gen(editorToolTextColor);
    }
    @Override
    public Color getEditorToolBackgroundColor(int index){
        return editorToolBackgroundColor = gen(editorToolBackgroundColor);
    }
    @Override
    public Color getSelectionColor(){
        return selectionColor = gen(selectionColor);
    }
    @Override
    public Color getEditorBackgroundColor(){
        return editorBackgroundColor = gen(editorBackgroundColor);
    }
    @Override
    public Color getImageExportBackgroundColor(){
        return imageExportBackgroundColor = gen(imageExportBackgroundColor);
    }
    @Override
    public Color getImageExportTextColor(){
        return imageExportTextColor = gen(imageExportTextColor);
    }
    @Override
    public Color getComponentTextColor(int index){
        return componentTextColor = gen(componentTextColor);
    }
    @Override
    public Color getMouseoverSelectedComponentColor(int index){
        return mouseoverSelectedComponentColor = gen(mouseoverSelectedComponentColor);
    }
    @Override
    public Color getSelectedComponentColor(int index){
        return selectedComponentColor = gen(selectedComponentColor);
    }
    @Override
    public Color getMouseoverComponentColor(int index){
        return mouseoverComponentColor = gen(mouseoverComponentColor);
    }
    @Override
    public Color getComponentColor(int index){
        return componentColor = gen(componentColor);
    }
    @Override
    public Color getEditorBackgroundMouseoverColor(){
        return editorBackgroundMouseoverColor = gen(editorBackgroundMouseoverColor);
    }
    @Override
    public Color getEditorGridColor(){
        return editorGridColor = gen(editorGridColor);
    }
    @Override
    public Color getSuggestionOutlineColor(){
        return suggestionOutlineColor = gen(suggestionOutlineColor);
    }
    @Override
    public Color getEditorMouseoverLightColor(){
        return editorMouseoverLightColor = gen(editorMouseoverLightColor);
    }
    @Override
    public Color getEditorMouseoverDarkColor(){
        return editorMouseoverDarkColor = gen(editorMouseoverDarkColor);
    }
    @Override
    public Color getEditorMouseoverLineColor(){
        return editorMouseoverLineColor = gen(editorMouseoverLineColor);
    }
    @Override
    public Color getEditorListBackgroundMouseoverColor(int index){
        return editorListBackgroundMouseoverColor = gen(editorListBackgroundMouseoverColor);
    }
    @Override
    public Color getEditorListBackgroundColor(int index){
        return editorListBackgroundColor = gen(editorListBackgroundColor);
    }
    @Override
    public Color getEditorListLightSelectedColor(int index){
        return editorListLightSelectedColor = gen(editorListLightSelectedColor);
    }
    @Override
    public Color getEditorListDarkSelectedColor(int index){
        return editorListDarkSelectedColor = gen(editorListDarkSelectedColor);
    }
    @Override
    public Color getEditorListLightMouseoverColor(int index){
        return editorListLightMouseoverColor = gen(editorListLightMouseoverColor);
    }
    @Override
    public Color getEditorListDarkMouseoverColor(int index){
        return editorListDarkMouseoverColor = gen(editorListDarkMouseoverColor);
    }
    @Override
    public Color getMultiblockSelectedInputColor(){
        return multiblockSelectedInputColor = gen(multiblockSelectedInputColor);
    }
    @Override
    public Color getMultiblockInvalidInputColor(){
        return multiblockInvalidInputColor = gen(multiblockInvalidInputColor);
    }
    @Override
    public Color getSecondaryComponentColor(int index){
        return secondaryComponentColor = gen(secondaryComponentColor);
    }
    @Override
    public Color getProgressBarBackgroundColor(){
        return progressBarBackgroundColor = gen(progressBarBackgroundColor);
    }
    @Override
    public Color getProgressBarColor(){
        return progressBarColor = gen(progressBarColor);
    }
    @Override
    public Color getMultiblockDisplayBorderColor(){
        return multiblockDisplayBorderColor = gen(multiblockDisplayBorderColor);
    }
    @Override
    public Color getMultiblockDisplayBackgroundColor(){
        return multiblockDisplayBackgroundColor = gen(multiblockDisplayBackgroundColor);
    }
    @Override
    public Color getToggleBlockFadeout(int index){
        return toggleBlockFadeout = gen(toggleBlockFadeout);
    }
    @Override
    public Color getTutorialBackgroundColor(){
        return tutorialBackgroundColor = gen(tutorialBackgroundColor);
    }
    @Override
    public Color getScrollbarButtonColor(){
        return scrollbarButtonColor = gen(scrollbarButtonColor);
    }
    @Override
    public Color getBlockUnknownColor(){
        return blockUnknownColor = gen(blockUnknownColor);
    }
    @Override
    public Color getBlockTextColor(){
        return blockTextColor = gen(blockTextColor);
    }
    @Override
    public Color getScrollbarBackgroundColor(){
        return scrollbarBackgroundColor = gen(scrollbarBackgroundColor);
    }
    @Override
    public Color getComponentPressedColor(int index){
        return componentPressedColor = gen(componentPressedColor);
    }
    @Override
    public Color getComponentMouseoverColor(int index){
        return componentMouseoverColor = gen(componentMouseoverColor);
    }
    @Override
    public Color getComponentDisabledColor(int index){
        return componentDisabledColor = gen(componentDisabledColor);
    }
    @Override
    public Color getSecondaryComponentPressedColor(int index){
        return secondaryComponentPressedColor = gen(secondaryComponentPressedColor);
    }
    @Override
    public Color getSecondaryComponentMouseoverColor(int index){
        return secondaryComponentMouseoverColor = gen(secondaryComponentMouseoverColor);
    }
    @Override
    public Color getSecondaryComponentDisabledColor(int index){
        return secondaryComponentDisabledColor = gen(secondaryComponentDisabledColor);
    }
    @Override
    public Color getSliderColor(){
        return sliderColor = gen(sliderColor);
    }
    @Override
    public Color getSliderPressedColor(){
        return sliderPressedColor = gen(sliderPressedColor);
    }
    @Override
    public Color getSliderMouseoverColor(){
        return sliderMouseoverColor = gen(sliderMouseoverColor);
    }
    @Override
    public Color getSliderDisabledColor(){
        return sliderDisabledColor = gen(sliderDisabledColor);
    }
    @Override
    public Color getSecondarySliderColor(){
        return secondarySliderColor = gen(secondarySliderColor);
    }
    @Override
    public Color getSecondarySliderPressedColor(){
        return secondarySliderPressedColor = gen(secondarySliderPressedColor);
    }
    @Override
    public Color getSecondarySliderMouseoverColor(){
        return secondarySliderMouseoverColor = gen(secondarySliderMouseoverColor);
    }
    @Override
    public Color getSecondarySliderDisabledColor(){
        return secondarySliderDisabledColor = gen(secondarySliderDisabledColor);
    }
    @Override
    public Color getTextBoxBorderColor(){
        return textBoxBorderColor = gen(textBoxBorderColor);
    }
    @Override
    public Color getTextBoxColor(){
        return textBoxColor = gen(textBoxColor);
    }
    @Override
    public Color getTextViewBackgroundColor(){
        return textViewBackgroundColor = gen(textViewBackgroundColor);
    }
    @Override
    public Color getToggleBoxBorderColor(int index){
        return toggleBoxBorderColor = gen(toggleBoxBorderColor);
    }
    @Override
    public Color getSecondaryToggleBoxBorderColor(int index){
        return secondaryToggleBoxBorderColor = gen(secondaryToggleBoxBorderColor);
    }
    @Override
    public Color getToggleBoxBackgroundColor(int index){
        return toggleBoxBackgroundColor = gen(toggleBoxBackgroundColor);
    }
    @Override
    public Color getToggleBoxSelectedColor(int index){
        return toggleBoxSelectedColor = gen(toggleBoxSelectedColor);
    }
    @Override
    public Color getToggleBoxMouseoverColor(int index){
        return toggleBoxMouseoverColor = gen(toggleBoxMouseoverColor);
    }
    @Override
    public Color getMouseoverUnselectableComponentColor(int index){
        return mouseoverUnselectableComponentColor = gen(mouseoverUnselectableComponentColor);
    }
    @Override
    public Color getConfigurationSidebarColor(){
        return configurationSidebarColor = gen(configurationSidebarColor);
    }
    @Override
    public Color getConfigurationWarningTextColor(){
        return configurationWarningTextColor = gen(configurationWarningTextColor);
    }
    @Override
    public Color getConfigurationDividerColor(){
        return configurationDividerColor = gen(configurationDividerColor);
    }
    @Override
    public Color getDialogBorderColor(){
        return dialogBorderColor = gen(dialogBorderColor);
    }
    @Override
    public Color getDialogBackgroundColor(){
        return dialogBackgroundColor = gen(dialogBackgroundColor);
    }
    @Override
    public Color getCreditsImageColor(){
        return creditsImageColor = gen(creditsImageColor);
    }
    @Override
    public Color getCreditsBrightImageColor(){
        return creditsBrightImageColor = gen(creditsBrightImageColor);
    }
    @Override
    public Color getCreditsTextColor(){
        return creditsTextColor = gen(creditsTextColor);
    }
    @Override
    public Color get3DMultiblockOutlineColor(){
        return multiblockOutlineColor = gen(multiblockOutlineColor);
    }
    @Override
    public Color get3DDeviceoverOutlineColor(){
        return deviceoverOutlineColor = gen(deviceoverOutlineColor);
    }
    @Override
    public Color getAddButtonTextColor(){
        return addButtonTextColor = gen(addButtonTextColor);
    }
    @Override
    public Color getDeleteButtonTextColor(){
        return deleteButtonTextColor = gen(deleteButtonTextColor);
    }
    @Override
    public Color getConvertButtonTextColor(){
        return convertButtonTextColor = gen(convertButtonTextColor);
    }
    @Override
    public Color getInputsButtonTextColor(){
        return inputsButtonTextColor = gen(inputsButtonTextColor);
    }
    @Override
    public Color getMetadataPanelBackgroundColor(){
        return metadataPanelBackgroundColor = gen(metadataPanelBackgroundColor);
    }
    @Override
    public Color getMetadataPanelHeaderColor(){
        return metadataPanelHeaderColor = gen(metadataPanelHeaderColor);
    }
    @Override
    public Color getMetadataPanelTextColor(){
        return metadataPanelTextColor = gen(metadataPanelTextColor);
    }
    @Override
    public Color getMultiblocksListHeaderColor(){
        return multiblocksListHeaderColor = gen(multiblocksListHeaderColor);
    }
    @Override
    public Color getRecoveryModeColor(int index){
        if(recoveryModeColor.containsKey(index))return recoveryModeColor.get(index);
        Color c = gen(null);
        recoveryModeColor.put(index, c);
        return c;
    }
    @Override
    public Color getRecoveryModeTextColor(){
        return recoveryModeTextColor = gen(recoveryModeTextColor);
    }
    @Override
    public Color getRotateMultiblockTextColor(){
        return rotateMultiblockTextColor = gen(rotateMultiblockTextColor);
    }
    @Override
    public Color getResizeMenuTextColor(){
        return resizeMenuTextColor = gen(resizeMenuTextColor);
    }
    @Override
    public Color getMenuBackgroundColor(){
        return menuBackgroundColor = gen(menuBackgroundColor);
    }
    @Override
    public Color getSettingsSidebarColor(){
        return settingsSidebarColor = gen(settingsSidebarColor);
    }
    @Override
    public Color getWhiteColor(){
        return whiteColor = gen(whiteColor);
    }
    @Override
    public Color getTutorialTextColor(){
        return tutorialTextColor = gen(tutorialTextColor);
    }
    @Override
    public Color getVRComponentColor(int index){
        return vrComponentColor = gen(vrComponentColor);
    }
    @Override
    public Color getVRDeviceoverComponentColor(int index){
        return vrDeviceoverComponentColor = gen(vrDeviceoverComponentColor);
    }
    @Override
    public Color getVRSelectedOutlineColor(int index){
        return vrSelectedOutlineColor = gen(vrSelectedOutlineColor);
    }
    @Override
    public Color getVRPanelOutlineColor(){
        return vrPanelOutlineColor = gen(vrPanelOutlineColor);
    }
    @Override
    public Color getVRMultitoolTextColor(){
        return vrMultitoolTextColor = gen(vrMultitoolTextColor);
    }
    private Color gen(Color color){
        if(color!=null)return color;
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }
    @Override
    public Color getSettingsMergeTextColor(){
        return settingsMergeTextColor = gen(settingsMergeTextColor);
    }
}