package planner.menu.component;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import planner.ImageIO;
import planner.file.FileFormat;
import planner.menu.dialog.MenuGenerateTexture;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.image.Image;
import static simplelibrary.opengl.Renderer2D.drawRect;
public class MenuComponentTextureButton extends MenuComponentMinimalistButton{
    private final Supplier<Image> texture;
    private final Consumer<Image> setTextureFunc;
    private final String textureName;
    public MenuComponentTextureButton(double x, double y, double width, double height, String textureName, boolean enabled, boolean useMouseover, Supplier<Image> texture, Consumer<Image> setTextureFunc){
        super(x, y, width, height, textureName!=null?"Set "+textureName+" Texture":"Set Texture", enabled, useMouseover);
        this.textureName = textureName;
        this.texture = texture;
        this.setTextureFunc = setTextureFunc;
        addActionListener((e) -> {
            try{
                Core.createFileChooser((file) -> {
                    try{
                        Image img = ImageIO.read(file);
                        if(img.getWidth()!=img.getHeight()){
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                            return;
                        }
                        setTextureFunc.accept(img);
                    }catch(IOException ex){
                        Sys.error(ErrorLevel.severe, "Failed to load texture "+file.getName()+"!", ex, ErrorCategory.fileIO);
                    }
                }, FileFormat.PNG);
            }catch(IOException ex){
                Sys.error(ErrorLevel.severe, "Failed to load texture!", ex, ErrorCategory.fileIO);
            }
        });
        setTooltip("Click or drop files to change "+(textureName==null?"":(textureName.toLowerCase(Locale.ROOT)+" "))+"texture\nOr right click to generate a texture");
    }
    @Override
    public void render(){
        Image tex = texture.get();
        if(tex!=null){
            Core.applyWhite();
            drawRect(x, y, x+width, y+height, Core.getTexture(tex));
            return;
        }
        super.render();
    }
    @Override
    public boolean onFilesDropped(double x, double y, String[] files){
        for(String s : files){
            try{
                Image img = ImageIO.read(new File(s));
                if(img.getWidth()!=img.getHeight()){
                    Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                    continue;
                }
                setTextureFunc.accept(img);
            }catch(IOException ex){
                Sys.error(ErrorLevel.severe, "Failed to load texture "+s+"!", ex, ErrorCategory.fileIO);
            }
        }
        return super.onFilesDropped(x, y, files);
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(pressed&&enabled&&button==GLFW.GLFW_MOUSE_BUTTON_RIGHT&&!isPressed){
            gui.menu = new MenuGenerateTexture(gui, parent, textureName, setTextureFunc);
        }
    }
}