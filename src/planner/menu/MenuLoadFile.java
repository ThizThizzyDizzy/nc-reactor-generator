package planner.menu;
import java.io.File;
import java.util.Locale;
import planner.FileChooserResultListener;
import planner.file.FileFormat;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentTextDisplay;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuLoadFile extends Menu{
    private final FileChooserResultListener listener;
    private final FileFormat[] formats;
    public MenuComponentMinimalistButton cancel = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Cancel", true, true));
    private final MenuComponentTextDisplay text;
    public MenuLoadFile(GUI gui, simplelibrary.opengl.gui.Menu parent, FileChooserResultListener listener, FileFormat[] formats){
        super(gui, parent);
        text = add(new MenuComponentTextDisplay("", true));
        this.listener = listener;
        this.formats = formats;
        setText("");
        cancel.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void renderBackground(){
        text.width = gui.helper.displayWidth();
        cancel.width = gui.helper.displayWidth();
        text.height = cancel.y = gui.helper.displayHeight()-cancel.height;
    }
    public void setText(String extra){
        String txt = "Drag-and-drop a file onto this window\nAllowed File formats:";
        for(FileFormat format : formats){
            txt+="\n"+format.description;
        }
        text.setText(txt+"\n\n"+extra);
    }
    @Override
    public boolean onFilesDropped(double x, double y, String[] files){
        if(files.length!=1){
            setText("One file at a time");
        }else{
            String fil = files[0];
            FileFormat format = null;
            for(FileFormat form : formats){
                for(String ext : form.extensions){
                    if(fil.toLowerCase(Locale.ENGLISH).endsWith("."+ext))format = form;
                }
            }
            if(format==null){
                setText("Unrecognized file format!");
            }else{
                gui.open(parent);
                listener.approved(new File(fil), format);
            }
        }
        return true;
    }
}