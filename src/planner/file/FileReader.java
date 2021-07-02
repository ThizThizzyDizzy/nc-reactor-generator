package planner.file;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import planner.file.reader.*;
public class FileReader{
    public static final ArrayList<FormatReader> formats = new ArrayList<>();
    static{
        formats.add(new UnderhaulNCConfigReader());// UNDERHAUL nuclearcraft.cfg
        formats.add(new OverhaulNCConfigReader());// OVERHAUL nuclearcraft.cfg
        formats.add(new UnderhaulHellrage1Reader());// hellrage .json 1.2.5-1.2.22
        formats.add(new UnderhaulHellrage2Reader());// hellrage .json 1.2.23-1.2.25 (present)
        formats.add(new OverhaulHellrageSFR1Reader());// hellrage SFR .json 2.0.1-2.0.6
        formats.add(new OverhaulHellrageSFR2Reader());// hellrage SFR .json 2.0.7-2.0.29
        formats.add(new OverhaulHellrageSFR3Reader());// hellrage SFR .json 2.0.30
        formats.add(new OverhaulHellrageSFR4Reader());// hellrage SFR .json 2.0.31
        formats.add(new OverhaulHellrageSFR5Reader());// hellrage SFR .json 2.0.32-2.0.37
        formats.add(new OverhaulHellrageSFR6Reader());// hellrage SFR .json 2.1.1-2.1.7 (present)
        formats.add(new OverhaulHellrageMSR1Reader());// hellrage MSR .json 2.0.1-2.0.6
        formats.add(new OverhaulHellrageMSR2Reader());// hellrage MSR .json 2.0.7-2.0.29
        formats.add(new OverhaulHellrageMSR3Reader());// hellrage MSR .json 2.0.30
        formats.add(new OverhaulHellrageMSR4Reader());// hellrage MSR .json 2.0.31
        formats.add(new OverhaulHellrageMSR5Reader());// hellrage MSR .json 2.0.32-2.0.37
        formats.add(new OverhaulHellrageMSR6Reader());// hellrage MSR .json 2.1.1-2.1.7 (present)
        formats.add(new NCPF1Reader());// .ncpf version 1
        formats.add(new NCPF2Reader());// .ncpf version 2
        formats.add(new NCPF3Reader());// .ncpf version 3
        formats.add(new NCPF4Reader());// .ncpf version 4
        formats.add(new NCPF5Reader());// .ncpf version 5
        formats.add(new NCPF6Reader());// .ncpf version 6
        formats.add(new NCPF7Reader());// .ncpf version 7
        formats.add(new NCPF8Reader());// .ncpf version 8
        formats.add(new NCPF9Reader());// .ncpf version 9
        formats.add(new NCPF10Reader());// .ncpf version 10
        formats.add(new NCPF11Reader());// .ncpf version 11
    }
    public static NCPFFile read(InputStreamProvider provider){
        for(FormatReader reader : formats){
            boolean matches = false;
            try{
                if(reader.formatMatches(provider.getInputStream()))matches = true;
            }catch(Throwable t){}
            if(matches)return reader.read(provider.getInputStream());
        }
        throw new IllegalArgumentException("Unknown file format!");
    }
    public static NCPFFile read(File file){
        return read(() -> {
            try{
                return new FileInputStream(file);
            }catch(FileNotFoundException ex){
                return null;
            }
        });
    }
}