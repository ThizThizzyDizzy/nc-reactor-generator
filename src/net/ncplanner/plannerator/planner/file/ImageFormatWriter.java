package net.ncplanner.plannerator.planner.file;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.ImageIO;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.gui.menu.MenuImageExportPreview;
import net.ncplanner.plannerator.planner.module.Module;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.design.MultiblockDesign;
public abstract class ImageFormatWriter extends FormatWriter{
    public ArrayList<EditorOverlay> overlays = new ArrayList<>();
    @Override
    public void write(Project ncpf, OutputStream stream){
        try{
            ImageIO.write(write(ncpf), stream);
            stream.close();
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    public abstract Image write(Project ncpf);
    @Override
    public void openExportSettings(Project ncpf, Runnable onExport){
        overlays.clear();
        Multiblock mb = null;
        for(Module m : Core.modules){
            if(m.isActive()){
                Design d = ncpf.designs.get(0);
                if(d instanceof MultiblockDesign){
                    mb = ((MultiblockDesign)d).toMultiblock();
                    m.getEditorOverlays(mb, overlays);
                }
            }
        }
        for(EditorOverlay o : overlays){
            if(!o.isActive())continue;
            o.refresh(mb);
        }
        Core.gui.open(new MenuImageExportPreview(Core.gui, Core.gui.menu, ()->{return write(ncpf);}, onExport, overlays, mb));
    }
}