package planner;
import java.awt.Color;
import java.util.ArrayList;
public abstract class Theme{
    public static ArrayList<Theme> themes = new ArrayList<>();
    static{
        themes.add(new SolidColorTheme("Grey", new Color(100, 100, 100), new Color(1f, 1f, 1f, 1f), .625f, .75f));
        themes.add(new SolidColorTheme("Heavy Water", new Color(40, 50, 100), new Color(0.5f, 0.5f, 1f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Violet", new Color(100, 0, 100), new Color(1f, 0f, 1f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Blue", new Color(0, 0, 100), new Color(0f, 0f, 1f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Aqua", new Color(0, 100, 100), new Color(0f, 1f, 1f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Green", new Color(0, 100, 0), new Color(0f, 1f, 0f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Yellow", new Color(100, 100, 0), new Color(1f, 1f, 0f, 1f), .625f, 1f));
        themes.add(new SolidColorTheme("Red", new Color(100, 0, 0), new Color(1f, 0f, 0f, 1f), .625f, 1f));
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
    public abstract Color getSelectionColor();
    public abstract Color getRGB(float r, float g, float b);
    private static class SolidColorTheme extends Theme{
        private final Color background;
        private final Color color;
        private final float rgbTint;
        private final float rgbSat;
        public SolidColorTheme(String name, Color background, Color color, float rgbTint, float rgbSat){
            super(name);
            this.background = background;
            this.color = color;
            this.rgbTint = rgbTint;
            this.rgbSat = rgbSat;
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
        public Color getRGB(float r, float g, float b){
            return new Color(r*(1-(1-rgbTint)*rgbSat), g*(1-(1-rgbTint)*rgbSat), b*(1-(1-rgbTint)*rgbSat));
        }
        @Override
        public Color getSelectionColor(){
            return getRGB(.75f, .75f, 0);
        }
    }
}