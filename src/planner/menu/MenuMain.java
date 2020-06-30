package planner.menu;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import planner.menu.component.MenuComponentMultiblock;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.MenuConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.configuration.PartialConfiguration;
import planner.file.FileReader;
import planner.file.FileWriter;
import planner.file.FormatWriter;
import planner.file.NCPFFile;
import planner.multiblock.Multiblock;
import simplelibrary.config2.Config;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuMain extends Menu{
    private MenuComponentMinimaList multiblocks = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private MenuComponentMinimalistButton addMultiblock = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "+", true, true));
    private MenuComponentMinimalistButton importFile = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Import", false, true));
    private MenuComponentMinimalistButton exportMultiblock = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Export", false, true));
    private MenuComponentMinimalistButton saveFile = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Save", false, true));
    private MenuComponentMinimalistButton loadFile = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Load", false, true));
    private MenuComponentMinimalistButton editMetadata = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true));
    private MenuComponentMinimalistButton settings = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true){
        @Override
        public void drawText(){
            double holeRad = width*.1;
            int teeth = 8;
            double averageRadius = width*.3;
            double toothSize = width*.1;
            double rot = 360/16d;
            int resolution = (int)(2*Math.PI*averageRadius*2/teeth);//an extra *2 to account for wavy surface?
            GL11.glBegin(GL11.GL_QUADS);
            double angle = rot;
            double radius = averageRadius+toothSize/2;
            for(int i = 0; i<teeth*resolution; i++){
                double inX = x+width/2+Math.cos(Math.toRadians(angle-90))*holeRad;
                double inY = y+height/2+Math.sin(Math.toRadians(angle-90))*holeRad;
                GL11.glVertex2d(inX, inY);
                double outX = x+width/2+Math.cos(Math.toRadians(angle-90))*radius;
                double outY = y+height/2+Math.sin(Math.toRadians(angle-90))*radius;
                GL11.glVertex2d(outX,outY);
                angle+=(360d/(teeth*resolution));
                if(angle>=360)angle-=360;
                radius = averageRadius+(toothSize/2)*Math.cos(Math.toRadians(teeth*(angle-rot)));
                outX = x+width/2+Math.cos(Math.toRadians(angle-90))*radius;
                outY = y+height/2+Math.sin(Math.toRadians(angle-90))*radius;
                GL11.glVertex2d(outX,outY);
                inX = x+width/2+Math.cos(Math.toRadians(angle-90))*holeRad;
                inY = y+height/2+Math.sin(Math.toRadians(angle-90))*holeRad;
                GL11.glVertex2d(inX, inY);
            }
            GL11.glEnd();
        }
    });
    private MenuComponentMinimalistButton delete = (MenuComponentMinimalistButton)add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Delete Multiblock (Hold Shift)", true, true).setForegroundColor(Core.theme.getRed()));
    private boolean forceMetaUpdate = true;
    private MenuComponent metadataPanel = add(new MenuComponent(0, 0, 0, 0){
        MenuComponentMulticolumnMinimaList list = add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, 0, 50, 50));
        MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true));
        {
            done.addActionListener((e) -> {
                Core.resetMetadata();
                for(int i = 0; i<list.components.size(); i+=2){
                    MenuComponentMinimalistTextBox key = (MenuComponentMinimalistTextBox) list.components.get(i);
                    MenuComponentMinimalistTextBox value = (MenuComponentMinimalistTextBox) list.components.get(i+1);
                    if(key.text.trim().isEmpty()&&value.text.trim().isEmpty())continue;
                    Core.metadata.put(key.text, value.text);
                }
                metadating = false;
                refresh();
            });
        }
        @Override
        public void renderBackground(){
            list.width = width;
            list.y = height/16;
            list.height = height-height/8;
            list.columnWidth = (list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0))/2;
            done.width = width;
            done.y = height-height/16;
            done.height = height/16;
        }
        @Override
        public void render(){
            Core.applyColor(Core.theme.getMetadataPanelBackgroundColor());
            drawRect(x, y, x+width, y+height, 0);
            Core.applyColor(Core.theme.getMetadataPanelHeaderColor());
            drawRect(x, y, x+width, y+height/16, 0);
            Core.applyColor(Core.theme.getTextColor());
            drawCenteredText(x, y, x+width, y+height/16, "Metadata");
        }
        @Override
        public void tick(){
            if(forceMetaUpdate){
                forceMetaUpdate = false;
                list.components.clear();
                for(String key : Core.metadata.keySet()){
                    String value = Core.metadata.get(key);
                    list.add(new MenuComponentMinimalistTextBox(0,0,0,0,key, true));
                    list.add(new MenuComponentMinimalistTextBox(0,0,0,0,value, true));
                }
            }
            if(!metadating)return;
            ArrayList<MenuComponent> remove = new ArrayList<>();
            boolean add = list.components.isEmpty();
            for(int i = 0; i<list.components.size(); i+=2){
                MenuComponentMinimalistTextBox key = (MenuComponentMinimalistTextBox) list.components.get(i);
                MenuComponentMinimalistTextBox value = (MenuComponentMinimalistTextBox) list.components.get(i+1);
                if(i==list.components.size()-2){//the last one
                    if(!(key.text.trim().isEmpty()&&value.text.trim().isEmpty())){
                        add = true;
                    }
                }else{
                    if(key.text.trim().isEmpty()&&value.text.trim().isEmpty()){
                        remove.add(key);
                        remove.add(value);
                    }
                }
            }
            list.components.removeAll(remove);
            if(add){
                list.add(new MenuComponentMinimalistTextBox(0,0,0,0,"", true));
                list.add(new MenuComponentMinimalistTextBox(0,0,0,0,"", true));
            }
        }
    });
    private ArrayList<MenuComponentMinimalistButton> multiblockButtons = new ArrayList<>();
    private MenuComponentMinimalistButton multiblockCancel = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Cancel", true, true, true));
    private boolean adding = false;
    private int addingScale = 0;
    private final int addingTime = 3;
    private boolean metadating = false;
    private int metadatingScale = 0;
    private final int metadatingTime = 4;
    public MenuMain(GUI gui){
        super(gui, null);
        for(Multiblock m : Core.multiblockTypes){
            MenuComponentMinimalistButton button = add(new MenuComponentMinimalistButton(0, 0, 0, 0, m.getDefinitionName(), true, true, true));
            button.addActionListener((e) -> {
                Core.multiblocks.add(m.newInstance());
                adding = false;
                refresh();
            });
            multiblockButtons.add(button);
        }
        saveFile.addActionListener((e) -> {
            new Thread(() -> {
                NCPFFile ncpf = new NCPFFile();
                ncpf.configuration = PartialConfiguration.generate(Core.configuration, Core.multiblocks);
                ncpf.multiblocks.addAll(Core.multiblocks);
                ncpf.metadata.putAll(Core.metadata);
                JFileChooser chooser = new JFileChooser(new File("file").getAbsoluteFile().getParentFile());
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(new FileNameExtensionFilter("NuclearCraft Planner File", "ncpf"));
                chooser.addActionListener((event) -> {
                    if(event.getActionCommand().equals("ApproveSelection")){
                        File file = chooser.getSelectedFile();
                        if(!file.getName().endsWith(".ncpf"))file = new File(file.getAbsolutePath()+".ncpf");
                        if(file.exists()){
                            if(JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "File already exists!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)!=JOptionPane.OK_OPTION)return;
                            file.delete();
                        }
                        try(FileOutputStream stream = new FileOutputStream(file)){
                            Config header = Config.newConfig();
                            header.set("version", (byte)1);
                            header.set("count", ncpf.multiblocks.size());
                            Config meta = Config.newConfig();
                            for(String key : ncpf.metadata.keySet()){
                                String value = ncpf.metadata.get(key);
                                if(value.trim().isEmpty())continue;
                                meta.set(key,value);
                            }
                            if(meta.properties().length>0){
                                header.set("metadata", meta);
                            }
                            header.save(stream);
                            ncpf.configuration.save(stream);
                            for(Multiblock m : ncpf.multiblocks){
                                m.save(ncpf.configuration, stream);
                            }
                        }catch(IOException ex){
                            JOptionPane.showMessageDialog(null, ex.getMessage(), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                chooser.showSaveDialog(null);
            }).start();
        });
        loadFile.addActionListener((e) -> {
            new Thread(() -> {
                JFileChooser chooser = new JFileChooser(new File("file").getAbsoluteFile().getParentFile());
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(new FileNameExtensionFilter("NuclearCraft Planner File", "ncpf", "json"));
                chooser.addActionListener((event) -> {
                    if(event.getActionCommand().equals("ApproveSelection")){
                        File file = chooser.getSelectedFile();
                        NCPFFile ncpf = FileReader.read(file);
                        if(ncpf==null)return;
                        Core.multiblocks.clear();
                        Core.metadata.clear();
                        Core.metadata.putAll(ncpf.metadata);
                        if(ncpf.configuration==null||ncpf.configuration.isPartial()){
                            if(ncpf.configuration!=null&&!ncpf.configuration.name.equals(Core.configuration.name)){
                                JOptionPane.showMessageDialog(null, "Configuration mismatch detected!", "Failed to load file", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }else{
                            Core.configuration = ncpf.configuration;
                        }
                        for(Multiblock mb : ncpf.multiblocks){
                            mb.convertTo(Core.configuration);
                            Core.multiblocks.add(mb);
                        }
                        onGUIOpened();
                    }
                });
                chooser.showOpenDialog(null);
            }).start();
        });
        importFile.addActionListener((e) -> {
            new Thread(() -> {
                JFileChooser chooser = new JFileChooser(new File("file").getAbsoluteFile().getParentFile());
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(new FileNameExtensionFilter("NuclearCraft Planner File", "ncpf", "json"));
                chooser.addActionListener((event) -> {
                    if(event.getActionCommand().equals("ApproveSelection")){
                        File file = chooser.getSelectedFile();
                        NCPFFile ncpf = FileReader.read(file);
                        if(ncpf==null)return;
                        if(ncpf.configuration!=null&&!ncpf.configuration.name.equals(Core.configuration.name)){
                            JOptionPane.showMessageDialog(null, "Configuration mismatch detected!", "Failed to load file", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        for(Multiblock mb : ncpf.multiblocks){
                            mb.convertTo(Core.configuration);
                            Core.multiblocks.add(mb);
                        }
                        onGUIOpened();
                    }
                });
                chooser.showOpenDialog(null);
            }).start();
        });
        exportMultiblock.addActionListener((e) -> {
            new Thread(() -> {
                NCPFFile ncpf = new NCPFFile();
                Multiblock multi = getSelectedMultiblock();
                ncpf.multiblocks.add(multi);
                ncpf.configuration = PartialConfiguration.generate(Core.configuration, ncpf.multiblocks);
                JFileChooser chooser = new JFileChooser(new File("file").getAbsoluteFile().getParentFile());
                chooser.setAcceptAllFileFilterUsed(false);
                HashMap<FileFilter, FormatWriter> filters = new HashMap<>();
                for(FormatWriter writer : FileWriter.formats){
                    FileFilter f = new FileNameExtensionFilter(writer.getName(), writer.getExtensions());
                    chooser.addChoosableFileFilter(f);
                    filters.put(f, writer);
                }
                chooser.addActionListener((event) -> {
                    if(event.getActionCommand().equals("ApproveSelection")){
                        File file = chooser.getSelectedFile();
                        FormatWriter writer = filters.get(chooser.getFileFilter());
                        boolean hasExtension = false;
                        for(String ext : writer.getExtensions()){
                            if(file.getName().endsWith("."+ext))hasExtension = true;
                        }
                        if(!hasExtension)file = new File(file.getAbsolutePath()+"."+writer.getExtensions()[0]);
                        if(file.exists()){
                            if(JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "File already exists!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)!=JOptionPane.OK_OPTION)return;
                            file.delete();
                        }
                        FileWriter.write(ncpf, file, writer);
                    }
                });
                chooser.showSaveDialog(null);
            }).start();
        });
        addMultiblock.addActionListener((e) -> {
            adding = true;
        });
        editMetadata.addActionListener((e) -> {
            metadating = true;
            forceMetaUpdate = true;
        });
        settings.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, new MenuConfiguration(gui, this), MenuTransition.SlideTransition.slideFrom(0, -1), 5));
        });
        delete.addActionListener((e) -> {
            Core.multiblocks.remove(multiblocks.getSelectedIndex());
            onGUIOpened();
        });
        multiblockCancel.addActionListener((e) -> {
            adding = false;
        });
    }
    @Override
    public void renderBackground(){
        Core.applyColor(Core.theme.getHeaderColor());
        drawRect(0, 0, Display.getWidth(), Display.getHeight()/16, 0);
        Core.applyColor(Core.theme.getHeader2Color());
        drawRect(0, Display.getHeight()/16, Display.getWidth()/3, Display.getHeight()/8, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawCenteredText(0, Display.getHeight()/16, Display.getWidth()/3-Display.getHeight()/16, Display.getHeight()/8, "Multiblocks");
    }
    @Override
    public void tick(){
        super.tick();
        if(adding)addingScale = Math.min(addingScale+1, addingTime);
        else addingScale = Math.max(addingScale-1, 0);
        if(metadating)metadatingScale = Math.min(metadatingScale+1, metadatingTime);
        else metadatingScale = Math.max(metadatingScale-1, 0);
    }
    @Override
    public void render(int millisSinceLastTick){
        editMetadata.x = Display.getWidth()/3;
        importFile.width = exportMultiblock.width = saveFile.width = loadFile.width = Display.getWidth()/12;
        exportMultiblock.x = importFile.width;
        saveFile.x = exportMultiblock.x+exportMultiblock.width;
        loadFile.x = saveFile.x+saveFile.width;
        editMetadata.width = Display.getWidth()*2/3-Display.getHeight()/16;
        importFile.height = exportMultiblock.height = saveFile.height = loadFile.height = editMetadata.height = settings.width = settings.height = Display.getHeight()/16;
        settings.x = Display.getWidth()-Display.getHeight()/16;
        multiblocks.y = Display.getHeight()/8;
        multiblocks.height = Display.getHeight()-multiblocks.y;
        multiblocks.width = Display.getWidth()/3;
        for(MenuComponent c : multiblocks.components){
            c.width = multiblocks.width-(multiblocks.hasScrollbar()?multiblocks.vertScrollbarWidth:0);
            ((MenuComponentMultiblock) c).edit.enabled = !(adding||metadating);
        }
        addMultiblock.x = Display.getWidth()/3-Display.getHeight()/16;
        addMultiblock.y = Display.getHeight()/16;
        addMultiblock.width = addMultiblock.height = Display.getHeight()/16;
        delete.height = addMultiblock.height;
        delete.width = (Display.getWidth()-multiblocks.width)*.8;
        delete.x = Display.getWidth()-delete.width;
        delete.y = Display.getHeight()-delete.height;
        addMultiblock.enabled = !(adding||metadating);
        editMetadata.enabled = !(adding||metadating);
        settings.enabled = !(adding||metadating);
        importFile.enabled = !(adding||metadating);
        exportMultiblock.enabled = !(adding||metadating)&&multiblocks.getSelectedIndex()!=-1;
        saveFile.enabled = !Core.multiblocks.isEmpty()&&!(adding||metadating);
        loadFile.enabled = !(adding||metadating);
        delete.enabled = (!(adding||metadating)&&multiblocks.getSelectedIndex()!=-1)&&(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)||Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
        for(MenuComponentMinimalistButton b : multiblockButtons){
            b.enabled = adding;
        }
        multiblockCancel.enabled = adding;
        metadataPanel.width = Display.getWidth()*.75;
        metadataPanel.height = Display.getHeight()*.75;
        metadataPanel.x = Display.getWidth()/2-metadataPanel.width/2;
        double addScale = Math.min(1,Math.max(0,(adding?(addingScale+(millisSinceLastTick/50d)):(addingScale-(millisSinceLastTick/50d)))/addingTime));
        multiblockCancel.width = Display.getWidth()/3*addScale;
        multiblockCancel.height = Display.getHeight()/10*addScale;
        multiblockCancel.x = Display.getWidth()/2-multiblockCancel.width/2;
        multiblockCancel.y = Display.getHeight()-Display.getHeight()/8*1.5-multiblockCancel.height/2;
        for(int i = 0; i<multiblockButtons.size(); i++){
            MenuComponentMinimalistButton button = multiblockButtons.get(i);
            double midX = Display.getWidth()/(multiblockButtons.size()+1d)*(i+1);
            double midY = Display.getHeight()/2-multiblockCancel.height;
            button.width = button.height = Display.getWidth()/multiblockButtons.size()/2*addScale;
            button.x = midX-button.width/2;
            button.y = midY-button.height/2;
        }
        double metadataScale = Math.min(1,Math.max(0,(metadating?(metadatingScale+(millisSinceLastTick/50d)):(metadatingScale-(millisSinceLastTick/50d)))/metadatingTime));
        metadataPanel.y = Display.getHeight()/2-metadataPanel.height/2-Display.getHeight()*(1-metadataScale);
        super.render(millisSinceLastTick);
    }
    @Override
    public void onGUIOpened(){
        refresh();
    }
    public void refresh(){
        multiblocks.components.clear();
        for(Multiblock multi : Core.multiblocks){
            multiblocks.add(new MenuComponentMultiblock(multi));
        }
        editMetadata.label = Core.metadata.containsKey("Name")?Core.metadata.get("Name"):"";
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(MenuComponent c : multiblocks.components){
            if(c instanceof MenuComponentMultiblock){
                if(button==((MenuComponentMultiblock) c).edit){
                    gui.open(new MenuTransition(gui, this, new MenuEdit(gui, this, ((MenuComponentMultiblock) c).multiblock), MenuTransition.SlideTransition.slideFrom(1, 0), 5));
                }
            }
        }
    }
    public Multiblock getSelectedMultiblock(){
        if(multiblocks.getSelectedIndex()==-1)return null;
        return ((MenuComponentMultiblock)multiblocks.components.get(multiblocks.getSelectedIndex())).multiblock;
    }
}