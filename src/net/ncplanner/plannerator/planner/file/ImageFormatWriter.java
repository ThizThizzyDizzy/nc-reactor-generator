package net.ncplanner.plannerator.planner.file;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.ImageIO;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.gui.menu.MenuImageExportPreview;
import net.ncplanner.plannerator.planner.module.Module;
public abstract class ImageFormatWriter extends FormatWriter{
    public ArrayList<EditorOverlay> overlays = new ArrayList<>();
    @Override
    public void write(NCPFFile ncpf, OutputStream stream){
        try{
            ImageIO.write(write(ncpf), stream);
            stream.close();
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    public abstract Image write(NCPFFile ncpf);
    @Override
    public void openExportSettings(NCPFFile ncpf, Runnable onExport){
        overlays.clear();
        for(Module m : Core.modules){
            if(m.isActive()){
                m.getEditorOverlays(ncpf.multiblocks.get(0), overlays);
            }
        }
        for(EditorOverlay o : overlays){
            if(!o.isActive())continue;
            o.refresh(ncpf.multiblocks.get(0));
        }
        Core.gui.open(new MenuImageExportPreview(Core.gui, Core.gui.menu, ()->{return write(ncpf);}, onExport, overlays, ncpf.multiblocks.get(0)));
    }
}