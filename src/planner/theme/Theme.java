package planner.theme;
import java.util.ArrayList;
import java.util.Random;
import planner.theme.legacy.LegacyTheme;
import planner.theme.legacy.SolidColorTheme;
import simplelibrary.image.Color;
public abstract class Theme{
    public static ArrayList<ThemeCategory> themes = new ArrayList<>();
    public static final SolidColorTheme STANDARD, GOLD, CRACKER, CHOCOLATE, MARSHMALLOW;
    public static final Theme QUESTIONQUESTIONQUESTION;
    static{
        Random rand = new Random();
        newCategory("General");
        addTheme(STANDARD = new StandardTheme("Light", new Color(100, 100, 100), new Color(1f, 1f, 1f, 1f), .625f, .75f));
        addTheme(new StandardTheme("Light, but darker", new Color(50, 50, 50), new Color(.5f, .5f, .5f, 1f), .3125f, .75f));
        newCategory("Materials");
        addTheme(new SolidColorTheme("Water", new Color(64, 78, 203)));
        addTheme(new SolidColorTheme("Iron", new Color(229, 229, 229)));
        addTheme(new SolidColorTheme("Redstone", new Color(144, 16, 8)));
        addTheme(new SolidColorTheme("Quartz", new Color(166, 164, 160)));
        addTheme(new SolidColorTheme("Obsidian", new Color(20, 18, 30)));
        addTheme(new SolidColorTheme("Nether Brick", new Color(68, 4, 7)));
        addTheme(new SolidColorTheme("Glowstone", new Color(143, 117, 68)));
        addTheme(new SolidColorTheme("Lapis", new Color(37, 65, 139)));
        addTheme(GOLD = new SolidColorTheme("Gold", new Color(254, 249, 85)));
        addTheme(new SolidColorTheme("Prismarine", new Color(101, 162, 144)));
        addTheme(new SolidColorTheme("Slime", new Color(119, 187, 101)));
        addTheme(new SolidColorTheme("End Stone", new Color(225, 228, 170)));
        addTheme(new SolidColorTheme("Purpur", new Color(165, 121, 165)));
        addTheme(new SolidColorTheme("Diamond", new Color(136, 230, 226)));
        addTheme(new SolidColorTheme("Emerald", new Color(82, 221, 119)));
        addTheme(new SolidColorTheme("Copper", new Color(222, 151, 109)));
        addTheme(new SolidColorTheme("Tin", new Color(222, 225, 242)));
        addTheme(new SolidColorTheme("Lead", new Color(65, 77, 77)));
        addTheme(new SolidColorTheme("Boron", new Color(160, 160, 160)));
        addTheme(new SolidColorTheme("Lithium", new Color(241, 241, 241)));
        addTheme(new SolidColorTheme("Magnesium", new Color(242, 220, 229)));
        addTheme(new SolidColorTheme("Manganese", new Color(173, 176, 201)));
        addTheme(new SolidColorTheme("Aluminum", new Color(213, 245, 233)));
        addTheme(new SolidColorTheme("Silver", new Color(241, 238, 246)));
        addTheme(new SolidColorTheme("Fluorite", new Color(132, 160, 142)));
        addTheme(new SolidColorTheme("Villiaumite", new Color(154, 109, 97)));
        addTheme(new SolidColorTheme("Carobbiite", new Color(160, 167, 82)));
        addTheme(new SolidColorTheme("Arsenic", new Color(147, 149, 137)));
        addTheme(new SolidColorTheme("Nitrogen", new Color(64, 166, 70)));
        addTheme(new SolidColorTheme("Helium", new Color(201, 76, 73)));
        addTheme(new SolidColorTheme("Enderium", new Color(0, 71, 75)));
        addTheme(new SolidColorTheme("Cryotheum", new Color(0, 150, 194)));
        addTheme(new SolidColorTheme("Beryllium", new Color(240, 244, 236)));
        addTheme(new SolidColorTheme("Graphite", new Color(18, 18, 18)));
        addTheme(new SolidColorTheme("Heavy Water", new Color(103, 71, 210)));
        addTheme(new SolidColorTheme("Reflector", new Color(186, 144, 94)));
        addTheme(new SolidColorTheme("Conductor", new Color(129, 129, 129)));
        addTheme(new SolidColorTheme("Old Heavy Water", new Color(40, 50, 100), new Color(0.5f, 0.5f, 1f, 1f), .625f, .875f, new Color(.875f,.875f,1f,1f)));
        addTheme(new LegacyTheme("Air"){
            @Override
            public Color getBackgroundColor(){
                return average(Color.WHITE, STANDARD.getBackgroundColor());
            }
            @Override
            public Color getTextColor(){
                return process(STANDARD.getTextColor());
            }
            @Override
            public Color getHeaderColor(){
                return process(STANDARD.getHeaderColor());
            }
            @Override
            public Color getHeader2Color(){
                return process(STANDARD.getHeader2Color());
            }
            @Override
            public Color getListColor(){
                return process(STANDARD.getListColor());
            }
            @Override
            public Color getSelectedMultiblockColor(){
                return process(STANDARD.getSelectedMultiblockColor());
            }
            @Override
            public Color getListBackgroundColor(){
                return process(STANDARD.getListBackgroundColor());
            }
            @Override
            public Color getMetadataPanelBackgroundColor(){
                return process(STANDARD.getMetadataPanelBackgroundColor());
            }
            @Override
            public Color getMetadataPanelHeaderColor(){
                return process(STANDARD.getMetadataPanelHeaderColor());
            }
            @Override
            public Color getRed(){
                return process(STANDARD.getRed());
            }
            @Override
            public Color getGreen(){
                return process(STANDARD.getGreen());
            }
            @Override
            public Color getBlue(){
                return process(STANDARD.getBlue());
            }
            @Override
            public Color getEditorListBorderColor(){
                return process(STANDARD.getEditorListBorderColor());
            }
            @Override
            public Color getDarkButtonColor(){
                return process(STANDARD.getDarkButtonColor());
            }
            @Override
            public Color getButtonColor(){
                return process(STANDARD.getButtonColor());
            }
            @Override
            public Color getSelectionColor(){
                return process(STANDARD.getSelectionColor());
            }
            @Override
            public Color getRGBA(float r, float g, float b, float a){
                return process(STANDARD.getRGBA(r,g,b, a));
            }
            @Override
            public Color getWhite(){
                return process(STANDARD.getWhite());
            }
            private Color process(Color c){
                return new Color(c.getRed(), c.getBlue(), c.getGreen(), c.getAlpha()/16);
            }
            @Override
            public Color getSidebarColor(){
                return process(STANDARD.getSidebarColor());
            }
            @Override
            public Color getFadeout(){
                return process(STANDARD.getFadeout());
            }
        });
        newCategory("Colors");
        addTheme(new SolidColorTheme("Red", new Color(100, 0, 0), new Color(1f, 0f, 0f, 1f), .625f, 1f));
        addTheme(new SolidColorTheme("Orange", new Color(100, 50, 0), new Color(1, 0.5f, 0), .625f, 1f));
        addTheme(new SolidColorTheme("Yellow", new Color(100, 100, 0), new Color(1f, 1f, 0f, 1f), .625f, 1f));
        addTheme(new SolidColorTheme("Lime", new Color(50, 100, 0), new Color(.5f, 1f, 0), .625f, 1f));
        addTheme(new SolidColorTheme("Green", new Color(0, 100, 0), new Color(0f, 1f, 0f, 1f), .625f, 1f));
        addTheme(new SolidColorTheme("Bluish green", new Color(0, 100, 50), new Color(0f, 1f, .5f), .625f, 1f));
        addTheme(new SolidColorTheme("Aqua", new Color(0, 100, 100), new Color(0f, 1f, 1f, 1f), .625f, 1f));
        addTheme(new SolidColorTheme("Greenish Blue", new Color(0, 50, 100), new Color(0f, .5f, 1f), .625f, 1f));
        addTheme(new SolidColorTheme("Blue", new Color(0, 0, 100), new Color(0f, 0f, 1f, 1f), .625f, 1f));
        addTheme(new SolidColorTheme("Purple", new Color(50, 0, 100), new Color(.5f, 0f, 1f), .625f, 1f));
        addTheme(new SolidColorTheme("Magenta", new Color(100, 0, 100), new Color(1f, 0f, 1f, 1f), .625f, 1f));
        addTheme(new SolidColorTheme("Pink", new Color(100, 0, 50), new Color(1f, 0f, .5f), .625f, 1f));
        addTheme(new SolidColorTheme("Light Pink", new Color(255, 110, 199)));
        newCategory("S'mores");
        addTheme(CRACKER = new SolidColorTheme("Cracker", new Color(201, 163, 64)));//or 150 106 1
        addTheme(CHOCOLATE = new SolidColorTheme("Chocolate", new Color(97, 20, 0)));
        addTheme(MARSHMALLOW = new SolidColorTheme("Marshmallow", new Color(248, 248, 248)));
        addTheme(new SmoreTheme("S'more", CRACKER, CHOCOLATE, MARSHMALLOW));
        addTheme(new SmoreTheme("Golden S'more", GOLD, CHOCOLATE, MARSHMALLOW));
        addTheme(new SmoreTheme("Rainbow S'more", new RainbowTheme("Rainbow Cracker", 0, 0.25f, 0.9f, 1), new RainbowTheme("Rainbow Chocolate", 0.333f, 0.25f, 1f, 0.5f), new RainbowTheme("Rainbow Marshmallow", 0.777f, 0.25f, 0.5f, 1f)));
        newCategory("Other");
        addTheme(new ChangingColorTheme("Random", () -> {
            return new SolidColorTheme("Random", new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
        }));
        addTheme(QUESTIONQUESTIONQUESTION = new ChangingColorTheme("???", () -> {
            return new RandomColorsTheme("???");
        }));
        addTheme(new SiezureTheme("Disco"));
        addTheme(new RainbowTheme("Rainbow"));
    }
    private static ThemeCategory currentCategory;
    private static void newCategory(String name){
        themes.add(currentCategory = new ThemeCategory(name));
    }
    private static void addTheme(Theme theme){
        currentCategory.add(theme);
    }
    public static Theme getByName(String name){
        for(ThemeCategory cat : themes){
            for(Theme t : cat){
                if(t.name.equals(name))return t;
            }
        }
        return null;
    }
    public static Theme getByLegacyID(int i){
        ArrayList<Theme> allThemes = new ArrayList<>();
        for(ThemeCategory cat : themes)allThemes.addAll(cat);
        return allThemes.get(i);
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
    public abstract Color getSettingsMergeTextColor();
    public abstract void drawThemeButtonBackground(double x, double y, double width, double height, boolean darker, boolean enabled, boolean pressed, boolean mouseOver);
    public abstract void drawThemeButtonText(double x, double y, double width, double height, double textHeight, String text);
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
    public boolean shouldContantlyUpdateBackground(){
        return false;
    }
}