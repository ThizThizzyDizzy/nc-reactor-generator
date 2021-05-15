package planner.menu;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import multiblock.CuboidalMultiblock;
import multiblock.Multiblock;
import multiblock.configuration.PartialConfiguration;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.turbine.OverhaulTurbine;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.exception.MissingConfigurationEntryException;
import planner.file.FileFormat;
import planner.file.FileReader;
import planner.file.FileWriter;
import planner.file.FormatWriter;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentDropdownList;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.menu.component.editor.MenuComponentMultiblock;
import planner.vr.VRCore;
import simplelibrary.Queue;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuMain extends Menu{
    private MenuComponentMinimaList multiblocks = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private MenuComponentMinimalistButton addMultiblock = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "+", true, true).setTooltip("Add a new multiblock"));
    private MenuComponentMinimalistButton importFile = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Import", false, true).setTooltip("Import all multiblocks from a saved file"));
    private MenuComponentMinimalistButton exportMain;
    private MenuComponentDropdownList exportMultiblock = add(new MenuComponentDropdownList(0, 0, 0, 0){
        {
            add(exportMain = new MenuComponentMinimalistButton(0, 0, 0, 0, "Export", false, true).setTooltip("Export the selected multiblock to a file")).addActionListener((e) -> {});
        }
        @Override
        public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
            if(!exportMain.enabled)return;
            super.onMouseButton(x, y, button, pressed, mods);
        }
        @Override
        public void render(int millisSinceLastTick){
            setSelectedIndex(0);
            super.render(millisSinceLastTick);
        }
    });
    private MenuComponentMinimalistButton saveFile = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Save", false, true).setTooltip("Save all multiblocks to a file"));
    private MenuComponentMinimalistButton loadFile = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Load", false, true).setTooltip("Load a file, replacing all current multiblocks"));
    private MenuComponentMinimalistButton editMetadata = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true).setTooltip("Edit metadata"));
    private MenuComponentMinimalistButton vr = new MenuComponentMinimalistButton(0, 0, 0, 0, "VR", true, true).setTooltip("Enter VR");
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
    private MenuComponentMinimalistButton delete = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Delete Multiblock (Hold Shift)", true, true).setTextColor(Core.theme::getDeleteButtonTextColor).setTooltip("Delete the currently selected multiblock\nWARNING: This cannot be undone!"));
    private MenuComponentMinimalistButton credits = add(new MenuComponentMinimalistButton(0, 0, 192, 48, "Credits", true, true));
    private MenuComponentMinimalistButton convertOverhaulMSFR = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Convert SFR <> MSR", true, true).setTextColor(Core.theme::getConvertButtonTextColor));
    private MenuComponentMinimalistButton setInputs = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Set Inputs", true, true).setTextColor(Core.theme::getInputsButtonTextColor).setTooltip("Choose multiblocks to input Steam to this turbine\nYou can choose as many as you want"));
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
            Core.applyColor(Core.theme.getMetadataPanelTextColor());
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
        if(Core.vr)add(vr);
        addMultiblock.textInset = 0;
        saveFile.addActionListener((e) -> {
            NCPFFile ncpf = new NCPFFile();
            ncpf.configuration = PartialConfiguration.generate(Core.configuration, Core.multiblocks);
            ncpf.multiblocks.addAll(Core.multiblocks);
            ncpf.metadata.putAll(Core.metadata);
            try{
                Core.createFileChooser(null, (file) -> {
                    if(!file.getName().endsWith(".ncpf"))file = new File(file.getAbsolutePath()+".ncpf");
                    FileWriter.write(ncpf, file, FileWriter.NCPF);
                }, FileFormat.NCPF);
            }catch(IOException ex){
                Sys.error(ErrorLevel.severe, "Failed to save file!", ex, ErrorCategory.fileIO);
            }
        });
        loadFile.addActionListener((e) -> {
            try{
                Core.createFileChooser((file) -> {
                    NCPFFile ncpf = FileReader.read(file);
                    if(ncpf==null)return;
                    Core.multiblocks.clear();
                    Core.metadata.clear();
                    Core.metadata.putAll(ncpf.metadata);
                    if(ncpf.configuration==null||ncpf.configuration.isPartial()){
                        if(ncpf.configuration!=null&&!ncpf.configuration.name.equals(Core.configuration.name)){
                            Sys.error(ErrorLevel.warning, "File configuration '"+ncpf.configuration.name+"' does not match currently loaded configuration '"+Core.configuration.name+"'!", null, ErrorCategory.other);
                        }
                    }else{
                        Core.configuration = ncpf.configuration;
                    }
                    convertAndImportMultiblocks(ncpf.multiblocks);
                    onGUIOpened();
                }, FileFormat.ALL_PLANNER_FORMATS);
            }catch(IOException ex){
                Sys.error(ErrorLevel.severe, "Failed to load file!", ex, ErrorCategory.fileIO);
            }
        });
        importFile.addActionListener((e) -> {
            try{
                Core.createFileChooser((file) -> {
                    importMultiblocks(file);
                    onGUIOpened();
                }, FileFormat.ALL_PLANNER_FORMATS);
            }catch(IOException ex){
                Sys.error(ErrorLevel.severe, "Failed to import file!", ex, ErrorCategory.fileIO);
            }
        });
        for(FormatWriter writer : FileWriter.formats){
            FileFormat format = writer.getFileFormat();
            exportMultiblock.add(new MenuComponentMinimalistButton(0, 0, 0, 0, format.name, true, true).setTooltip(format.description)).addActionListener((e) -> {
                exportMultiblock.isDown = false;
                NCPFFile ncpf = new NCPFFile();
                Multiblock multi = getSelectedMultiblock();
                ncpf.multiblocks.add(multi);
                ncpf.configuration = PartialConfiguration.generate(Core.configuration, ncpf.multiblocks);
                try{
                    Core.createFileChooser(null, (file) -> {
                        boolean hasExtension = false;
                        for(String ext : format.extensions){
                            if(file.getName().endsWith("."+ext))hasExtension = true;
                        }
                        if(!hasExtension)file = new File(file.getAbsolutePath()+"."+format.extensions[0]);
                        if(file==null)return;
                        pendingWrites.enqueue(new PendingWrite(ncpf, file, writer));
                    }, format);
                }catch(IOException ex){
                    Sys.error(ErrorLevel.severe, "Failed to export multiblock!", ex, ErrorCategory.fileIO);
                }
            });
        }
        addMultiblock.addActionListener((e) -> {
            adding = true;
        });
        editMetadata.addActionListener((e) -> {
            metadating = true;
            forceMetaUpdate = true;
        });
        settings.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, new MenuSettings(gui, this), MenuTransition.SplitTransitionX.slideIn(384d/gui.helper.displayWidth()), 5));
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
            try{
                if(selected instanceof OverhaulSFR){
                    OverhaulMSR msr = ((OverhaulSFR) selected).convertToMSR();
                    Core.multiblocks.set(Core.multiblocks.indexOf(selected), msr);
                }else if(selected instanceof OverhaulMSR){
                    OverhaulSFR sfr = ((OverhaulMSR) selected).convertToSFR();
                    Core.multiblocks.set(Core.multiblocks.indexOf(selected), sfr);
                }
            }catch(MissingConfigurationEntryException ex){
                throw new RuntimeException(ex);
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
        vr.addActionListener((e) -> {
            VRCore.start();
        });
        credits.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, new MenuCredits(gui), MenuTransition.SplitTransitionY.slideOut(0.5), 10));
        });
    }
    @Override
    public void renderBackground(){
        Core.applyColor(Core.theme.getMultiblocksListHeaderColor());
        drawRect(0, gui.helper.displayHeight()/16, gui.helper.displayWidth()/3, gui.helper.displayHeight()/8, 0);
        Core.applyColor(Core.theme.getComponentTextColor(0));
        drawCenteredText(0, gui.helper.displayHeight()/16, gui.helper.displayWidth()/3-gui.helper.displayHeight()/16, gui.helper.displayHeight()/8, "Multiblocks");
    }
    @Override
    public void tick(){
        super.tick();
        if(gui.keyboardWereDown.contains(GLFW.GLFW_KEY_F))Sys.error(ErrorLevel.warning, "F", null, ErrorCategory.other);
        if(adding)addingScale = Math.min(addingScale+1, addingTime);
        else addingScale = Math.max(addingScale-1, 0);
        if(metadating)metadatingScale = Math.min(metadatingScale+1, metadatingTime);
        else metadatingScale = Math.max(metadatingScale-1, 0);
    }
    @Override
    public void render(int millisSinceLastTick){
        if(Core.recoveryMode){
            double size = gui.helper.displayHeight()/16d;
            int colorIndex = 0;
            ImageStash.instance.bindTexture(0);
            GL11.glBegin(GL11.GL_QUADS);
            for(double d = 0; d<gui.helper.displayWidth()+size; d+=size/2){
                Core.applyColor(Core.theme.getRecoveryModeColor(colorIndex));
                GL11.glVertex2d(d, gui.helper.displayHeight()-size);
                GL11.glVertex2d(d-size, gui.helper.displayHeight());
                GL11.glVertex2d(d-size/2, gui.helper.displayHeight());
                GL11.glVertex2d(d+size/2, gui.helper.displayHeight()-size);
                colorIndex++;
            }
            GL11.glEnd();
            Core.applyColor(Core.theme.getRecoveryModeTextColor());
            drawCenteredText(0, gui.helper.displayHeight()-size*2, gui.helper.displayWidth(), gui.helper.displayHeight()-size, "RECOVERY MODE ENABLED. PRESS CTRL+SHIFT+R TO DISABLE");
        }
        if(settingInputs!=null)multiblocks.setSelectedIndex(Core.multiblocks.indexOf(settingInputs));
        if(!pendingWrites.isEmpty()){
            pendingWrites.dequeue().write();
        }
        convertOverhaulMSFR.x = setInputs.x = editMetadata.x = gui.helper.displayWidth()/3;
        exportMain.width = importFile.width = exportMultiblock.width = saveFile.width = loadFile.width = gui.helper.displayWidth()/12;
        exportMultiblock.x = importFile.width;
        saveFile.x = exportMultiblock.x+exportMultiblock.width;
        loadFile.x = saveFile.x+saveFile.width;
        editMetadata.width = gui.helper.displayWidth()*2/3-gui.helper.displayHeight()/16*(Core.vr?2:1);
        importFile.height = exportMultiblock.preferredHeight = saveFile.height = loadFile.height = editMetadata.height = vr.width = vr.height = settings.width = settings.height = gui.helper.displayHeight()/16;
        settings.x = gui.helper.displayWidth()-gui.helper.displayHeight()/16;
        vr.x = settings.x-gui.helper.displayHeight()/16;
        multiblocks.y = gui.helper.displayHeight()/8;
        convertOverhaulMSFR.height = setInputs.height = delete.height = addMultiblock.height;
        if(multiblocks.getSelectedIndex()==-1)delete.height = 0;
        multiblocks.height = gui.helper.displayHeight()-multiblocks.y-delete.height;
        delete.width = multiblocks.width = gui.helper.displayWidth()/3;
        for(simplelibrary.opengl.gui.components.MenuComponent c : multiblocks.components){
            c.width = multiblocks.width-(multiblocks.hasScrollbar()?multiblocks.vertScrollbarWidth:0);
            ((MenuComponentMultiblock) c).edit.enabled = ((MenuComponentMultiblock) c).multiblock.exists()&&(!(adding||metadating));
        }
        addMultiblock.x = gui.helper.displayWidth()/3-gui.helper.displayHeight()/16;
        addMultiblock.y = gui.helper.displayHeight()/16;
        addMultiblock.width = addMultiblock.height = gui.helper.displayHeight()/16;
        convertOverhaulMSFR.width = setInputs.width = editMetadata.width+settings.width;
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
        delete.y = gui.helper.displayHeight()-delete.height;
        credits.y = gui.helper.displayHeight()-credits.height;
        credits.x = gui.helper.displayWidth()-credits.width;
        addMultiblock.enabled = !(adding||metadating);
        editMetadata.enabled = !(adding||metadating);
        settings.enabled = !(adding||metadating);
        vr.enabled = !(adding||metadating);
        importFile.enabled = !(adding||metadating);
        exportMain.enabled = !(adding||metadating)&&multiblocks.getSelectedIndex()!=-1;
        for(MenuComponent c : exportMultiblock.components){
            if(c instanceof MenuComponentMinimalistButton)((MenuComponentMinimalistButton)c).enabled = !(adding||metadating)&&multiblocks.getSelectedIndex()!=-1;
        }
        saveFile.enabled = !Core.multiblocks.isEmpty()&&!(adding||metadating);
        loadFile.enabled = !(adding||metadating);
        delete.enabled = (!(adding||metadating)&&multiblocks.getSelectedIndex()!=-1)&&Core.isShiftPressed();
        for(MenuComponentMinimalistButton b : multiblockButtons){
            b.enabled = adding&&Core.multiblockTypes.get(multiblockButtons.indexOf(b)).exists();
        }
        multiblockCancel.enabled = adding;
        metadataPanel.width = gui.helper.displayWidth()*.75;
        metadataPanel.height = gui.helper.displayHeight()*.75;
        metadataPanel.x = gui.helper.displayWidth()/2-metadataPanel.width/2;
        double addScale = Math.min(1,Math.max(0,(adding?(addingScale+(millisSinceLastTick/50d)):(addingScale-(millisSinceLastTick/50d)))/addingTime));
        multiblockCancel.width = gui.helper.displayWidth()/3*addScale;
        multiblockCancel.height = gui.helper.displayHeight()/10*addScale;
        multiblockCancel.x = gui.helper.displayWidth()/2-multiblockCancel.width/2;
        multiblockCancel.y = gui.helper.displayHeight()-gui.helper.displayHeight()/8*1.5-multiblockCancel.height/2;
        for(int i = 0; i<multiblockButtons.size(); i++){
            MenuComponentMinimalistButton button = multiblockButtons.get(i);
            double midX = gui.helper.displayWidth()/(multiblockButtons.size()+1d)*(i+1);
            double midY = gui.helper.displayHeight()/2-multiblockCancel.height;
            button.width = button.height = gui.helper.displayWidth()/multiblockButtons.size()/2*addScale;
            button.x = midX-button.width/2;
            button.y = midY-button.height/2;
        }
        double metadataScale = Math.min(1,Math.max(0,(metadating?(metadatingScale+(millisSinceLastTick/50d)):(metadatingScale-(millisSinceLastTick/50d)))/metadatingTime));
        metadataPanel.y = gui.helper.displayHeight()/2-metadataPanel.height/2-gui.helper.displayHeight()*(1-metadataScale);
        super.render(millisSinceLastTick);
        Core.applyColor(Core.theme.getRotateMultiblockTextColor(), .4f);
        if(getSelectedMultiblock()!=null)drawCenteredText(multiblocks.x+multiblocks.width, gui.helper.displayHeight()-45, credits.x, gui.helper.displayHeight()-5, "Use Arrow keys to rotate preview");
    }
    @Override
    public void onGUIOpened(){
        refresh();
        components.removeAll(multiblockButtons);
        multiblockButtons.clear();
        for(Multiblock m : Core.multiblockTypes){
            MenuComponentMinimalistButton button = add(new MenuComponentMinimalistButton(0, 0, 0, 0, m.getDefinitionName(), true, true, true).setTooltip(m.getDescriptionTooltip()));
            button.addActionListener((e) -> {
                Multiblock mb = m.newInstance();
                if(mb instanceof OverhaulTurbine)((OverhaulTurbine)mb).setBearing(1);
                if(mb instanceof CuboidalMultiblock)((CuboidalMultiblock)mb).buildDefaultCasing();
                Core.multiblocks.add(mb);
                adding = false;
                refresh();
            });
            multiblockButtons.add(button);
        }
    }
    public void refresh(){
        multiblocks.components.clear();
        for(Multiblock multi : Core.multiblocks){
            multiblocks.add(new MenuComponentMultiblock(this, multi));
        }
        String name = Core.metadata.containsKey("Name")?Core.metadata.get("Name"):"";
        editMetadata.label = name.isEmpty()?"Edit Metadata":(name+" | Edit Metadata");
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : multiblocks.components){
            if(c instanceof MenuComponentMultiblock){
                if(button==((MenuComponentMultiblock) c).edit){
                    gui.open(new MenuTransition(gui, this, new MenuEdit(gui, this, ((MenuComponentMultiblock) c).multiblock), MenuTransition.SplitTransitionX.slideIn((MenuEdit.partSize+MenuEdit.partSize/4d+MenuEdit.partsWide*MenuEdit.partSize)/gui.helper.displayWidth()), 5));
                }
            }
        }
    }
    public Multiblock getSelectedMultiblock(){
        if(multiblocks.getSelectedIndex()==-1)return null;
        return ((MenuComponentMultiblock)multiblocks.components.get(multiblocks.getSelectedIndex())).multiblock;
    }
    private void importMultiblocks(File file){
        NCPFFile ncpf = FileReader.read(file);
        if(ncpf==null)return;
        if(ncpf.configuration!=null&&!ncpf.configuration.name.equals(Core.configuration.name)){
            Sys.error(ErrorLevel.warning, "File configuration '"+ncpf.configuration.name+"' does not match currently loaded configuration '"+Core.configuration.name+"'!", null, ErrorCategory.other);
        }
        convertAndImportMultiblocks(ncpf.multiblocks);
    }
    private void convertAndImportMultiblocks(ArrayList<Multiblock> multiblocks){
        for(Multiblock mb : multiblocks){
            try{
                mb.convertTo(Core.configuration);
            }catch(MissingConfigurationEntryException ex){
                Sys.error(ErrorLevel.warning, "Failed to load multiblock - Are you missing an addon?", ex, ErrorCategory.fileIO);
                continue;
            }
            Core.multiblocks.add(mb);
        }
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
    @Override
    public boolean onFilesDropped(double x, double y, String[] files){
        for(String fil : files){
            try{
                importMultiblocks(new File(fil));
            }catch(Exception ex){
                Sys.error(ErrorLevel.severe, "Failed to load file "+fil+"!", ex, ErrorCategory.fileIO);
            }
        }
        onGUIOpened();
        return true;
    }
    @Override
    public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
        if(isPress&&key==GLFW.GLFW_KEY_R&&Core.isControlPressed()&&Core.isShiftPressed()){
            Core.recoveryMode = !Core.recoveryMode;
        }
        super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
    }
}