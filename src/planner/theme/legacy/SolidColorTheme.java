package planner.theme.legacy;
import simplelibrary.image.Color;
public class SolidColorTheme extends LegacyTheme{
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