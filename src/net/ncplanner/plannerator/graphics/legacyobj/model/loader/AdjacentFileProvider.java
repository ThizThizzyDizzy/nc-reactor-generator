package net.ncplanner.plannerator.graphics.legacyobj.model.loader;
import java.io.InputStream;
@Deprecated
public interface AdjacentFileProvider{
    public InputStream getAdjacentFile(String name);
    public String getAdjacentFilepath(String name);
}