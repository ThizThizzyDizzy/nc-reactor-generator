package planner.menu;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import planner.menu.component.MenuComponentMultiblock;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistTextBox;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.lwjgl.opengl.GL11;
import planner.Core;
import multiblock.configuration.PartialConfiguration;
import planner.file.FileReader;
import planner.file.FileWriter;
import planner.file.FormatWriter;
import planner.file.NCPFFile;
import multiblock.Multiblock;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.turbine.OverhaulTurbine;
import simplelibrary.Queue;
import simplelibrary.config2.Config;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuMain extends Menu{
    private MenuComponentMinimaList multiblocks = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private MenuComponentMinimalistButton addMultiblock = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "+", true, true).setTooltip("Add a new multiblock"));
    private MenuComponentMinimalistButton importFile = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Import", false, true).setTooltip("Import all multiblocks from a saved file"));
    private MenuComponentMinimalistButton exportMultiblock = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Export", false, true).setTooltip("Export the selected multiblock to a file"));
    private MenuComponentMinimalistButton saveFile = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Save", false, true).setTooltip("Save all multiblocks to a file"));
    private MenuComponentMinimalistButton loadFile = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Load", false, true).setTooltip("Load a file, replacing all current multiblocks"));
    private MenuComponentMinimalistButton editMetadata = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true).setTooltip("Edit metadata"));
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
    }.setTooltip("Settings"));
    private MenuComponentMinimalistButton delete = (MenuComponentMinimalistButton)add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Delete Multiblock (Hold Shift)", true, true).setTextColor(() -> {return Core.theme.getRed();}).setTooltip("Delete the currently selected multiblock\nWARNING: This cannot be undone!"));
    private MenuComponentMinimalistButton convertOverhaulMSFR = (MenuComponentMinimalistButton)add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Convert SFR <> MSR", true, true).setTextColor(() -> {return Core.theme.getRGB(1, .5f, 0);}));
    private MenuComponentMinimalistButton setInputs = (MenuComponentMinimalistButton)add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Set Inputs", true, true).setTextColor(() -> {return Core.theme.getRGB(1, 1, 0);}).setTooltip("Choose multiblocks to input Steam to this turbine\nYou can choose as many as you want"));
    private boolean forceMetaUpdate = true;
    private MenuComponent metadataPanel = add(new MenuComponent(0, 0, 0, 0){
        MenuComponentMulticolumnMinimaList list = add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, 0, 50, 50));
        MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true).setTooltip("Finish editing metadata"));
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
            ArrayList<simplelibrary.opengl.gui.components.MenuComponent> remove = new ArrayList<>();
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
    private Queue<PendingWrite> pendingWrites = new Queue<>();
    public OverhaulTurbine settingInputs = null;
    public MenuMain(GUI gui){
        super(gui, null);
        for(Multiblock m : Core.multiblockTypes){
            MenuComponentMinimalistButton button = add(new MenuComponentMinimalistButton(0, 0, 0, 0, m.getDefinitionName(), true, true, true).setTooltip(m.getDescriptionTooltip()));
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
                        FileWriter.write(ncpf, file, FileWriter.NCPF);
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
                    if(!writer.isMultiblockSupported(multi))continue;
                    FileFilter f = new FileNameExtensionFilter(writer.getDesc(), writer.getExtensions());
                    chooser.addChoosableFileFilter(f);
                    if(Core.isShiftPressed()&&f.getDescription().contains("PNG"))chooser.setFileFilter(f);
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
                        pendingWrites.enqueue(new PendingWrite(ncpf, file, writer));
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
            gui.open(new MenuTransition(gui, this, new MenuSettings(gui, this), MenuTransition.SlideTransition.slideFrom(0, -1), 5));
        });
        delete.addActionListener((e) -> {
            Multiblock multiblock = Core.multiblocks.get(multiblocks.getSelectedIndex());
            for(Multiblock m : Core.multiblocks){
                if(m instanceof OverhaulTurbine){
                    ((OverhaulTurbine)m).inputs.remove(multiblock);
                }
            }
            Core.multiblocks.remove(multiblock);
            onGUIOpened();
        });
        convertOverhaulMSFR.addActionListener((e) -> {
            Multiblock selected = getSelectedMultiblock();
            if(selected instanceof OverhaulSFR){
                OverhaulMSR msr = ((OverhaulSFR) selected).convertToMSR();
                Core.multiblocks.set(Core.multiblocks.indexOf(selected), msr);
            }else if(selected instanceof OverhaulMSR){
                OverhaulSFR sfr = ((OverhaulMSR) selected).convertToSFR();
                Core.multiblocks.set(Core.multiblocks.indexOf(selected), sfr);
            }
            onGUIOpened();
        });
        setInputs.addActionListener((e) -> {
            if(settingInputs==null){
                settingInputs = (OverhaulTurbine)getSelectedMultiblock();
                setInputs.label = "Finish Setting Inputs";
            }else{
                settingInputs = null;
                setInputs.label = "Set Inputs";
            }
        });
        multiblockCancel.addActionListener((e) -> {
            adding = false;
        });
    }
    @Override
    public void renderBackground(){
        Core.applyColor(Core.theme.getHeaderColor());
        drawRect(0, 0, Core.helper.displayWidth(), Core.helper.displayHeight()/16, 0);
        Core.applyColor(Core.theme.getHeader2Color());
        drawRect(0, Core.helper.displayHeight()/16, Core.helper.displayWidth()/3, Core.helper.displayHeight()/8, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawCenteredText(0, Core.helper.displayHeight()/16, Core.helper.displayWidth()/3-Core.helper.displayHeight()/16, Core.helper.displayHeight()/8, "Multiblocks");
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
        if(settingInputs!=null)multiblocks.setSelectedIndex(Core.multiblocks.indexOf(settingInputs));
        if(!pendingWrites.isEmpty()){
            pendingWrites.dequeue().write();
        }
        convertOverhaulMSFR.x = setInputs.x = editMetadata.x = Core.helper.displayWidth()/3;
        importFile.width = exportMultiblock.width = saveFile.width = loadFile.width = Core.helper.displayWidth()/12;
        exportMultiblock.x = importFile.width;
        saveFile.x = exportMultiblock.x+exportMultiblock.width;
        loadFile.x = saveFile.x+saveFile.width;
        editMetadata.width = Core.helper.displayWidth()*2/3-Core.helper.displayHeight()/16;
        importFile.height = exportMultiblock.height = saveFile.height = loadFile.height = editMetadata.height = settings.width = settings.height = Core.helper.displayHeight()/16;
        settings.x = Core.helper.displayWidth()-Core.helper.displayHeight()/16;
        multiblocks.y = Core.helper.displayHeight()/8;
        multiblocks.height = Core.helper.displayHeight()-multiblocks.y;
        multiblocks.width = Core.helper.displayWidth()/3;
        for(simplelibrary.opengl.gui.components.MenuComponent c : multiblocks.components){
            c.width = multiblocks.width-(multiblocks.hasScrollbar()?multiblocks.vertScrollbarWidth:0);
            ((MenuComponentMultiblock) c).edit.enabled = ((MenuComponentMultiblock) c).multiblock.exists()&&(!(adding||metadating));
        }
        addMultiblock.x = Core.helper.displayWidth()/3-Core.helper.displayHeight()/16;
        addMultiblock.y = Core.helper.displayHeight()/16;
        addMultiblock.width = addMultiblock.height = Core.helper.displayHeight()/16;
        convertOverhaulMSFR.height = setInputs.height = delete.height = addMultiblock.height;
        delete.width = (Core.helper.displayWidth()-multiblocks.width)*.8;
        convertOverhaulMSFR.width = setInputs.width = editMetadata.width+settings.width;
        delete.x = Core.helper.displayWidth()-delete.width;
        setInputs.y = convertOverhaulMSFR.y = addMultiblock.y;
        if(getSelectedMultiblock() instanceof OverhaulSFR){
            convertOverhaulMSFR.enabled = Core.configuration.overhaul!=null&&Core.configuration.overhaul.fissionMSR!=null&&!(adding||metadating)&&Core.isControlPressed();
            convertOverhaulMSFR.label = "Convert to MSR (Hold Control)";
            convertOverhaulMSFR.setTooltip("Convert the currently selected multiblock to an Overhaul MSR\nWARNING: All fuels will be converted to their Flouride counterparts");
        }else if(getSelectedMultiblock() instanceof OverhaulMSR){
            convertOverhaulMSFR.enabled = Core.configuration.overhaul!=null&&Core.configuration.overhaul.fissionSFR!=null&&!(adding||metadating)&&Core.isControlPressed();
            convertOverhaulMSFR.label = "Convert to SFR (Hold Control)";
            convertOverhaulMSFR.setTooltip("Convert the currently selected multiblock to an Overhaul SFR\nWARNING: All fuels will be converted to their Oxide counterparts");
        }else{
            convertOverhaulMSFR.enabled = false;
            convertOverhaulMSFR.y = -convertOverhaulMSFR.height;
        }
        if(getSelectedMultiblock() instanceof OverhaulTurbine){
            setInputs.enabled = true;
        }else{
            setInputs.enabled = false;
            setInputs.y = -setInputs.height;
        }
        delete.y = Core.helper.displayHeight()-delete.height;
        addMultiblock.enabled = !(adding||metadating);
        editMetadata.enabled = !(adding||metadating);
        settings.enabled = !(adding||metadating);
        importFile.enabled = !(adding||metadating);
        exportMultiblock.enabled = !(adding||metadating)&&multiblocks.getSelectedIndex()!=-1;
        saveFile.enabled = !Core.multiblocks.isEmpty()&&!(adding||metadating);
        loadFile.enabled = !(adding||metadating);
        delete.enabled = (!(adding||metadating)&&multiblocks.getSelectedIndex()!=-1)&&Core.isShiftPressed();
        for(MenuComponentMinimalistButton b : multiblockButtons){
            b.enabled = adding&&Core.multiblockTypes.get(multiblockButtons.indexOf(b)).exists();
        }
        multiblockCancel.enabled = adding;
        metadataPanel.width = Core.helper.displayWidth()*.75;
        metadataPanel.height = Core.helper.displayHeight()*.75;
        metadataPanel.x = Core.helper.displayWidth()/2-metadataPanel.width/2;
        double addScale = Math.min(1,Math.max(0,(adding?(addingScale+(millisSinceLastTick/50d)):(addingScale-(millisSinceLastTick/50d)))/addingTime));
        multiblockCancel.width = Core.helper.displayWidth()/3*addScale;
        multiblockCancel.height = Core.helper.displayHeight()/10*addScale;
        multiblockCancel.x = Core.helper.displayWidth()/2-multiblockCancel.width/2;
        multiblockCancel.y = Core.helper.displayHeight()-Core.helper.displayHeight()/8*1.5-multiblockCancel.height/2;
        for(int i = 0; i<multiblockButtons.size(); i++){
            MenuComponentMinimalistButton button = multiblockButtons.get(i);
            double midX = Core.helper.displayWidth()/(multiblockButtons.size()+1d)*(i+1);
            double midY = Core.helper.displayHeight()/2-multiblockCancel.height;
            button.width = button.height = Core.helper.displayWidth()/multiblockButtons.size()/2*addScale;
            button.x = midX-button.width/2;
            button.y = midY-button.height/2;
        }
        double metadataScale = Math.min(1,Math.max(0,(metadating?(metadatingScale+(millisSinceLastTick/50d)):(metadatingScale-(millisSinceLastTick/50d)))/metadatingTime));
        metadataPanel.y = Core.helper.displayHeight()/2-metadataPanel.height/2-Core.helper.displayHeight()*(1-metadataScale);
        super.render(millisSinceLastTick);
        Core.applyColor(Core.theme.getTextColor(), .4f);
        if(getSelectedMultiblock()!=null)drawCenteredText(delete.x, delete.y-45, delete.x+delete.width, delete.y-5, "Use Arrow keys to rotate preview");
    }
    @Override
    public void onGUIOpened(){
        refresh();
    }
    public void refresh(){
        multiblocks.components.clear();
        for(Multiblock multi : Core.multiblocks){
            multiblocks.add(new MenuComponentMultiblock(this, multi));
        }
        editMetadata.label = Core.metadata.containsKey("Name")?Core.metadata.get("Name"):"";
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : multiblocks.components){
            if(c instanceof MenuComponentMultiblock){
                if(button==((MenuComponentMultiblock) c).edit){
                    gui.open(/*new MenuTransition(gui, this, */new MenuEdit(gui, this, ((MenuComponentMultiblock) c).multiblock)/*, MenuTransition.SlideTransition.slideFrom(1, 0), 5)*/);
                }
            }
        }
    }
    public Multiblock getSelectedMultiblock(){
        if(multiblocks.getSelectedIndex()==-1)return null;
        return ((MenuComponentMultiblock)multiblocks.components.get(multiblocks.getSelectedIndex())).multiblock;
    }
    private static class PendingWrite{
        private final NCPFFile ncpf;
        private final File file;
        private final FormatWriter writer;
        private PendingWrite(NCPFFile ncpf, File file, FormatWriter writer){
            this.ncpf = ncpf;
            this.file = file;
            this.writer = writer;
        }
        private void write(){
            FileWriter.write(ncpf, file, writer);
        }
    }
}