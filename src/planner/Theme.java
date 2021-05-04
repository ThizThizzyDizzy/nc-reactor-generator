package planner;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import planner.menu.dialog.MenuSiezureTheme;
import simplelibrary.image.Color;
public abstract class Theme{
    public static ArrayList<Theme> themes = new ArrayList<>();
    static{
        themes.add(new SolidColorTheme("Light", new Color(100, 100, 100), new Color(1f, 1f, 1f, 1f), .625f, .75f));
        themes.add(new SolidColorTheme("Light, but darker", new Color(50, 50, 50), new Color(.5f, .5f, .5f, 1f), .3125f, .75f));
        themes.add(new SolidColorTheme("Water", new Color(64, 78, 203)));
        themes.add(new SolidColorTheme("Iron", new Color(229, 229, 229)));
        themes.add(new SolidColorTheme("Redstone", new Color(144, 16, 8)));
        themes.add(new SolidColorTheme("Quartz", new Color(166, 164, 160)));
        themes.add(new SolidColorTheme("Obsidian", new Color(20, 18, 30)));
        themes.add(new SolidColorTheme("Nether Brick", new Color(68, 4, 7)));
        themes.add(new SolidColorTheme("Glowstone", new Color(143, 117, 68)));
        themes.add(new SolidColorTheme("Lapis", new Color(37, 65, 139)));
        themes.add(new SolidColorTheme("Gold", new Color(254, 249, 85)));
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
        themes.add(new Theme("Air"){
            @Override
            public Color getBackgroundColor(){
                return average(Color.WHITE, themes.get(0).getBackgroundColor());
            }
            @Override
            public Color getTextColor(){
                return process(themes.get(0).getTextColor());
            }
            @Override
            public Color getHeaderColor(){
                return process(themes.get(0).getHeaderColor());
            }
            @Override
            public Color getHeader2Color(){
                return process(themes.get(0).getHeader2Color());
            }
            @Override
            public Color getListColor(){
                return process(themes.get(0).getListColor());
            }
            @Override
            public Color getSelectedMultiblockColor(){
                return process(themes.get(0).getSelectedMultiblockColor());
            }
            @Override
            public Color getListBackgroundColor(){
                return process(themes.get(0).getListBackgroundColor());
            }
            @Override
            public Color getMetadataPanelBackgroundColor(){
                return process(themes.get(0).getMetadataPanelBackgroundColor());
            }
            @Override
            public Color getMetadataPanelHeaderColor(){
                return process(themes.get(0).getMetadataPanelHeaderColor());
            }
            @Override
            public Color getRed(){
                return process(themes.get(0).getRed());
            }
            @Override
            public Color getGreen(){
                return process(themes.get(0).getGreen());
            }
            @Override
            public Color getBlue(){
                return process(themes.get(0).getBlue());
            }
            @Override
            public Color getEditorListBorderColor(){
                return process(themes.get(0).getEditorListBorderColor());
            }
            @Override
            public Color getDarkButtonColor(){
                return process(themes.get(0).getDarkButtonColor());
            }
            @Override
            public Color getButtonColor(){
                return process(themes.get(0).getButtonColor());
            }
            @Override
            public Color getSelectionColor(){
                return process(themes.get(0).getSelectionColor());
            }
            @Override
            public Color getRGBA(float r, float g, float b, float a){
                return process(themes.get(0).getRGBA(r,g,b, a));
            }
            @Override
            public Color getWhite(){
                return process(themes.get(0).getWhite());
            }
            private Color process(Color c){
                return new Color(c.getRed(), c.getBlue(), c.getGreen(), c.getAlpha()/16);
            }
            @Override
            public Color getSidebarColor(){
                return process(themes.get(0).getSidebarColor());
            }
            @Override
            public Color getFadeout(){
                return process(themes.get(0).getFadeout());
            }
        });
        themes.add(new ChangingTheme("Random", () -> {
            Random rand = new Random();
            return new SolidColorTheme("Random", new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
        }));
        themes.add(new ChangingTheme("???", RandomColorsTheme::new));
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
    public abstract Color getBackgroundColor();
    public abstract Color getTextColor();
    public abstract Color getHeaderColor();
    public abstract Color getHeader2Color();
    public abstract Color getListColor();
    public abstract Color getSelectedMultiblockColor();
    public abstract Color getListBackgroundColor();
    public abstract Color getMetadataPanelBackgroundColor();
    public abstract Color getMetadataPanelHeaderColor();
    public abstract Color getRed();
    public abstract Color getGreen();
    public abstract Color getBlue();
    public abstract Color getEditorListBorderColor();
    public abstract Color getDarkButtonColor();
    public abstract Color getButtonColor();
    public abstract Color getSidebarColor();
    public abstract Color getSelectionColor();
    public abstract Color getRGBA(float r, float g, float b, float a);
    public abstract Color getWhite();
    public void onSet(){}
    public abstract Color getFadeout();
    public Color getRGBA(Color color){
        return getRGBA(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f);
    }
    //TODO no brighter()/darker()
    public Color getBrighterEditorListBorderColor(){
        return brighter(getEditorListBorderColor());
    }
    public Color getDarkerEditorListBorderColor(){
        return darker(getEditorListBorderColor());
    }
    public Color getDarkerTextColor(){
        return darker(getTextColor());
    }
    public Color getDarkerDarkButtonColor(){
        return darker(getDarkButtonColor());
    }
    public Color getBrighterDarkButtonColor(){
        return brighter(getDarkButtonColor());
    }
    public Color getDarkerButtonColor(){
        return darker(getButtonColor());
    }
    public Color getBrighterButtonColor(){
        return brighter(getButtonColor());
    }
    public Color getDarkerDarkerDarkButtonColor(){
        return darker(darker(getDarkButtonColor()));
    }
    public Color getDarkerDarkerButtonColor(){
        return darker(darker(getButtonColor()));
    }
    public Color getBrighterDarkerDarkerDarkButtonColor(){
        return brighter(darker(darker(getDarkButtonColor())));
    }
    public Color getBrighterBrighterDarkButtonColor(){
        return brighter(brighter(getDarkButtonColor()));
    }
    public Color getBrighterDarkerDarkerButtonColor(){
        return brighter(darker(darker(getButtonColor())));
    }
    public Color getBrighterBrighterButtonColor(){
        return brighter(brighter(getButtonColor()));
    }
    public Color getBrighterDarkerButtonColor(){
        return brighter(darker(getButtonColor()));
    }
    public Color getDarkerListColor(){
        return darker(getListColor());
    }
    private static class SolidColorTheme extends Theme{
        private final Color background;
        private final Color color;
        private final float rgbTint;
        private final float rgbSat;
        private final Color white;
        public SolidColorTheme(String name, Color color){
            this(name, new Color(color.getRed()*100/255, color.getGreen()*100/255, color.getBlue()*100/255, color.getAlpha()), color);
        }
        public SolidColorTheme(String name, Color background, Color color){
            this(name, background, color, .625f, 1);
        }
        public SolidColorTheme(String name, Color background, Color color, float rgbTint, float rgbSat){
            this(name, background, color, rgbTint, rgbSat, average(color, Color.WHITE));
        }
        public SolidColorTheme(String name, Color background, Color color, float rgbTint, float rgbSat, Color white){
            super(name);
            this.background = background;
            this.color = color;
            this.rgbTint = rgbTint;
            this.rgbSat = rgbSat;
            this.white = white;
        }
        @Override
        public Color getBackgroundColor(){
            return background;
        }
        @Override
        public Color getTextColor(){
            return tint(.2f);
        }
        @Override
        public Color getHeaderColor(){
            return tint(.4f);
        }
        @Override
        public Color getHeader2Color(){
            return tint(.5f);
        }
        @Override
        public Color getListColor(){
            return tint(.5f);
        }
        @Override
        public Color getDarkButtonColor(){
            return tint(.55f);
        }
        @Override
        public Color getButtonColor(){
            return tint(.6f);
        }
        @Override
        public Color getSelectedMultiblockColor(){
            return tint(.7f);
        }
        @Override
        public Color getListBackgroundColor(){
            return tint(.8f);
        }
        @Override
        public Color getEditorListBorderColor(){
            return tint(.9f);
        }
        @Override
        public Color getMetadataPanelBackgroundColor(){
            return tint(background, .45f);
        }
        @Override
        public Color getMetadataPanelHeaderColor(){
            return tint(.45f);
        }
        @Override
        public Color getSidebarColor(){
            return tint(background, .9f);
        }
        @Override
        public Color getRed(){
            return new Color(rgbTint, rgbTint*(1-rgbSat), rgbTint*(1-rgbSat));
        }
        @Override
        public Color getGreen(){
            return new Color(rgbTint*(1-rgbSat), rgbTint, rgbTint*(1-rgbSat));
        }
        @Override
        public Color getBlue(){
            return new Color(rgbTint*(1-rgbSat), rgbTint*(1-rgbSat), rgbTint);
        }
        private Color tint(float f){
            return tint(color, f);
        }
        private Color tint(Color color, float f){
            return new Color(color.getRed()/255f*f, color.getGreen()/255f*f, color.getBlue()/255f*f, color.getAlpha()/255f);
        }
        @Override
        public Color getRGBA(float r, float g, float b, float a){
            return new Color(r*(1-(1-rgbTint)*rgbSat), g*(1-(1-rgbTint)*rgbSat), b*(1-(1-rgbTint)*rgbSat), a);
        }
        @Override
        public Color getSelectionColor(){
            return getRGBA(.75f, .75f, 0, 1);
        }
        @Override
        public Color getWhite(){
            return white;
        }
        @Override
        public Color getFadeout(){
            return new Color(background.getRed(), background.getGreen(), background.getBlue(), 255*3/4);
        }
    }
    private static class RandomColorsTheme extends Theme{
        private final Color background;
        private final Color text;
        private final Color header;
        private final Color header2;
        private final Color list;
        private final Color selectedMultiblock;
        private final Color listBackground;
        private final Color metadataPanelBackground;
        private final Color metadataPanelHeader;
        private final Color red;
        private final Color green;
        private final Color blue;
        private final Color editorListBorder;
        private final Color darkButton;
        private final Color button;
        private final Color selection;
        private final Color rgb;
        private final Color white;
        private final Color fadeout;
        private final Color sidebar;
        public RandomColorsTheme(){
            this("RANDOM");
        }
        public RandomColorsTheme(String name){
            super(name);
            background = rand();
            text = rand();
            header = rand();
            header2 = rand();
            list = rand();
            selectedMultiblock = rand();
            listBackground = rand();
            metadataPanelBackground = rand();
            metadataPanelHeader = rand();
            red = rand();
            green = rand();
            blue = rand();
            editorListBorder = rand();
            darkButton = rand();
            button = rand();
            selection = rand();
            rgb = rand();
            white = rand();
            fadeout = rand();
            sidebar = rand();
        }
        @Override
        public Color getBackgroundColor(){
            return background;
        }
        @Override
        public Color getTextColor(){
            return text;
        }
        @Override
        public Color getHeaderColor(){
            return header;
        }
        @Override
        public Color getHeader2Color(){
            return header2;
        }
        @Override
        public Color getListColor(){
            return list;
        }
        @Override
        public Color getSelectedMultiblockColor(){
            return selectedMultiblock;
        }
        @Override
        public Color getListBackgroundColor(){
            return listBackground;
        }
        @Override
        public Color getMetadataPanelBackgroundColor(){
            return metadataPanelBackground;
        }
        @Override
        public Color getMetadataPanelHeaderColor(){
            return metadataPanelHeader;
        }
        @Override
        public Color getRed(){
            return red;
        }
        @Override
        public Color getGreen(){
            return green;
        }
        @Override
        public Color getBlue(){
            return blue;
        }
        @Override
        public Color getEditorListBorderColor(){
            return editorListBorder;
        }
        @Override
        public Color getDarkButtonColor(){
            return darkButton;
        }
        @Override
        public Color getButtonColor(){
            return button;
        }
        @Override
        public Color getSelectionColor(){
            return selection;
        }
        @Override
        public Color getRGBA(float r, float g, float b, float a){
            return average(rgb, new Color(r,g,b,a));
        }
        @Override
        public Color getWhite(){
            return white;
        }
        private Color rand(){
            Random rand = new Random();
            return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        }
        @Override
        public Color getFadeout(){
            return fadeout;
        }
        @Override
        public Color getSidebarColor(){
            return sidebar;
        }
    }
    private static class ChangingTheme extends Theme{
        private Theme current;
        private final Supplier<Theme> theme;
        public ChangingTheme(String name, Supplier<Theme> theme){
            super(name);
            this.theme = theme;
            current = theme.get();
        }
        @Override
        public void onSet(){
            current = theme.get();
        }
        @Override
        public Color getBackgroundColor(){
            return current.getBackgroundColor();
        }
        @Override
        public Color getTextColor(){
            return current.getTextColor();
        }
        @Override
        public Color getHeaderColor(){
            return current.getHeaderColor();
        }
        @Override
        public Color getHeader2Color(){
            return current.getHeader2Color();
        }
        @Override
        public Color getListColor(){
            return current.getListColor();
        }
        @Override
        public Color getSelectedMultiblockColor(){
            return current.getSelectedMultiblockColor();
        }
        @Override
        public Color getListBackgroundColor(){
            return current.getListBackgroundColor();
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
        public Color getRed(){
            return current.getRed();
        }
        @Override
        public Color getGreen(){
            return current.getGreen();
        }
        @Override
        public Color getBlue(){
            return current.getBlue();
        }
        @Override
        public Color getEditorListBorderColor(){
            return current.getEditorListBorderColor();
        }
        @Override
        public Color getDarkButtonColor(){
            return current.getDarkButtonColor();
        }
        @Override
        public Color getButtonColor(){
            return current.getButtonColor();
        }
        @Override
        public Color getSelectionColor(){
            return current.getSelectionColor();
        }
        @Override
        public Color getRGBA(float r, float g, float b, float a){
            return current.getRGBA(r,g,b,a);
        }
        @Override
        public Color getWhite(){
            return current.getWhite();
        }
        @Override
        public Color getFadeout(){
            return current.getFadeout();
        }
        @Override
        public Color getSidebarColor(){
            return current.getSidebarColor();
        }
    }
    private static class SiezureTheme extends Theme{
        private Boolean siezureAllowed = null;
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
        public SiezureTheme(String name){
            super(name);
        }
        @Override
        public Color getBackgroundColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getBackgroundColor();
            return rand();
        }
        @Override
        public Color getTextColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getTextColor();
            return rand();
        }
        @Override
        public Color getHeaderColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getHeaderColor();
            return rand();
        }
        @Override
        public Color getHeader2Color(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getHeader2Color();
            return rand();
        }
        @Override
        public Color getListColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getListColor();
            return rand();
        }
        @Override
        public Color getSelectedMultiblockColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getSelectedMultiblockColor();
            return rand();
        }
        @Override
        public Color getListBackgroundColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getListBackgroundColor();
            return rand();
        }
        @Override
        public Color getMetadataPanelBackgroundColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getMetadataPanelBackgroundColor();
            return rand();
        }
        @Override
        public Color getMetadataPanelHeaderColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getMetadataPanelHeaderColor();
            return rand();
        }
        @Override
        public Color getRed(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getRed();
            return rand();
        }
        @Override
        public Color getGreen(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getGreen();
            return rand();
        }
        @Override
        public Color getBlue(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getBlue();
            return rand();
        }
        @Override
        public Color getEditorListBorderColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getEditorListBorderColor();
            return rand();
        }
        @Override
        public Color getDarkButtonColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getDarkButtonColor();
            return rand();
        }
        @Override
        public Color getButtonColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getButtonColor();
            return rand();
        }
        @Override
        public Color getSelectionColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getSelectionColor();
            return rand();
        }
        @Override
        public Color getRGBA(float r, float g, float b, float a){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getRGBA(r, g, b, a);
            return rand();
        }
        @Override
        public Color getWhite(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getWhite();
            return rand();
        }
        private Color rand(){
            Random rand = new Random();
            return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        }
        @Override
        public Color getFadeout(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getFadeout();
            return rand();
        }
        @Override
        public Color getSidebarColor(){
            if(!Objects.equals(siezureAllowed, Boolean.TRUE))return themes.get(0).getSidebarColor();
            return rand();
        }
    }
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