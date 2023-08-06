package net.ncplanner.plannerator.planner.gui.menu;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.Main;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.Queue;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.file.FileWriter;
import net.ncplanner.plannerator.planner.file.FormatWriter;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.DropdownList;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.MulticolumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.TextView;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentMultiblock;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuImportFile;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuImportFiles;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuInputDialog;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuLoad;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuMessageDialog;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuOKMessageDialog;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuSaveDialog;
import net.ncplanner.plannerator.planner.gui.menu.dssl.MenuDsslEditor;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.design.MultiblockDesign;
import net.ncplanner.plannerator.planner.vr.VRCore;
import org.joml.Matrix4f;
import static org.lwjgl.glfw.GLFW.*;
public class MenuMain extends Menu{
    private SingleColumnList multiblocks = add(new SingleColumnList(0, 0, 0, 0, 50));
    private Button addMultiblock = add(new Button("+", true).setTooltip("Add a new multiblock"));
    private Button importFile = add(new Button("Import", false).setTooltip("Import all multiblocks from a saved file"));
    private Button exportMain;
    private DropdownList exportMultiblock = add(new DropdownList(0, 0, 0, 0){
        {
            add(exportMain = new Button("Export", false).setTooltip("Export the selected multiblock to a file"));
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
    private Button saveFile = add(new Button("Save", false).setTooltip("Save all multiblocks to a file"));
    private Button loadFile = add(new Button("Load", false).setTooltip("Load a file, replacing all current multiblocks"));
    private Button editMetadata = add(new Button("", true).setTooltip("Edit metadata"));
    private Button vr = new Button("VR", true).setTooltip("Enter VR");
    private Button settings = add(new Button("", true){
        @Override
        public void drawText(Renderer renderer, double deltaTime){
            renderer.drawGear(x+width/2, y+height/2, width*.1f, 8, width*.3f, width*.1f, 360/16f);
        }
    }.setTooltip("Settings"));
    private Button buttonSidePanel = add(new Button("<", true));
    private Button delete = add(new Button("Delete Multiblock (Hold Shift)", true).setTextColor(Core.theme::getDeleteButtonTextColor).setTooltip("Delete the currently selected multiblock\nWARNING: This cannot be undone!"));
    private Button credits = add(new Button(0, 0, 192, 48, "Credits", true, true));
    private Button convertOverhaulMSFR = add(new Button("Convert SFR <> MSR", true).setTextColor(Core.theme::getConvertButtonTextColor));
    private Button setInputs = add(new Button("Set Inputs", true).setTextColor(Core.theme::getInputsButtonTextColor).setTooltip("Choose multiblocks to input Steam to this turbine\nYou can choose as many as you want"));
    private Component sidePanel = add(new Component(0, 0, 0, 0){
        Button changelog = add(new Button("Changelog", true){
            @Override
            public void drawText(Renderer renderer, double deltaTime){
                renderer.fillRect(x+width*.1f, y+height*.15f, x+width*.2f, y+height*.25f);
                renderer.fillRect(x+width*.1f, y+height*.45f, x+width*.2f, y+height*.55f);
                renderer.fillRect(x+width*.1f, y+height*.75f, x+width*.2f, y+height*.85f);
                renderer.fillRect(x+width*.3f, y+height*.15f, x+width*.9f, y+height*.25f);
                renderer.fillRect(x+width*.3f, y+height*.45f, x+width*.9f, y+height*.55f);
                renderer.fillRect(x+width*.3f, y+height*.75f, x+width*.9f, y+height*.85f);
            }
        });
        Button patreon = add(new Button("Supporters", true){
            @Override
            public void drawText(Renderer renderer, double deltaTime){
                //draw heart
                float margin = 0.1f;
                float spacing = 0.05f;
                float diagonalHeight = 0.5f-margin;
                float circleSize = 0.5f-margin-spacing/2;
                float k = (float)(Math.sqrt(2)/2);
                float totalHeight = (float)(diagonalHeight+circleSize*k);//should be less than width, so just assume it is
                float totalWidth = 1-margin*2;
                float yOff = (totalWidth-totalHeight)/4;
                //now actually draw the heart
                float circleY = y+height*(1-(margin+diagonalHeight+circleSize/2*k)-yOff);
                float diagTopY = y+height*(1-(margin+diagonalHeight)-yOff);
                renderer.drawCircle(x+width*(0.5f-spacing/2-circleSize/2), circleY, 0, circleSize/2*width);
                renderer.drawCircle(x+width*(0.5f+spacing/2+circleSize/2), circleY, 0, circleSize/2*width);
                renderer.fillTri(x+width*(margin+circleSize/2*(1-k)), diagTopY, x+width/2, y+height*(1-margin-yOff), x+width*(1-(margin+circleSize/2*(1-k))), diagTopY);
                //spacing infill
                if(spacing>0){
                    float cY = circleY-circleSize/2*k*height;
                    float size = diagTopY-cY;
                    float cx = x+width*(0.5f+spacing/2+circleSize/2*(1-k));
                    renderer.fillTri(cx, cY, cx-size, cY+size, cx+size, cY+size);
                    cx = x+width*(0.5f-spacing/2-circleSize/2*(1-k));
                    renderer.fillTri(cx, cY, cx-size, cY+size, cx+size, cY+size);
                }
            }
        });
        Label header = add(new Label(0, 0, 0, 0, "", true));
        TextView body = add(new TextView(0, 0, 0, 0, 0, 10){
            @Override
            public void onMouseButton(double x, double y, int button, int action, int mods){
                super.onMouseButton(x, y, button, action, mods);
                if(button==0&&action==GLFW_PRESS)clickity(tab);
            }
        });
        ArrayList<Button> buttons = new ArrayList<>();
        int tab = 0;
        {
            buttons.add(changelog);
            buttons.add(patreon);
            for(int i = 0; i<buttons.size(); i++){
                int I = i;
                Button b = buttons.get(i);
                b.addAction(() -> {
                    tab(I);
                });
            }
        }
        private void tab(int i){
            tab = i;
            header.text = buttons.get(i).text;
            body.setText(getText(i));
            body.snap = i-1;//Dear future thiz: This was a bad idea.
        }
        {
            tab(0);
        }
        @Override
        public void render2d(double deltaTime){
            float size = gui.getHeight()/16;
            for(int i = 0; i<buttons.size(); i++){
                Button b = buttons.get(i);
                b.enabled = !(adding||metadating||tab==i);
                b.x = 0;
                b.width = b.height = size;
                b.y = i*size;
            }
            header.width = body.width = width-size;
            body.height = height-size;
            body.x = body.y = header.x = header.height = size;
            if(!sidePanelOpen&&sidePanelScale<=0)return;
            super.render2d(deltaTime);
        }
        private FormattedText getText(int i){
            switch(i){
                case 0:
                    return new FormattedText(MenuMain.changelog);
                case 1:
                    FormattedText t = new FormattedText("\nThank you to my patrons:");
                    t.addText(" ");
                    for(String s : MenuCredits.patrons){
                        t.addText(s);
                    }
                    t.addText("\nIf you enjoy this plannerator, consider supporting me on patreon or ko-fi:\npatreon.com/thizthizzydizzy\nko-fi.com/thizthizzydizzy\n\nJoin my discord server:\ndiscord.gg/dhcPSMt");
                    return t;
                default:
                    return new FormattedText("Hey thiz, you forgot to add text for this tab");
            }
        }
        private void clickity(int i){
            if(i==1){
                new MenuMessageDialog(gui, MenuMain.this, "Open a link? (Will open in default browser)")
                        .addButton("Patreon", ()->{Core.openURL("https://patreon.com/thizthizzydizzy");}, true)
                        .addButton("Ko-Fi", ()->{Core.openURL("https://ko-fi.com/thizthizzydizzy");}, true)
                        .addButton("Discord", ()->{Core.openURL("https://discord.gg/dhcPSMt");}, true)
                        .addButton("Cancel", true).open();
            }
        }
    });
    private boolean forceMetaUpdate = true;
    private Component metadataPanel = add(new Component(0, 0, 0, 0){
        MulticolumnList list = add(new MulticolumnList(0, 0, 0, 0, 0, 50, 50));
        Button done = add(new Button("Done", true).setTooltip("Finish editing metadata"));
        Button stack = add(new Button("S'tack", true, true).setTooltip("What's this doing here?"));
        {
            done.addAction(() -> {
                Core.resetMetadata();
                for(int i = 0; i<list.components.size(); i+=2){
                    TextBox key = (TextBox) list.components.get(i);
                    TextBox value = (TextBox) list.components.get(i+1);
                    if(key.text.trim().isEmpty()&&value.text.trim().isEmpty())continue;
                    Core.project.metadata.put(key.text, value.text);
                }
                Core.saved = false;
                metadating = false;
                refresh();
            });
            stack.addAction(() -> {
                if(enables)gui.open(new MenuDsslEditor(gui, MenuMain.this));
            });
        }
        @Override
        public void render2d(double deltaTime){
            if(forceMetaUpdate){
                forceMetaUpdate = false;
                list.components.clear();
                for(String key : Core.project.metadata.keys()){
                    String value = Core.project.metadata.get(key);
                    list.add(new TextBox(0,0,0,0,key, true));
                    list.add(new TextBox(0,0,0,0,value, true));
                }
            }
            if(!metadating&&metadatingScale<=0)return;
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
            Core.dssl |= enables;
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
    private Button multiblockCancel = add(new Button("Cancel", true, true));
    public static boolean enables = false;
    private boolean adding = false;
    private double addingScale = 0;
    private final int addingTime = 3;
    private boolean metadating = false;
    private double metadatingScale = 0;
    private final int metadatingTime = 4;
    private boolean sidePanelOpen = Main.justUpdated;
    private double sidePanelScale = 0;
    private final int sidePanelTime = 3;
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
            new MenuSaveDialog(gui, this).open();
        });
        loadFile.addAction(new MenuLoad(gui, this).onClose(this::onOpened)::open);
        importFile.addAction(new MenuImportFile(gui, this, this::onOpened)::open);
        for(FormatWriter writer : FileWriter.formats){
            FileFormat format = writer.getFileFormat();
            exportMultiblock.add(new Button(format.name, true, true).setTooltip(format.description)).addAction(() -> {
                exportMultiblock.isDown = false;
                exportMultiblock.isFocused = false;
                focusedComponent = null;
                Project ncpf = new Project();//TODO set configuration
                Multiblock multi = getSelectedMultiblock();
                ncpf.designs.add(multi.toDesign());
                String name = Core.filename;
//                if(name==null) name = ncpf.metadata.get("name");
                if(name==null||name.isEmpty()){
                    name = "unnamed";
                    File file = new File(name+"."+format.extensions[0]);
                    int i = 0;
                    while(file.exists()){
                        name = "unnamed_"+i;
                        file = new File(name+"."+format.extensions[0]);
                        i++;
                    }
                }
                String nam = name;
                Runnable r = () -> {
                    new MenuInputDialog(gui, gui.menu, nam, "Filename").addButton("Cancel", true).addButton("Save Dialog", () -> {
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
                    }, true).addButton("Save", (dialog, filename) -> {
                        if(filename==null||filename.isEmpty()){
                            Core.warning("Invalid filename: "+filename+"."+format.extensions[0], null);
                        }else{
                            Core.filename = filename;
                            File file = new File(filename+"."+format.extensions[0]);
                            if(file.exists())new MenuMessageDialog(gui, dialog, "File "+filename+"."+format.extensions[0]+" already exists!\nOverwrite?").addButton("Cancel", true).addButton("Save", () -> imprt(ncpf, writer, file, filename+"."+format.extensions[0]), true).open();
                            else imprt(ncpf, writer, file, filename+"."+format.extensions[0]);
                        }
                    }).open();
                };
                writer.openExportSettings(ncpf, r);
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
        buttonSidePanel.addAction(()->{
            sidePanelOpen = !sidePanelOpen;
            buttonSidePanel.text = sidePanelOpen?">":"<";
        });
        delete.addAction(() -> {
            Multiblock multiblock = Core.multiblocks.get(multiblocks.getSelectedIndex());
            for(Multiblock m : Core.multiblocks){
                if(m instanceof OverhaulTurbine){
                    ((OverhaulTurbine)m).inputs.remove(multiblock);
                }
            }
            Core.multiblocks.remove(multiblock);
            Core.saved = false;
            onOpened();
        });
        convertOverhaulMSFR.addAction(() -> {
            Multiblock selected = getSelectedMultiblock();
            try{
                if(selected instanceof OverhaulSFR){
                    OverhaulMSR msr = ((OverhaulSFR) selected).convertToMSR();
                    Core.multiblocks.set(Core.multiblocks.indexOf(selected), msr);
                    Core.saved = false;
                }else if(selected instanceof OverhaulMSR){
                    OverhaulSFR sfr = ((OverhaulMSR) selected).convertToSFR();
                    Core.multiblocks.set(Core.multiblocks.indexOf(selected), sfr);
                    Core.saved = false;
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
    public static String changelog = "";
    static{
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Core.getInputStream("changelog.txt")))){
            String line;
            while((line = reader.readLine())!=null){
                changelog+=line+"\n";
            }
        }catch(IOException ex){
            changelog = ex.getClass().getName()+": "+ex.getMessage();
        }
    }
    private void imprt(Project project, FormatWriter writer, File file, String filename){
        FileWriter.write(project, file, writer);
        new MenuOKMessageDialog(gui, this, "Saved as "+filename).open();
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
            renderer.pushModel(new Matrix4f().setTranslation(.4f, 0, -1.5f)
                    .rotate((float)MathUtil.toRadians(yRot), 1, 0, 0)
                    .rotate((float)MathUtil.toRadians(xRot), 0, 1, 0)
                    .scale(1/size, 1/size, 1/size)
                    .translate(-bbox.getWidth()/2f, -bbox.getHeight()/2f, -bbox.getDepth()/2f));
            mb.draw3D();
            renderer.popModel();
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
                Button button = add(new Button(m.getDefinitionName(), true, true){
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
                    Core.saved = false;
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
        if(sidePanelOpen)sidePanelScale = Math.min(sidePanelScale+deltaTime*20, sidePanelTime);
        else sidePanelScale = Math.max(sidePanelScale-deltaTime*20, 0);
        Renderer renderer = new Renderer();
        if(Core.recoveryMode){
            float size = gui.getHeight()/16f;
            int colorIndex = 0;
            for(float f = 0; f<gui.getWidth()+size; f+=size/2){
                renderer.setColor(Core.theme.getRecoveryModeColor(colorIndex));
                renderer.drawScreenQuad(
                        f-size, gui.getHeight(),
                        f, gui.getHeight()-size,
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
        importFile.height = exportMultiblock.preferredHeight = saveFile.height = loadFile.height = editMetadata.height = vr.width = vr.height = settings.width = settings.height = buttonSidePanel.width = buttonSidePanel.height = buttonSidePanel.y = gui.getHeight()/16;
        buttonSidePanel.x = settings.x = gui.getWidth()-gui.getHeight()/16;
        vr.x = settings.x-gui.getHeight()/16;
        multiblocks.y = gui.getHeight()/8;
        convertOverhaulMSFR.height = setInputs.height = delete.height = addMultiblock.height;
        if(multiblocks.getSelectedIndex()==-1)delete.height = 0;
        multiblocks.height = gui.getHeight()-multiblocks.y-delete.height;
        delete.y = gui.getHeight()-delete.height;
        delete.height = addMultiblock.height;
        delete.width = multiblocks.width = gui.getWidth()/3;
        for(Component c : multiblocks.components){
            c.width = multiblocks.width-(multiblocks.hasVertScrollbar()?multiblocks.vertScrollbarWidth:0);
            ((MenuComponentMultiblock) c).edit.enabled = ((MenuComponentMultiblock) c).multiblock.exists()&&(!(adding||metadating));
        }
        addMultiblock.x = gui.getWidth()/3-gui.getHeight()/16;
        addMultiblock.y = gui.getHeight()/16;
        addMultiblock.width = addMultiblock.height = gui.getHeight()/16;
        convertOverhaulMSFR.width = setInputs.width = editMetadata.width+(Core.vr?vr.width:0);
        setInputs.y = convertOverhaulMSFR.y = addMultiblock.y;
        if(getSelectedMultiblock() instanceof OverhaulSFR){
            convertOverhaulMSFR.enabled = Core.project.conglomeration.hasConfiguration(OverhaulMSRConfiguration::new)&&!(adding||metadating)&&Core.isControlPressed();
            convertOverhaulMSFR.text = "Convert to MSR (Hold Control)";
            convertOverhaulMSFR.setTooltip("Convert the currently selected multiblock to an Overhaul MSR\nWARNING: All fuels will be converted to their Flouride counterparts");
        }else if(getSelectedMultiblock() instanceof OverhaulMSR){
            convertOverhaulMSFR.enabled = Core.project.conglomeration.hasConfiguration(OverhaulSFRConfiguration::new)&&!(adding||metadating)&&Core.isControlPressed();
            convertOverhaulMSFR.text = "Convert to SFR (Hold Control)";
            convertOverhaulMSFR.setTooltip("Convert the currently selected multiblock to an Overhaul SFR\nWARNING: All fuels will be converted to their Oxide counterparts");
        }else{
            convertOverhaulMSFR.enabled = false;
            convertOverhaulMSFR.y = -convertOverhaulMSFR.height*100;
            convertOverhaulMSFR.width = 0;
        }
        if(getSelectedMultiblock() instanceof OverhaulTurbine){
            setInputs.enabled = true;
        }else{
            setInputs.enabled = false;
            setInputs.y = -setInputs.height*100;
            setInputs.width = 0;
        }
        credits.y = gui.getHeight()-credits.height;
        credits.x = gui.getWidth()-credits.width;
        addMultiblock.enabled = !(adding||metadating);
        editMetadata.enabled = !(adding||metadating);
        settings.enabled = !(adding||metadating);
        buttonSidePanel.enabled = !(adding||metadating);
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
        sidePanel.y = buttonSidePanel.y+buttonSidePanel.height;
        sidePanel.width = editMetadata.width/2;
        sidePanel.height = credits.y-sidePanel.y;
        float metadataScale = (float)Math.min(1,Math.max(0,(metadating?(metadatingScale+(deltaTime*20)):(metadatingScale-(deltaTime*20)))/metadatingTime));
        metadataPanel.y = gui.getHeight()/2-metadataPanel.height/2-gui.getHeight()*(1-metadataScale);
        float sidePaneScale = (float)Math.min(1,Math.max(0,(sidePanelOpen?(sidePanelScale+(deltaTime*20)):(sidePanelScale-(deltaTime*20)))/sidePanelTime));
        sidePanel.x = gui.getWidth()-sidePanel.width*sidePaneScale;
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
                Core.saved = false;
                gui.open(new MenuTransition(gui, this, new MenuEdit(gui, this, mcm.multiblock), MenuTransition.SplitTransitionX.slideIn((MenuEdit.partSize+MenuEdit.partSize/4f+MenuEdit.partsWide*MenuEdit.partSize)/gui.getWidth()), 5));
            });
            multiblocks.add(mcm);
        }
        String name = Core.project.metadata.contains("Name")?Core.project.metadata.get("Name"):"";
        editMetadata.text = name.isEmpty()?"Edit Metadata":(name+" | Edit Metadata");
    }
    public Multiblock getSelectedMultiblock(){
        if(multiblocks.getSelectedIndex()==-1)return null;
        return ((MenuComponentMultiblock)multiblocks.components.get(multiblocks.getSelectedIndex())).multiblock;
    }
    private static class PendingWrite{
        private final Project ncpf;
        private final File file;
        private final FormatWriter writer;
        private PendingWrite(Project ncpf, File file, FormatWriter writer){
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
        ArrayList<File> toImport = new ArrayList<>();
        for(String fil : files){
            if((fil.endsWith(".dssl")||fil.endsWith(".essl"))&&Core.dssl){
                gui.open(new MenuDsslEditor(gui, this));
                gui.menu.onFilesDropped(files);
                return;
            }
            toImport.add(new File(fil));
        }
        if(!toImport.isEmpty()){
            new MenuImportFiles(gui, this, toImport, this::onOpened).open();
        }
    }
    @Override
    public void onKeyEvent(int key, int scancode, int action, int mods){
        if(action==GLFW_PRESS&&key==GLFW_KEY_R&&Core.isControlPressed()&&Core.isShiftPressed()){
            Core.recoveryMode = !Core.recoveryMode;
        }
        if(action==GLFW_PRESS&&key==GLFW_KEY_K&&Core.isControlPressed()&&Core.isShiftPressed()){
            gui.open(new MenuCalibrateCursor(gui, this));
        }
        super.onKeyEvent(key, scancode, action, mods);
    }
}