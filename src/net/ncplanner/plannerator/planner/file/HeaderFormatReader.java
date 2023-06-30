package net.ncplanner.plannerator.planner.file;
import java.io.InputStream;
public interface HeaderFormatReader extends FormatReader{
    public LegacyNCPFHeader readHeader(InputStream stream);
}