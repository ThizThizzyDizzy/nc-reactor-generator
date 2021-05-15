package planner.theme;
import java.util.ArrayList;
import java.util.Random;
import planner.theme.legacy.LegacyTheme;
import planner.theme.legacy.SolidColorTheme;
import simplelibrary.image.Color;
public abstract class Theme{
    public static ArrayList<Theme> themes = new ArrayList<>();
    public static final SolidColorTheme LIGHT, GOLD, CRACKER, CHOCOLATE, MARSHMALLOW;
    static{
        themes.add(LIGHT = new SolidColorTheme("Light", new Color(100, 100, 100), new Color(1f, 1f, 1f, 1f), .625f, .75f));
        themes.add(new SolidColorTheme("Light, but darker", new Color(50, 50, 50), new Color(.5f, .5f, .5f, 1f), .3125f, .75f));
        themes.add(new SolidColorTheme("Water", new Color(64, 78, 203)));
        themes.add(new SolidColorTheme("Iron", new Color(229, 229, 229)));
        themes.add(new SolidColorTheme("Redstone", new Color(144, 16, 8)));
        themes.add(new SolidColorTheme("Quartz", new Color(166, 164, 160)));
        themes.add(new SolidColorTheme("Obsidian", new Color(20, 18, 30)));
        themes.add(new SolidColorTheme("Nether Brick", new Color(68, 4, 7)));
        themes.add(new SolidColorTheme("Glowstone", new Color(143, 117, 68)));
        themes.add(new SolidColorTheme("Lapis", new Color(37, 65, 139)));
        themes.add(GOLD = new SolidColorTheme("Gold", new Color(254, 249, 85)));
        themes.add(new SolidColorTheme("Prismarine", new Color(101, 162, 144)));
        themes.add(new SolidColorTheme("Slime", new Color(119, 187, 101)));
        themes.add(new SolidColorTheme("End Stone", new Color(225, 228, 170)));
        themes.add(new SolidColorTheme("Purpur", new Color(165, 121, 165)));
        themes.add(new SolidColorTheme("Diamond", new Color(136, 230, 226)));
        themes.add(new SolidColorTheme("Emerald", new Color(82, 221, 119)));
        themes.add(new SolidColorTheme("Copper", new Color(222, 151, 109)));
        themes.add(new SolidColorTheme("Tin", new Color(222, 225, 242)));
        themes.add(new SolidColorTheme("Lead", new Color(65, 77, 77)));
        themes.add(new SolidColorTheme("Boron", new Color(160, 160, 160)));
        themes.add(new SolidColorTheme("Lithium", new Color(241, 241, 241)));
        themes.add(new SolidColorTheme("Magnesium", new Color(242, 220, 229)));
        themes.add(new SolidColorTheme("Manganese", new Color(173, 176, 201)));
        themes.add(new SolidColorTheme("Aluminum", new Color(213, 245, 233)));
        themes.add(new SolidColorTheme("Silver", new Color(241, 238, 246)));
        themes.add(new SolidColorTheme("Fluorite", new Color(132, 160, 142)));
        themes.add(new SolidColorTheme("Villiaumite", new Color(154, 109, 97)));
        themes.add(new SolidColorTheme("Carobbiite", new Color(160, 167, 82)));
        themes.add(new SolidColorTheme("Arsenic", new Color(147, 149, 137)));
        themes.add(new SolidColorTheme("Nitrogen", new Color(64, 166, 70)));
        themes.add(new SolidColorTheme("Helium", new Color(201, 76, 73)));
        themes.add(new SolidColorTheme("Enderium", new Color(0, 71, 75)));
        themes.add(new SolidColorTheme("Cryotheum", new Color(0, 150, 194)));
        themes.add(new SolidColorTheme("Beryllium", new Color(240, 244, 236)));
        themes.add(new SolidColorTheme("Graphite", new Color(18, 18, 18)));
        themes.add(new SolidColorTheme("Heavy Water", new Color(103, 71, 210)));
        themes.add(new SolidColorTheme("Reflector", new Color(186, 144, 94)));
        themes.add(new SolidColorTheme("Conductor", new Color(129, 129, 129)));
        themes.add(new SolidColorTheme("OLD Heavy Water", new Color(40, 50, 100), new Color(0.5f, 0.5f, 1f, 1f), .625f, .875f, new Color(.875f,.875f,1f,1f)));
        themes.add(new LegacyTheme("Air"){
            @Override
            public Color getBackgroundColor(){
                return average(Color.WHITE, LIGHT.getBackgroundColor());
            }
            @Override
            public Color getTextColor(){
                return process(LIGHT.getTextColor());
            }
            @Override
            public Color getHeaderColor(){
                return process(LIGHT.getHeaderColor());
            }
            @Override
            public Color getHeader2Color(){
                return process(LIGHT.getHeader2Color());
            }
            @Override
            public Color getListColor(){
                return process(LIGHT.getListColor());
            }
            @Override
            public Color getSelectedMultiblockColor(){
                return process(LIGHT.getSelectedMultiblockColor());
            }
            @Override
            public Color getListBackgroundColor(){
                return process(LIGHT.getListBackgroundColor());
            }
            @Override
            public Color getMetadataPanelBackgroundColor(){
                return process(LIGHT.getMetadataPanelBackgroundColor());
            }
            @Override
            public Color getMetadataPanelHeaderColor(){
                return process(LIGHT.getMetadataPanelHeaderColor());
            }
            @Override
            public Color getRed(){
                return process(LIGHT.getRed());
            }
            @Override
            public Color getGreen(){
                return process(LIGHT.getGreen());
            }
            @Override
            public Color getBlue(){
                return process(LIGHT.getBlue());
            }
            @Override
            public Color getEditorListBorderColor(){
                return process(LIGHT.getEditorListBorderColor());
            }
            @Override
            public Color getDarkButtonColor(){
                return process(LIGHT.getDarkButtonColor());
            }
            @Override
            public Color getButtonColor(){
                return process(LIGHT.getButtonColor());
            }
            @Override
            public Color getSelectionColor(){
                return process(LIGHT.getSelectionColor());
            }
            @Override
            public Color getRGBA(float r, float g, float b, float a){
                return process(LIGHT.getRGBA(r,g,b, a));
            }
            @Override
            public Color getWhite(){
                return process(LIGHT.getWhite());
            }
            private Color process(Color c){
                return new Color(c.getRed(), c.getBlue(), c.getGreen(), c.getAlpha()/16);
            }
            @Override
            public Color getSidebarColor(){
                return process(LIGHT.getSidebarColor());
            }
            @Override
            public Color getFadeout(){
                return process(LIGHT.getFadeout());
            }
        });
        themes.add(new ChangingTheme("Random", () -> {
            Random rand = new Random();
            return new SolidColorTheme("Random", new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
        }));
        themes.add(CRACKER = new SolidColorTheme("Cracker", new Color(201, 163, 64)));//or 150 106 1
        themes.add(CHOCOLATE = new SolidColorTheme("Chocolate", new Color(97, 20, 0)));
        themes.add(MARSHMALLOW = new SolidColorTheme("Marshmallow", new Color(248, 248, 248)));
        themes.add(new SmoreTheme("S'more", CRACKER, CHOCOLATE, MARSHMALLOW));
        themes.add(new SmoreTheme("Golden S'more", GOLD, CHOCOLATE, MARSHMALLOW));
        themes.add(new ChangingTheme("???", () -> {
            return new RandomColorsTheme("???");
        }));
        themes.add(new SiezureTheme("Disco"));
        themes.add(new SolidColorTheme("Red", new Color(100, 0, 0), new Color(1f, 0f, 0f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Orange", new Color(100, 50, 0), new Color(1, 0.5f, 0), .625f, 1f));
        themes.add(new SolidColorTheme("Yellow", new Color(100, 100, 0), new Color(1f, 1f, 0f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Lime", new Color(50, 100, 0), new Color(.5f, 1f, 0), .625f, 1f));
        themes.add(new SolidColorTheme("Green", new Color(0, 100, 0), new Color(0f, 1f, 0f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Bluish green", new Color(0, 100, 50), new Color(0f, 1f, .5f), .625f, 1f));
        themes.add(new SolidColorTheme("Aqua", new Color(0, 100, 100), new Color(0f, 1f, 1f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Greenish Blue", new Color(0, 50, 100), new Color(0f, .5f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Blue", new Color(0, 0, 100), new Color(0f, 0f, 1f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Purple", new Color(50, 0, 100), new Color(.5f, 0f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Magenta", new Color(100, 0, 100), new Color(1f, 0f, 1f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Pink", new Color(100, 0, 50), new Color(1f, 0f, .5f), .625f, 1f));
        themes.add(new SolidColorTheme("Light Pink", new Color(255, 110, 199)));
    }
    public static String[] getThemeS(){
        String[] themeS = new String[themes.size()];
        for(int i = 0; i<themes.size(); i++){
            themeS[i] = themes.get(i).name;
        }
        return themeS;
    }
    public final String name;
    public Theme(String name){
        this.name = name;
    }
    public void onSet(){}
    //pixelScale is the amount of a dimension that should be regarded as a "pixel"
    //always 1 for 2D GUIs, much smaller for VR
    public abstract void drawKeywordBackground(double x, double y, double width, double height, double pixelScale);
    public abstract Color getKeywordColorBlind();
    public abstract Color getKeywordColorUnderhaul();
    public abstract Color getKeywordColorSymmetry();
    public abstract Color getKeywordColorPriority();
    public abstract Color getKeywordColorOverhaul();
    public abstract Color getKeywordColorMultiblock();
    public abstract Color getKeywordColorFuel();
    public abstract Color getKeywordColorFormat();
    public abstract Color getKeywordColorCuboid();
    public abstract Color getKeywordColorCube();
    public abstract Color getKeywordColorConfiguration();
    public abstract Color getKeywordColorBlockRange();
    public abstract Color getDecalColorAdjacentCell();
    public abstract Color getDecalColorAdjacentModerator();
    public abstract Color getDecalColorAdjacentModeratorLine(float efficiency);
    public abstract Color getDecalColorUnderhaulModeratorLine();
    public abstract Color getDecalColorReflectorAdjacentModeratorLine();
    public abstract Color getDecalColorOverhaulModeratorLine(float efficiency);
    public abstract Color getDecalTextColor();
    public abstract Color getDecalColorNeutronSourceTarget();
    public abstract Color getDecalColorNeutronSourceNoTarget();
    public abstract Color getDecalColorNeutronSourceLine();
    public abstract Color getDecalColorNeutronSource();
    public abstract Color getDecalColorModeratorActive();
    public abstract Color getDecalColorMissingCasing();
    public abstract Color getDecalColorMissingBlade();
    public abstract Color getDecalColorIrradiatorAdjacentModeratorLine();
    public abstract Color getDecalColorCellFlux(int flux, int criticality);
    public abstract Color getDecalColorBlockValid();
    public abstract Color getDecalColorBlockInvalid();
    public abstract Color getBlockColorOutlineInvalid();
    public abstract Color getBlockColorOutlineActive();
    public abstract Color getBlockColorSourceCircle(float efficiency, boolean selfPriming);
    public abstract Color getClusterOverheatingColor();
    public abstract Color getClusterOvercoolingColor();
    public abstract Color getClusterDisconnectedColor();
    public abstract Color getClusterInvalidColor();
    public abstract Color getTooltipInvalidTextColor();
    public abstract Color getTooltipTextColor();
    public abstract Color getEditorToolTextColor(int index);
    public abstract Color getEditorToolBackgroundColor(int index);
    public abstract Color getSelectionColor();
    public abstract Color getEditorBackgroundColor();
    public abstract Color getImageExportBackgroundColor();
    public abstract Color getImageExportTextColor();
    public abstract Color getComponentTextColor(int index);
    public abstract Color getMouseoverSelectedComponentColor(int index);
    public abstract Color getSelectedComponentColor(int index);
    public abstract Color getMouseoverComponentColor(int index);
    public abstract Color getComponentColor(int index);
    public abstract Color getEditorBackgroundMouseoverColor();
    public abstract Color getEditorGridColor();
    public abstract Color getSuggestionOutlineColor();
    public abstract Color getEditorMouseoverLightColor();
    public abstract Color getEditorMouseoverDarkColor();
    public abstract Color getEditorMouseoverLineColor();
    public abstract Color getEditorListBackgroundMouseoverColor(int index);
    public abstract Color getEditorListBackgroundColor(int index);
    public abstract Color getEditorListLightSelectedColor(int index);
    public abstract Color getEditorListDarkSelectedColor(int index);
    public abstract Color getEditorListLightMouseoverColor(int index);
    public abstract Color getEditorListDarkMouseoverColor(int index);
    public abstract Color getMultiblockSelectedInputColor();
    public abstract Color getMultiblockInvalidInputColor();
    public abstract Color getSecondaryComponentColor(int index);
    public abstract Color getProgressBarBackgroundColor();
    public abstract Color getProgressBarColor();
    public abstract Color getMultiblockDisplayBorderColor();
    public abstract Color getMultiblockDisplayBackgroundColor();
    public abstract Color getToggleBlockFadeout(int index);
    public abstract Color getTutorialBackgroundColor();
    public abstract Color getScrollbarButtonColor();
    public abstract Color getBlockUnknownColor();
    public abstract Color getBlockTextColor();
    public abstract Color getScrollbarBackgroundColor();
    public abstract Color getComponentPressedColor(int index);
    public abstract Color getComponentMouseoverColor(int index);
    public abstract Color getComponentDisabledColor(int index);
    public abstract Color getSecondaryComponentPressedColor(int index);
    public abstract Color getSecondaryComponentMouseoverColor(int index);
    public abstract Color getSecondaryComponentDisabledColor(int index);
    public abstract Color getSliderColor();
    public abstract Color getSliderPressedColor();
    public abstract Color getSliderMouseoverColor();
    public abstract Color getSliderDisabledColor();
    public abstract Color getSecondarySliderColor();
    public abstract Color getSecondarySliderPressedColor();
    public abstract Color getSecondarySliderMouseoverColor();
    public abstract Color getSecondarySliderDisabledColor();
    public abstract Color getTextBoxBorderColor();
    public abstract Color getTextBoxColor();
    public abstract Color getTextViewBackgroundColor();
    public abstract Color getToggleBoxBorderColor(int index);
    public abstract Color getSecondaryToggleBoxBorderColor(int index);
    public abstract Color getToggleBoxBackgroundColor(int index);
    public abstract Color getToggleBoxSelectedColor(int index);
    public abstract Color getToggleBoxMouseoverColor(int index);
    public abstract Color getMouseoverUnselectableComponentColor(int index);
    public abstract Color getConfigurationSidebarColor();
    public abstract Color getConfigurationWarningTextColor();
    public abstract Color getConfigurationDividerColor();
    public abstract Color getDialogBorderColor();
    public abstract Color getDialogBackgroundColor();
    public abstract Color getCreditsImageColor();
    public abstract Color getCreditsBrightImageColor();
    public abstract Color getCreditsTextColor();
    public abstract Color get3DMultiblockOutlineColor();
    public abstract Color get3DDeviceoverOutlineColor();
    public abstract Color getAddButtonTextColor();
    public abstract Color getDeleteButtonTextColor();
    public abstract Color getConvertButtonTextColor();
    public abstract Color getInputsButtonTextColor();
    public abstract Color getMetadataPanelBackgroundColor();
    public abstract Color getMetadataPanelHeaderColor();
    public abstract Color getMetadataPanelTextColor();
    public abstract Color getMultiblocksListHeaderColor();
    public abstract Color getRecoveryModeColor(int index);
    public abstract Color getRecoveryModeTextColor();
    public abstract Color getRotateMultiblockTextColor();
    public abstract Color getResizeMenuTextColor();
    public abstract Color getMenuBackgroundColor();
    public abstract Color getSettingsSidebarColor();
    public abstract Color getWhiteColor();
    public abstract Color getTutorialTextColor();
    public abstract Color getVRComponentColor(int index);
    public abstract Color getVRDeviceoverComponentColor(int index);
    public abstract Color getVRSelectedOutlineColor(int index);
    public abstract Color getVRPanelOutlineColor();
    public abstract Color getVRMultitoolTextColor();
    public static Color average(Color c1, Color c2){
        return new Color((c1.getRed()+c2.getRed())/2, (c1.getGreen()+c2.getGreen())/2, (c1.getBlue()+c2.getBlue())/2, (c1.getAlpha()+c2.getAlpha())/2);
    }
    private static final double FACTOR = 0.7;
    public static Color brighter(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();
        int i = (int)(1.0/(1.0-FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;
        return new Color(Math.min((int)(r/FACTOR), 255),
                         Math.min((int)(g/FACTOR), 255),
                         Math.min((int)(b/FACTOR), 255),
                         alpha);
    }
    public static Color darker(Color color) {
        return new Color(Math.max((int)(color.getRed()  *FACTOR), 0),
                         Math.max((int)(color.getGreen()*FACTOR), 0),
                         Math.max((int)(color.getBlue() *FACTOR), 0),
                         color.getAlpha());
    }
}