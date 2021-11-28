package net.ncplanner.plannerator.planner.gui.menu;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.PartialConfiguration;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.Queue;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.file.FileWriter;
import net.ncplanner.plannerator.planner.file.FormatWriter;
import net.ncplanner.plannerator.planner.file.NCPFFile;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.DropdownList;
import net.ncplanner.plannerator.planner.gui.menu.component.MulticolumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentMultiblock;
import net.ncplanner.plannerator.planner.vr.VRCore;
import org.joml.Matrix4f;
import static org.lwjgl.glfw.GLFW.*;
public class MenuMain extends Menu{
    private SingleColumnList multiblocks = add(new SingleColumnList(0, 0, 0, 0, 50));
    private Button addMultiblock = add(new Button(0, 0, 0, 0, "+", true, true).setTooltip("Add a new multiblock"));
    private Button importFile = add(new Button(0, 0, 0, 0, "Import", false, true).setTooltip("Import all multiblocks from a saved file"));
    private Button exportMain;
    private DropdownList exportMultiblock = add(new DropdownList(0, 0, 0, 0){
        {
            add(exportMain = new Button(0, 0, 0, 0, "Export", false, true).setTooltip("Export the selected multiblock to a file"));
        }
        @Override
        public void onMouseButton(double x, double y, int button, int action, int mods){
            if(!exportMain.enabled)return;
            super.onMouseButton(x, y, button, action, mods);
        }
        @Override
        public void render2d(double deltaTime){
            setSelectedIndex(0);
            super.render2d(deltaTime);
        }
    });
    private Button saveFile = add(new Button(0, 0, 0, 0, "Save", false).setTooltip("Save all multiblocks to a file"));
    private Button loadFile = add(new Button(0, 0, 0, 0, "Load", false).setTooltip("Load a file, replacing all current multiblocks"));
    private Button editMetadata = add(new Button(0, 0, 0, 0, "", true).setTooltip("Edit metadata"));
    private Button vr = new Button(0, 0, 0, 0, "VR", true).setTooltip("Enter VR");
    private Button settings = add(new Button(0, 0, 0, 0, "", true){
        @Override
        public void drawText(Renderer renderer, double deltaTime){
            renderer.drawGear(x+width/2, y+height/2, width*.1f, 8, width*.3f, width*.1f, 360/16f);
        }
    }.setTooltip("Settings"));
    private Button delete = add(new Button(0, 0, 0, 0, "Delete Multiblock (Hold Shift)", true, true).setTextColor(Core.theme::getDeleteButtonTextColor).setTooltip("Delete the currently selected multiblock\nWARNING: This cannot be undone!"));
    private Button credits = add(new Button(0, 0, 192, 48, "Credits", true, true));
    private Button convertOverhaulMSFR = add(new Button(0, 0, 0, 0, "Convert SFR <> MSR", true, true).setTextColor(Core.theme::getConvertButtonTextColor));
    private Button setInputs = add(new Button(0, 0, 0, 0, "Set Inputs", true, true).setTextColor(Core.theme::getInputsButtonTextColor).setTooltip("Choose multiblocks to input Steam to this turbine\nYou can choose as many as you want"));
    private boolean forceMetaUpdate = true;
    private Component metadataPanel = add(new Component(0, 0, 0, 0){
        MulticolumnList list = add(new MulticolumnList(0, 0, 0, 0, 0, 50, 50));
        Button done = add(new Button(0, 0, 0, 0, "Done", true).setTooltip("Finish editing metadata"));
        Button stack = add(new Button(0, 0, 0, 0, "S'tack", true, true).setTooltip("What's this doing here?"));
        {
            done.addAction(() -> {
                Core.resetMetadata();
                for(int i = 0; i<list.components.size(); i+=2){
                    TextBox key = (TextBox) list.components.get(i);
                    TextBox value = (TextBox) list.components.get(i+1);
                    if(key.text.trim().isEmpty()&&value.text.trim().isEmpty())continue;
                    Core.metadata.put(key.text, value.text);
                }
                metadating = false;
                refresh();
            });
            stack.addAction(() -> {
                if(enables)gui.open(new MenuStackEditor(gui, MenuMain.this));
            });
        }
        @Override
        public void render2d(double deltaTime){
            if(forceMetaUpdate){
                forceMetaUpdate = false;
                list.components.clear();
                for(String key : Core.metadata.keySet()){
                    String value = Core.metadata.get(key);
                    list.add(new TextBox(0,0,0,0,key, true));
                    list.add(new TextBox(0,0,0,0,value, true));
                }
            }
            if(!metadating)return;
            ArrayList<Component> remove = new ArrayList<>();
            boolean add = list.components.isEmpty();
            for(int i = 0; i<list.components.size(); i+=2){
                TextBox key = (TextBox) list.components.get(i);
                TextBox value = (TextBox) list.components.get(i+1);
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
                list.add(new TextBox(0,0,0,0,"", true));
                list.add(new TextBox(0,0,0,0,"", true));
            }
            super.render2d(deltaTime);
        }
        @Override
        public void drawBackground(double deltaTime){
            list.width = width;
            list.y = height/16;
            list.height = height-height/8;
            list.columnWidth = (list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0))/2;
            done.width = width;
            done.y = height-height/16;
            done.height = height/16;
            stack.height = done.height*3/4*(enables?1:0);
            stack.width = stack.height*4*(enables?1:0);
            stack.y = done.y-stack.height;
            stack.enabled = enables;
        }
        @Override
        public void draw(double deltaTime){
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getMetadataPanelBackgroundColor());
            renderer.fillRect(x, y, x+width, y+height);
            renderer.setColor(Core.theme.getMetadataPanelHeaderColor());
            renderer.fillRect(x, y, x+width, y+height/16);
            renderer.setColor(Core.theme.getMetadataPanelTextColor());
            renderer.drawCenteredText(x, y, x+width, y+height/16, "Metadata");
        }
    });
    private ArrayList<Button> multiblockButtons = new ArrayList<>();
    private Button multiblockCancel = add(new Button(0, 0, 0, 0, "Cancel", true, true));
    public static boolean enables = false;
    private boolean adding = false;
    private double addingScale = 0;
    private final int addingTime = 3;
    private boolean metadating = false;
    private double metadatingScale = 0;
    private final int metadatingTime = 4;
    private Queue<PendingWrite> pendingWrites = new Queue<>();
    public OverhaulTurbine settingInputs = null;
    private boolean refreshNeeded = true;
    private float maxYRot = 80f;
    private float xRot = 30;
    private float yRot = 30;
    public MenuMain(GUI gui){
        super(gui, null);
        if(Core.vr)add(vr);
        addMultiblock.textInset = 0;
        saveFile.addAction(() -> {
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
                Core.error("Failed to save file!", ex);
            }
        });
        loadFile.addAction(() -> {
            try{
                Core.createFileChooser((file) -> {
                    NCPFFile ncpf = FileReader.read(file);
                    if(ncpf==null)return;
                    Core.multiblocks.clear();
                    Core.metadata.clear();
                    Core.metadata.putAll(ncpf.metadata);
                    if(ncpf.configuration==null||ncpf.configuration.isPartial()){
                        if(ncpf.configuration!=null&&!ncpf.configuration.name.equals(Core.configuration.name)){
                            Core.warning("File configuration '"+ncpf.configuration.name+"' does not match currently loaded configuration '"+Core.configuration.name+"'!", null);
                        }
                    }else{
                        Core.configuration = ncpf.configuration;
                    }
                    convertAndImportMultiblocks(ncpf.multiblocks);
                    onOpened();
                }, FileFormat.ALL_PLANNER_FORMATS);
            }catch(IOException ex){
                Core.error("Failed to load file!", ex);
            }
        });
        importFile.addAction(() -> {
            try{
                Core.createFileChooser((file) -> {
                    importMultiblocks(file);
                    onOpened();
                }, FileFormat.ALL_PLANNER_FORMATS);
            }catch(IOException ex){
                Core.error("Failed to import file!", ex);
            }
        });
        for(FormatWriter writer : FileWriter.formats){
            FileFormat format = writer.getFileFormat();
            exportMultiblock.add(new Button(0, 0, 0, 0, format.name, true, true).setTooltip(format.description)).addAction(() -> {
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
                    Core.error("Failed to export multiblock!", ex);
                }
            });
        }
        addMultiblock.addAction(() -> {
            adding = true;
        });
        editMetadata.addAction(() -> {
            metadating = true;
            forceMetaUpdate = true;
        });
        settings.addAction(() -> {
            gui.open(new MenuTransition(gui, this, new MenuSettings(gui, this), MenuTransition.SplitTransitionX.slideIn(384f/gui.getWidth()), 5));
        });
        delete.addAction(() -> {
            Multiblock multiblock = Core.multiblocks.get(multiblocks.getSelectedIndex());
            for(Multiblock m : Core.multiblocks){
                if(m instanceof OverhaulTurbine){
                    ((OverhaulTurbine)m).inputs.remove(multiblock);
                }
            }
            Core.multiblocks.remove(multiblock);
            onOpened();
        });
        convertOverhaulMSFR.addAction(() -> {
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
            onOpened();
        });
        setInputs.addAction(() -> {
            if(settingInputs==null){
                settingInputs = (OverhaulTurbine)getSelectedMultiblock();
                setInputs.text = "Finish Setting Inputs";
            }else{
                settingInputs = null;
                setInputs.text = "Set Inputs";
            }
        });
        multiblockCancel.addAction(() -> {
            adding = false;
        });
        vr.addAction(() -> {
            VRCore.start();
        });
        credits.addAction(() -> {
            gui.open(new MenuTransition(gui, this, new MenuCredits(gui), MenuTransition.SplitTransitionY.slideOut(0.5f), 10));
        });
    }
    @Override
    public void drawBackground(double deltaTime){
        super.drawBackground(deltaTime);
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getMultiblocksListHeaderColor());
        renderer.fillRect(0, gui.getHeight()/16, gui.getWidth()/3, gui.getHeight()/8);
        renderer.setColor(Core.theme.getComponentTextColor(0));
        renderer.drawCenteredText(0, gui.getHeight()/16, gui.getWidth()/3-gui.getHeight()/16, gui.getHeight()/8, "Multiblocks");
    }
    @Override
    public void render3d(double deltaTime){
        super.render3d(deltaTime);
        if(glfwGetKey(Core.window, GLFW_KEY_LEFT)==GLFW_PRESS)xRot-=deltaTime*40;
        if(glfwGetKey(Core.window, GLFW_KEY_RIGHT)==GLFW_PRESS)xRot+=deltaTime*40;
        if(glfwGetKey(Core.window, GLFW_KEY_UP)==GLFW_PRESS)yRot = MathUtil.min(maxYRot, MathUtil.max(-maxYRot, yRot-=deltaTime*40));
        if(glfwGetKey(Core.window, GLFW_KEY_DOWN)==GLFW_PRESS)yRot = MathUtil.min(maxYRot, MathUtil.max(-maxYRot, yRot+=deltaTime*40));
        Renderer renderer = new Renderer();
        Multiblock mb = ((MenuMain)gui.menu).getSelectedMultiblock();
        if(mb!=null){
            BoundingBox bbox = mb.getBoundingBox();
            float size = MathUtil.max(bbox.getWidth(), MathUtil.max(bbox.getHeight(), bbox.getDepth()));
            size/=mb.get3DPreviewScale();
            renderer.setModel(new Matrix4f().setTranslation(.4f, 0, -1.5f)
                    .rotate((float)MathUtil.toRadians(yRot), 1, 0, 0)
                    .rotate((float)MathUtil.toRadians(xRot), 0, 1, 0)
                    .scale(1/size, 1/size, 1/size)
                    .translate(-bbox.getWidth()/2f, -bbox.getHeight()/2f, -bbox.getDepth()/2f));
            mb.draw3D();
            renderer.resetModelMatrix();
        }
    }
    @Override
    public void render2d(double deltaTime){
        if(refreshNeeded){
            refresh();
            components.removeAll(multiblockButtons);
            multiblockButtons.clear();
            for(Multiblock m : Core.multiblockTypes){
                String tex = m.getPreviewTexture();
                Button button = add(new Button(0, 0, 0, 0, m.getDefinitionName(), true, true){
                    @Override
                    public void drawText(Renderer renderer, double deltaTime){
                        if(tex!=null){
                            String text = this.text;
                            float textLength = renderer.getStringWidth(text, height);
                            float scale = Math.min(1, (width-textInset*2)/textLength);
                            float textHeight = (int)((height-textInset*2)*scale)-4;
                            textHeight = Math.min(textHeight, height/8);
                            renderer.drawCenteredText(x, y+height-height/16-textHeight/2, x+width, y+height-height/16+textHeight/2, text);
                            renderer.setWhite();
                            renderer.drawImage(TextureManager.getImage(tex), x+width/16, y, x+width-width/16, y+height-height/8);
                        }
                        else super.drawText(renderer, deltaTime);
                    }
                }.setTooltip(m.getDescriptionTooltip()));
                button.addAction(() -> {
                    Multiblock mb = m.newInstance();
                    mb.init();
                    Core.multiblocks.add(mb);
                    adding = false;
                    refresh();
                });
                multiblockButtons.add(button);
            }
            refreshNeeded = false;
        }
        if(adding)addingScale = Math.min(addingScale+deltaTime*20, addingTime);
        else addingScale = Math.max(addingScale-deltaTime*20, 0);
        if(metadating)metadatingScale = Math.min(metadatingScale+deltaTime*20, metadatingTime);
        else metadatingScale = Math.max(metadatingScale-deltaTime*20, 0);
        Renderer renderer = new Renderer();
        if(Core.recoveryMode){
            float size = gui.getHeight()/16f;
            int colorIndex = 0;
            for(float f = 0; f<gui.getWidth()+size; f+=size/2){
                renderer.setColor(Core.theme.getRecoveryModeColor(colorIndex));
                renderer.drawScreenQuad(
                        f, gui.getHeight()-size,
                        f-size, gui.getHeight(),
                        f-size/2, gui.getHeight(),
                        f+size/2, gui.getHeight()-size, 1, 0, 0, 0, 0, 0, 0, 0, 0);
                colorIndex++;
            }
            renderer.setColor(Core.theme.getRecoveryModeTextColor());
            renderer.drawCenteredText(0, gui.getHeight()-size*2, gui.getWidth(), gui.getHeight()-size, "RECOVERY MODE ENABLED. PRESS CTRL+SHIFT+R TO DISABLE");
        }
        if(settingInputs!=null)multiblocks.setSelectedIndex(Core.multiblocks.indexOf(settingInputs));
        if(!pendingWrites.isEmpty()){
            pendingWrites.dequeue().write();
        }
        convertOverhaulMSFR.x = setInputs.x = editMetadata.x = gui.getWidth()/3;
        exportMain.width = importFile.width = exportMultiblock.width = saveFile.width = loadFile.width = gui.getWidth()/12;
        exportMultiblock.x = importFile.width;
        saveFile.x = exportMultiblock.x+exportMultiblock.width;
        loadFile.x = saveFile.x+saveFile.width;
        editMetadata.width = gui.getWidth()*2/3-gui.getHeight()/16*(Core.vr?2:1);
        importFile.height = exportMultiblock.preferredHeight = saveFile.height = loadFile.height = editMetadata.height = vr.width = vr.height = settings.width = settings.height = gui.getHeight()/16;
        settings.x = gui.getWidth()-gui.getHeight()/16;
        vr.x = settings.x-gui.getHeight()/16;
        multiblocks.y = gui.getHeight()/8;
        convertOverhaulMSFR.height = setInputs.height = delete.height = addMultiblock.height;
        if(multiblocks.getSelectedIndex()==-1)delete.height = 0;
        multiblocks.height = gui.getHeight()-multiblocks.y-delete.height;
        delete.width = multiblocks.width = gui.getWidth()/3;
        for(Component c : multiblocks.components){
            c.width = multiblocks.width-(multiblocks.hasVertScrollbar()?multiblocks.vertScrollbarWidth:0);
            ((MenuComponentMultiblock) c).edit.enabled = ((MenuComponentMultiblock) c).multiblock.exists()&&(!(adding||metadating));
        }
        addMultiblock.x = gui.getWidth()/3-gui.getHeight()/16;
        addMultiblock.y = gui.getHeight()/16;
        addMultiblock.width = addMultiblock.height = gui.getHeight()/16;
        convertOverhaulMSFR.width = setInputs.width = editMetadata.width+settings.width;
        setInputs.y = convertOverhaulMSFR.y = addMultiblock.y;
        if(getSelectedMultiblock() instanceof OverhaulSFR){
            convertOverhaulMSFR.enabled = Core.configuration.overhaul!=null&&Core.configuration.overhaul.fissionMSR!=null&&!(adding||metadating)&&Core.isControlPressed();
            convertOverhaulMSFR.text = "Convert to MSR (Hold Control)";
            convertOverhaulMSFR.setTooltip("Convert the currently selected multiblock to an Overhaul MSR\nWARNING: All fuels will be converted to their Flouride counterparts");
        }else if(getSelectedMultiblock() instanceof OverhaulMSR){
            convertOverhaulMSFR.enabled = Core.configuration.overhaul!=null&&Core.configuration.overhaul.fissionSFR!=null&&!(adding||metadating)&&Core.isControlPressed();
            convertOverhaulMSFR.text = "Convert to SFR (Hold Control)";
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
        delete.y = gui.getHeight()-delete.height;
        credits.y = gui.getHeight()-credits.height;
        credits.x = gui.getWidth()-credits.width;
        addMultiblock.enabled = !(adding||metadating);
        editMetadata.enabled = !(adding||metadating);
        settings.enabled = !(adding||metadating);
        vr.enabled = !(adding||metadating);
        importFile.enabled = !(adding||metadating);
        exportMain.enabled = !(adding||metadating)&&multiblocks.getSelectedIndex()!=-1;
        for(Component c : exportMultiblock.components){
            if(c instanceof Button)((Button)c).enabled = !(adding||metadating)&&multiblocks.getSelectedIndex()!=-1;
        }
        saveFile.enabled = !Core.multiblocks.isEmpty()&&!(adding||metadating);
        loadFile.enabled = !(adding||metadating);
        delete.enabled = (!(adding||metadating)&&multiblocks.getSelectedIndex()!=-1)&&Core.isShiftPressed();
        for(Button b : multiblockButtons){
            b.enabled = adding&&Core.multiblockTypes.get(multiblockButtons.indexOf(b)).exists();
        }
        multiblockCancel.enabled = adding;
        metadataPanel.width = gui.getWidth()*.75f;
        metadataPanel.height = gui.getHeight()*.75f;
        metadataPanel.x = gui.getWidth()/2-metadataPanel.width/2;
        float addScale = (float)Math.min(1,Math.max(0,(adding?(addingScale+(deltaTime*20)):(addingScale-(deltaTime*20)))/addingTime));
        multiblockCancel.width = gui.getWidth()/3*addScale;
        multiblockCancel.height = gui.getHeight()/10*addScale;
        multiblockCancel.x = gui.getWidth()/2-multiblockCancel.width/2;
        multiblockCancel.y = gui.getHeight()-gui.getHeight()/8*1.5f-multiblockCancel.height/2;
        for(int i = 0; i<multiblockButtons.size(); i++){
            Button button = multiblockButtons.get(i);
            float midX = gui.getWidth()/(multiblockButtons.size()+1f)*(i+1);
            float midY = gui.getHeight()/2-multiblockCancel.height;
            button.width = button.height = gui.getWidth()/multiblockButtons.size()/2*addScale;
            button.x = midX-button.width/2;
            button.y = midY-button.height/2;
        }
        float metadataScale = (float)Math.min(1,Math.max(0,(metadating?(metadatingScale+(deltaTime*20)):(metadatingScale-(deltaTime*20)))/metadatingTime));
        metadataPanel.y = gui.getHeight()/2-metadataPanel.height/2-gui.getHeight()*(1-metadataScale);
        super.render2d(deltaTime);
        renderer.setColor(Core.theme.getRotateMultiblockTextColor(), .4f);
        if(getSelectedMultiblock()!=null)renderer.drawCenteredText(multiblocks.x+multiblocks.width, gui.getHeight()-45, credits.x, gui.getHeight()-5, "Use Arrow keys to rotate preview");
    }
    @Override
    public void onOpened(){
        refreshNeeded = true;
    }
    public void refresh(){
        multiblocks.components.clear();
        for(Multiblock multi : Core.multiblocks){
            MenuComponentMultiblock mcm = new MenuComponentMultiblock(this, multi);
            mcm.edit.addAction(() -> {
                gui.open(new MenuTransition(gui, this, new MenuEdit(gui, this, mcm.multiblock), MenuTransition.SplitTransitionX.slideIn((MenuEdit.partSize+MenuEdit.partSize/4f+MenuEdit.partsWide*MenuEdit.partSize)/gui.getWidth()), 5));
            });
            multiblocks.add(mcm);
        }
        String name = Core.metadata.containsKey("Name")?Core.metadata.get("Name"):"";
        editMetadata.text = name.isEmpty()?"Edit Metadata":(name+" | Edit Metadata");
    }
    public Multiblock getSelectedMultiblock(){
        if(multiblocks.getSelectedIndex()==-1)return null;
        return ((MenuComponentMultiblock)multiblocks.components.get(multiblocks.getSelectedIndex())).multiblock;
    }
    private void importMultiblocks(File file){
        NCPFFile ncpf = FileReader.read(file);
        if(ncpf==null)return;
        if(ncpf.configuration!=null&&!ncpf.configuration.name.equals(Core.configuration.name)){
            Core.warning("File configuration '"+ncpf.configuration.name+"' does not match currently loaded configuration '"+Core.configuration.name+"'!", null);
        }
        convertAndImportMultiblocks(ncpf.multiblocks);
    }
    private void convertAndImportMultiblocks(ArrayList<Multiblock> multiblocks){
        for(Multiblock mb : multiblocks){
            try{
                mb.convertTo(Core.configuration);
            }catch(MissingConfigurationEntryException ex){
                Core.warning("Failed to load multiblock - Are you missing an addon?", ex);
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
    public void onFilesDropped(String[] files){
        for(String fil : files){
            try{
                importMultiblocks(new File(fil));
            }catch(Exception ex){
                Core.error("Failed to load file "+fil+"!", ex);
            }
        }
        onOpened();
    }
    @Override
    public void onKeyEvent(int key, int scancode, int action, int mods){
        if(action==GLFW_PRESS&&key==GLFW_KEY_R&&Core.isControlPressed()&&Core.isShiftPressed()){
            Core.recoveryMode = !Core.recoveryMode;
        }
        super.onKeyEvent(key, scancode, action, mods);
    }
}