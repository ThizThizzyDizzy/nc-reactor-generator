package planner.file.reader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import multiblock.CuboidalMultiblock;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import multiblock.configuration.PartialConfiguration;
import multiblock.configuration.overhaul.OverhaulConfiguration;
import multiblock.configuration.underhaul.UnderhaulConfiguration;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import multiblock.overhaul.turbine.OverhaulTurbine;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.file.FormatReader;
import planner.file.NCPFFile;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
import simplelibrary.image.Image;
public class NCPF8Reader extends NCPF9Reader {
    protected byte getTargetVersion() {
        return (byte) 8;
    }

    protected int parseInputRate(Config blockCfg) {
        return blockCfg.hasProperty("input") ? 1 : 0;
    }
    protected int parseOutputRate(Config blockCfg) {
        return blockCfg.hasProperty("output") ? 1 : 0;
    }
}