package net.ncplanner.plannerator.planner.file.ncpf;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.ncplanner.ncpf.io.NcpfJsonConverter;
import net.ncplanner.ncpf.structure.NcpfRoot;
import net.ncplanner.ncpf.structure.element.NcpfElement;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.file.FileWriter;
import net.ncplanner.plannerator.planner.file.reader.NCPFReader;
import net.ncplanner.plannerator.planner.file.writer.NCPFWriter;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.module.FusionTestModule;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.module.UnderhaulModule;
import net.ncplanner.plannerator.planner.ncpf.Project;

/** Standalone regression runner. Compares JSON structure and values, never formatting. */
public final class ModernNcpfTest{
    private static final JSONNCPFReader READER = new JSONNCPFReader();
    private static final JSONNCPFWriter WRITER = new JSONNCPFWriter();
    private static int passed;
    private static final java.util.TreeSet<String> changes = new java.util.TreeSet<>();
    public static void main(String[] args) throws Exception{
        // The corpus verifies exact encoded texture preservation without repeatedly decoding PNGs.
        System.setProperty("plannerator.skipTextures", "true");
        new CoreModule().registerNCPF();
        new UnderhaulModule().registerNCPF();
        new OverhaulModule().registerNCPF();
        new FusionTestModule().registerNCPF();
        FileReader.formats.add(new NCPFReader());
        Core.project = NcpfBridge.toProject(parse(Files.readString(Paths.get("src/configurations/nuclearcraft.ncpf.json"))));
        List<Path> documents = new ArrayList<>();
        try(Stream<Path> paths = Files.walk(Paths.get("src/configurations"))){
            paths.filter(p -> p.toString().endsWith(".ncpf.json")).sorted().forEach(documents::add);
        }
        documents.add(Paths.get("test/files/ncpf/detailed-reactor.ncpf.json"));
        documents.add(Paths.get("test/files/ncpf/generated-distiller.ncpf.json"));
        // Also exercise local saved projects when present.
        for(String filename : new String[]{"TEST REACTOR.ncpf.json", "project.ncpf.json", "nuclearcraft-withdistiller.ncpf.json", "overhaul.ncpf.json", "underhaul.ncpf.json"}){
            Path path = Paths.get(filename);
            if(Files.exists(path))documents.add(path);
        }
        if(args.length>0)for(String arg : args)documents.add(Paths.get(arg));
        for(Path path : documents){
            try{roundTrip(Files.readString(path), path.toString());}
            catch(Throwable failure){throw new AssertionError("Failed document: "+path, failure);}
        }
        equal(JsonParser.parseString("[[[1,2,3,4]]]"), NcpfJsonConverter.gson.toJsonTree(new int[][][]{{{1,2,3,4}}}), "single-row arrays remain nested");
        equal(JsonParser.parseString("[[[1,2,3,4]]]"), NcpfJsonConverter.gson.toJsonTree(NcpfJsonConverter.gson.fromJson("[1,2,3,4]", int[][][].class)), "flat input is written nested");
        allDesignFamilies();
        detailedExtensions();
        decodedTexture();
        edits();
        cannedConfigurations();
        exportsAndFailures();
        invalidDocuments();
        Files.write(Paths.get("/tmp/modern-ncpf-conversion-changes.txt"), changes);
        System.out.println("PASS: "+passed+" modern NCPF end-to-end documents plus edits, exports, and failure checks");
    }
    private static NcpfRoot parse(String json){
        return NcpfJsonConverter.parseJson(new JsonReader(new StringReader(json)));
    }
    private static JsonElement write(NcpfRoot root) throws IOException{
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        WRITER.write(root, stream);
        return JsonParser.parseString(stream.toString(StandardCharsets.UTF_8));
    }
    private static JsonElement save(Project project){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NCPFFileWriter.write(project, stream, WRITER);
        return JsonParser.parseString(stream.toString(StandardCharsets.UTF_8));
    }
    private static Project load(String json){
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return FileReader.read(() -> new ByteArrayInputStream(bytes));
    }
    private static void roundTrip(String json, String label) throws Exception{
        JsonElement original = JsonParser.parseString(json);
        JsonElement expected = write(READER.read(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        equal(expected, write(parse(expected.toString())), label+" library second cycle");
        Project project = load(json); // Includes FileReader's copyTo(Project::new).
        JsonElement applicationExpected = save(project);
        if(original.isJsonObject()&&original.getAsJsonObject().has("modules")&&original.getAsJsonObject().getAsJsonObject("modules").has("nuclearcraft:generated")){
            if(applicationExpected.getAsJsonObject().getAsJsonObject("modules").has("nuclearcraft:generated"))throw new AssertionError("generated marker not consumed");
        }else equal(save(NcpfBridge.toProject(parse(json))), applicationExpected, label+" bridge and application agreement");
        checkDesignArrays(original, applicationExpected, label);
        recordChanges(original, applicationExpected, label, "$", changes);
        equal(applicationExpected, save(project), label+" application IO");
        equal(applicationExpected, save(project.copyTo(Project::new)), label+" copied project");
        equal(applicationExpected, save(load(save(project).toString())), label+" second cycle");
        Path file = Files.createTempFile("modern-ncpf-test-", ".ncpf.json");
        try{
            NCPFFileWriter.write(project, file.toFile(), WRITER);
            equal(applicationExpected, JsonParser.parseString(Files.readString(file)), label+" file save");
            equal(applicationExpected, save(FileReader.read(file.toFile())), label+" file load");
            NcpfJsonConverter.writeJson(parse(json), file.toFile());
            equal(expected, JsonParser.parseString(Files.readString(file)), label+" library file writer");
            equal(expected, write(NcpfJsonConverter.parseJson(file.toFile())), label+" library file reader");
        }finally{Files.deleteIfExists(file);}
        passed++;
        System.out.println("PASS document: "+label);
    }
    private static void allDesignFamilies() throws Exception{
        JsonObject doc = JsonParser.parseString(Files.readString(Paths.get("test/files/ncpf/detailed-reactor.ncpf.json"))).getAsJsonObject();
        JsonObject configurations=doc.getAsJsonObject("configuration");
        JsonObject nuclearcraft=JsonParser.parseString(Files.readString(Paths.get("src/configurations/nuclearcraft.ncpf.json"))).getAsJsonObject();
        configurations.add("nuclearcraft:overhaul_turbine", nuclearcraft.getAsJsonObject("configuration").get("nuclearcraft:overhaul_turbine"));
        JsonObject distiller=JsonParser.parseString(Files.readString(Paths.get("test/files/ncpf/generated-distiller.ncpf.json"))).getAsJsonObject();
        configurations.add("nuclearcraft:overhaul_distiller", distiller.getAsJsonObject("configuration").get("nuclearcraft:overhaul_distiller"));
        JsonObject fusion=JsonParser.parseString(Files.readString(Paths.get("src/configurations/fusion_test.ncpf.json"))).getAsJsonObject();
        configurations.add("plannerator:fusion_test", fusion.getAsJsonObject("configuration").get("plannerator:fusion_test"));
        JsonArray designs=doc.getAsJsonArray("designs");
        for(String type : new String[]{"nuclearcraft:underhaul_sfr", "nuclearcraft:overhaul_msr", "nuclearcraft:overhaul_turbine", "nuclearcraft:overhaul_distiller", "plannerator:fusion_test"}){
            JsonObject design=new JsonObject();
            design.addProperty("type", type);
            design.add("dimensions", JsonParser.parseString(type.equals("plannerator:fusion_test")?"[1,1,1,1]":"[3,3,3]"));
            JsonArray cells=new JsonArray();
            for(int i=0;i<(type.equals("plannerator:fusion_test")?1125:27);i++)cells.add(-1);
            design.add("design", cells);
            design.add("block_recipes", new JsonArray());
            design.addProperty("recipe", 0);
            design.addProperty("fuel", 0);
            design.addProperty("coolant_recipe", 0);
            design.add("modules", JsonParser.parseString("{\"plannerator:metadata\":{\"Name\":\""+type+"\"}}"));
            if(type.equals("nuclearcraft:overhaul_turbine"))design.add("inputs", JsonParser.parseString("[0,3]"));
            designs.add(design);
        }
        roundTrip(doc.toString(), "all six design families with turbine links");
        JsonArray planes=new JsonArray();
        for(int x=0;x<3;x++){
            JsonArray rows=new JsonArray();
            for(int y=0;y<3;y++)rows.add(JsonParser.parseString("[-1,-1,-1]"));
            planes.add(rows);
        }
        designs.get(2).getAsJsonObject().add("design", planes);
        roundTrip(doc.toString(), "nested MSR design alongside flattened designs");
    }
    private static void detailedExtensions() throws Exception{
        JsonObject doc = JsonParser.parseString(Files.readString(Paths.get("test/files/ncpf/detailed-reactor.ncpf.json"))).getAsJsonObject();
        doc.add("future_document_field", JsonParser.parseString("{\"null\":null,\"large\":123456789012345678901234567890,\"precise\":0.123456789012345678901234567890,\"text\":\"é ☢ 核\"}"));
        JsonObject modules = doc.getAsJsonObject("modules");
        modules.add("future:module", JsonParser.parseString("{\"nested\":[null,{\"enabled\":true}],\"fraction\":0.125}"));
        JsonObject config = doc.getAsJsonObject("configuration");
        config.add("future:configuration", JsonParser.parseString("{\"field\":[1,2,3]}"));
        config.getAsJsonObject("nuclearcraft:overhaul_sfr").addProperty("future_configuration_field", "retained");
        JsonObject block = config.getAsJsonObject("nuclearcraft:overhaul_sfr").getAsJsonArray("blocks").get(0).getAsJsonObject();
        block.addProperty("future_block_field", "retained");
        doc.getAsJsonArray("designs").get(0).getAsJsonObject().addProperty("future_design_field", "retained");
        doc.getAsJsonArray("designs").add(JsonParser.parseString("{\"type\":\"future:design\",\"payload\":[1,{\"x\":null}]}"));
        for(JsonElement element : config.getAsJsonObject("nuclearcraft:overhaul_sfr").getAsJsonArray("blocks")){
            JsonObject blockModules=element.getAsJsonObject().getAsJsonObject("modules");
            if(blockModules!=null&&blockModules.has("nuclearcraft:overhaul_sfr:heat_sink")){
                blockModules.getAsJsonObject("nuclearcraft:overhaul_sfr:heat_sink").getAsJsonArray("rules").add(JsonParser.parseString("{\"type\":\"future:rule\",\"payload\":[1,2]}"));
                break;
            }
        }
        roundTrip(doc.toString(), "detailed extensions and unknown design without array");
        JsonObject converted = save(load(doc.toString())).getAsJsonObject();
        if(converted.has("future_document_field") || converted.getAsJsonObject("configuration").getAsJsonObject("nuclearcraft:overhaul_sfr").has("future_configuration_field"))throw new AssertionError("extra known fields retained");
        if(converted.getAsJsonObject("configuration").getAsJsonObject("nuclearcraft:overhaul_sfr").getAsJsonArray("blocks").get(0).getAsJsonObject().has("future_block_field") || converted.getAsJsonArray("designs").get(0).getAsJsonObject().has("future_design_field"))throw new AssertionError("extra known fields retained");
        JsonElement unknownModule = converted.getAsJsonObject("modules").get("future:module");
        equal(modules.get("future:module"), unknownModule, "unknown module payload");
        equal(config.get("future:configuration"), converted.getAsJsonObject("configuration").get("future:configuration"), "unknown configuration payload");
        JsonObject unknownDesign = converted.getAsJsonArray("designs").get(1).getAsJsonObject();
        if(!"future:design".equals(unknownDesign.get("type").getAsString()) || unknownDesign.getAsJsonArray("payload").get(0).getAsInt()!=1)throw new AssertionError("unknown design lost");
        JsonObject minimal = JsonParser.parseString("{\"version\":1,\"future\":null}").getAsJsonObject();
        roundTrip(minimal.toString(), "omitted optional fields");
        JsonObject designOnly = doc.deepCopy();
        designOnly.remove("configuration");
        // Design-only indices are resolved against the active configuration.
        Core.project = load(doc.toString());
        roundTrip(designOnly.toString(), "design-only document");
    }
    private static void decodedTexture() throws Exception{
        JsonObject document = JsonParser.parseString(Files.readString(Paths.get("test/files/ncpf/detailed-reactor.ncpf.json"))).getAsJsonObject();
        // Exercise native decode/encode too, with one texture and the full detailed reactor data.
        boolean[] kept = {false};
        retainOneTexture(document, kept);
        if(!kept[0])throw new AssertionError("No texture in detailed fixture");
        System.clearProperty("plannerator.skipTextures");
        try{
            roundTrip(document.toString(), "decoded texture with detailed reactor");
            com.thizthizzydizzy.dizzyengine.graphics.image.Image before = com.thizthizzydizzy.dizzyengine.graphics.image.Image.fromBase64(firstTexture(document));
            com.thizthizzydizzy.dizzyengine.graphics.image.Image after = com.thizthizzydizzy.dizzyengine.graphics.image.Image.fromBase64(firstTexture(save(load(document.toString()))));
            if(before.getWidth()!=after.getWidth()||before.getHeight()!=after.getHeight())throw new AssertionError("texture dimensions changed");
            for(int x=0;x<before.getWidth();x++)for(int y=0;y<before.getHeight();y++)if(before.getRGB(x,y)!=after.getRGB(x,y))throw new AssertionError("texture pixel changed");
        }
        finally{System.setProperty("plannerator.skipTextures", "true");}
    }
    private static String firstTexture(JsonElement json){
        if(json.isJsonObject()){
            JsonObject object=json.getAsJsonObject();
            if(object.has("modules")){
                JsonObject modules=object.getAsJsonObject("modules");
                if(modules.has("plannerator:texture")&&modules.getAsJsonObject("plannerator:texture").has("texture"))return modules.getAsJsonObject("plannerator:texture").get("texture").getAsString();
            }
            for(JsonElement child:object.asMap().values()){
                String result=firstTexture(child);if(result!=null)return result;
            }
        }else if(json.isJsonArray())for(JsonElement child:json.getAsJsonArray()){
            String result=firstTexture(child);if(result!=null)return result;
        }
        return null;
    }
    private static void retainOneTexture(JsonElement json, boolean[] kept){
        if(json.isJsonObject()){
            JsonObject object=json.getAsJsonObject();
            if(object.has("modules")){
                JsonObject modules=object.getAsJsonObject("modules");
                if(modules.has("plannerator:texture")){
                    if(kept[0])modules.remove("plannerator:texture");
                    else kept[0]=true;
                }
            }
            for(JsonElement child : object.asMap().values())retainOneTexture(child, kept);
        }else if(json.isJsonArray())for(JsonElement child : json.getAsJsonArray())retainOneTexture(child, kept);
    }
    private static void edits() throws Exception{
        String json = Files.readString(Paths.get("test/files/ncpf/detailed-reactor.ncpf.json"));
        Project project = load(json);
        JsonObject expected = save(project).getAsJsonObject();
        project.metadata.put("Name", "Edited ☢");
        expected.getAsJsonObject("modules").getAsJsonObject("plannerator:metadata").addProperty("Name", "Edited ☢");
        equal(expected, save(project), "internal edit must reach file");
        equal(expected, save(project.copyTo(Project::new)), "edited copy must reach file");
        NcpfRoot root = parse(json);
        root.version = 2;
        JsonObject libraryExpected = write(parse(json)).getAsJsonObject();
        libraryExpected.addProperty("version", 2);
        equal(libraryExpected, write(root), "typed library edit must reach file");
        net.ncplanner.ncpf.structure.design.nuclearcraft.OverhaulSFRDesign typed=(net.ncplanner.ncpf.structure.design.nuclearcraft.OverhaulSFRDesign)root.designs.get(0);
        typed.design[0][0][0]=-1;
        libraryExpected.getAsJsonArray("designs").get(0).getAsJsonObject().getAsJsonArray("design").get(0).getAsJsonArray().get(0).getAsJsonArray().set(0, new com.google.gson.JsonPrimitive(-1));
        equal(libraryExpected, write(root), "typed cell edit reaches nested output");
        JsonObject configurationSource=JsonParser.parseString(Files.readString(Paths.get("src/configurations/nuclearcraft.ncpf.json"))).getAsJsonObject();
        JsonArray blocks=configurationSource.getAsJsonObject("configuration").getAsJsonObject("nuclearcraft:overhaul_sfr").getAsJsonArray("blocks");
        blocks.get(0).getAsJsonObject().addProperty("future", "first");
        blocks.get(1).getAsJsonObject().addProperty("future", "second");
        NcpfRoot reordered=parse(configurationSource.toString());
        List<NcpfElement> typedBlocks = reordered.configuration.getConfiguration(net.ncplanner.ncpf.structure.configuration.nuclearcraft.OverhaulSFRConfiguration.class).blocks;
        java.util.Collections.swap(typedBlocks,0,1);
        configurationSource = write(parse(configurationSource.toString())).getAsJsonObject();
        blocks = configurationSource.getAsJsonObject("configuration").getAsJsonObject("nuclearcraft:overhaul_sfr").getAsJsonArray("blocks");
        JsonElement first=blocks.get(0);blocks.set(0,blocks.get(1));blocks.set(1,first);
        equal(configurationSource,write(reordered),"reordering known blocks");
        typedBlocks.remove(0);blocks.remove(0);
        equal(configurationSource,write(reordered),"removing known blocks");
        project.designs.clear();
        expected.add("designs", new JsonArray());
        equal(expected, save(project), "deleted designs must stay deleted");
        // Saving a newly constructed internal project also has a library endpoint.
        Project fresh = new Project();
        fresh.metadata.put("Name", "Fresh");
        Project reloaded = load(save(fresh).toString());
        if(!"Fresh".equals(reloaded.metadata.get("Name")))throw new AssertionError("fresh project metadata");
    }
    private static void cannedConfigurations() throws Exception{
        JsonObject source=JsonParser.parseString(Files.readString(Paths.get("src/configurations/nuclearcraft.ncpf.json"))).getAsJsonObject();
        net.ncplanner.plannerator.planner.configuration.ConfigurationManager.initNuclearcraftConfiguration();
        equal(save(NcpfBridge.toProject(parse(source.toString()))),save(net.ncplanner.plannerator.planner.configuration.ConfigurationManager.NUCLEARCRAFT.toProject()),"default configuration loader");
        source.addProperty("future", "retained");
        source.getAsJsonObject("configuration").getAsJsonObject("nuclearcraft:overhaul_sfr").addProperty("future", "retained");
        net.ncplanner.plannerator.planner.configuration.CannedConfiguration canned=new net.ncplanner.plannerator.planner.configuration.CannedConfiguration(parse(source.toString()));
        source = save(NcpfBridge.toProject(parse(source.toString()))).getAsJsonObject();
        equal(source, save(canned.toProject()), "canned configuration document");
        equal(source, save(canned.safeCopy().toProject()), "canned configuration copy");
        net.ncplanner.plannerator.planner.ncpf.Configuration legacy=new net.ncplanner.plannerator.planner.ncpf.Configuration(load(source.toString()));
        equal(source, save(new net.ncplanner.plannerator.planner.configuration.CannedConfiguration(legacy).toProject()), "wrapped configuration document");
    }
    private static void exportsAndFailures() throws Exception{
        Project project = load(Files.readString(Paths.get("test/files/ncpf/detailed-reactor.ncpf.json")));
        JsonElement before = save(project);
        NCPFWriter.format = WRITER;
        ByteArrayOutputStream exported = new ByteArrayOutputStream();
        FileWriter.write(project, exported, FileWriter.NCPF);
        JsonObject document = JsonParser.parseString(exported.toString(StandardCharsets.UTF_8)).getAsJsonObject();
        checkTrimmed(document);
        equal(before, save(project), "export must not mutate source");
        Project copy = project.copyTo(Project::new);
        copy.makePartial();
        net.ncplanner.plannerator.ncpf.io.NCPFObject old = new net.ncplanner.plannerator.ncpf.io.NCPFObject();
        copy.convertToObject(old);
        JsonElement expected = NcpfJsonConverter.gson.toJsonTree(old);
        trim(expected);
        equal(expected, document, "export retains previous partial/trimmed semantics");
        Path destination = Files.createTempFile("modern-ncpf-failure-", ".ncpf.json");
        try{
            NCPFFileWriter.write(project, destination.toFile(), WRITER);
            byte[] saved = Files.readAllBytes(destination);
            NCPFFormatWriter failing = new NCPFFormatWriter(){
                public String getExtension(){return "json";}
                public void write(NcpfRoot root, OutputStream stream) throws IOException{stream.write(123);throw new IOException("expected failure");}
            };
            try{NCPFFileWriter.write(project, destination.toFile(), failing);throw new AssertionError("write failure swallowed");}
            catch(java.io.UncheckedIOException expectedFailure){}
            if(!java.util.Arrays.equals(saved, Files.readAllBytes(destination)))throw new AssertionError("failed save destroyed destination");
            FileWriter.write(project, destination.toFile(), FileWriter.NCPF);
            equal(document, JsonParser.parseString(Files.readString(destination)), "file export endpoint");
        }finally{Files.deleteIfExists(destination);}
        class OwnedStream extends ByteArrayOutputStream{
            boolean closed;
            @Override public void close(){closed=true;}
        }
        OwnedStream stream = new OwnedStream();
        NCPFFileWriter.write(project, stream, WRITER);
        if(stream.closed)throw new AssertionError("caller stream closed");
        class OwnedInput extends ByteArrayInputStream{
            boolean closed;
            OwnedInput(){super(before.toString().getBytes(StandardCharsets.UTF_8));}
            @Override public void close(){closed=true;}
        }
        OwnedInput direct = new OwnedInput();
        READER.read(direct);
        if(direct.closed)throw new AssertionError("format reader closed caller stream");
        List<OwnedInput> opened = new ArrayList<>();
        NCPFFileReader.read(() -> {OwnedInput input=new OwnedInput();opened.add(input);return input;}, null);
        if(opened.stream().anyMatch(input -> !input.closed))throw new AssertionError("file reader leaked opened stream");
    }
    private static void invalidDocuments(){
        for(String json : new String[]{"{}", "{\"version\":\"1\"}", "{\"version\":1.5}", "{\"version\":2147483648}", "{\"version\":1,\"designs\":42}", "{\"version\":1}{\"version\":1}", "{\"version\":1"}){
            try{READER.read(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));throw new AssertionError("accepted invalid document: "+json);}
            catch(com.google.gson.JsonParseException expected){}
        }
        try{load("{\"version\":1,\"designs\":42}");throw new AssertionError("invalid modern document swallowed");}
        catch(com.google.gson.JsonParseException expected){}
    }
    private static void checkTrimmed(JsonElement json){
        if(json.isJsonObject()){
            JsonObject object=json.getAsJsonObject();
            if(object.has("modules"))for(String key : object.getAsJsonObject("modules").keySet()){
                if(!key.startsWith("ncpf:"))throw new AssertionError("untrimmed export module: "+key);
            }
            for(JsonElement child : object.asMap().values())checkTrimmed(child);
        }else if(json.isJsonArray())for(JsonElement child : json.getAsJsonArray())checkTrimmed(child);
    }
    private static void trim(JsonElement json){
        if(json.isJsonObject()){
            JsonObject object=json.getAsJsonObject();
            if(object.has("modules")){
                JsonObject modules=object.getAsJsonObject("modules");
                modules.keySet().removeIf(key -> !key.startsWith("ncpf:"));
                if(modules.isEmpty())object.remove("modules");
            }
            for(JsonElement child : object.asMap().values())trim(child);
        }else if(json.isJsonArray())for(JsonElement child : json.getAsJsonArray())trim(child);
    }
    private static void checkDesignArrays(JsonElement original, JsonElement converted, String label){
        if(!original.isJsonObject()||!original.getAsJsonObject().has("designs"))return;
        JsonArray before=original.getAsJsonObject().getAsJsonArray("designs"), after=converted.getAsJsonObject().getAsJsonArray("designs");
        if(before.size()!=after.size())throw new AssertionError(label+" lost designs");
        for(int i=0;i<before.size();i++){
            JsonObject a=before.get(i).getAsJsonObject(), b=after.get(i).getAsJsonObject();
            equal(a.get("type"), b.get("type"), label+" design type");
            for(String key:new String[]{"dimensions","design","block_recipes"})if(a.has(key)){
                if(!b.has(key)){
                    JsonArray flat=new JsonArray();flatten(a.get(key),flat);
                    if(flat.isEmpty())continue;
                    throw new AssertionError(label+" lost design field "+key);
                }
                JsonArray flatBefore=new JsonArray(), flatAfter=new JsonArray();
                flatten(a.get(key),flatBefore);flatten(b.get(key),flatAfter);
                equal(flatBefore,flatAfter,label+" design "+i+" "+key+" values");
            }
        }
    }
    private static void flatten(JsonElement value, JsonArray target){
        if(value.isJsonArray())for(JsonElement child:value.getAsJsonArray())flatten(child,target);
        else target.add(value);
    }
    private static void recordChanges(JsonElement before, JsonElement after, String label, String path, java.util.Set<String> output){
        if(sameValue(before,after))return;
        if(before.isJsonObject()&&after.isJsonObject()){
            JsonObject a=before.getAsJsonObject(), b=after.getAsJsonObject();
            for(String key:a.keySet()){
                if(!b.has(key))output.add(label+" REMOVED "+path+"."+key);
                else recordChanges(a.get(key),b.get(key),label,path+"."+key,output);
            }
            for(String key:b.keySet())if(!a.has(key))output.add(label+" ADDED "+path+"."+key);
        }else if(before.isJsonArray()&&after.isJsonArray()){
            JsonArray a=before.getAsJsonArray(), b=after.getAsJsonArray();
            if(a.size()!=b.size()){output.add(label+" ARRAY SHAPE/SIZE "+path);return;}
            for(int i=0;i<a.size();i++)recordChanges(a.get(i),b.get(i),label,path+"[]",output);
        }else output.add(label+" VALUE/SHAPE "+path);
    }
    private static void equal(JsonElement expected, JsonElement actual, String label){
        if(!sameValue(expected, actual))throw new AssertionError(label+" differs at "+difference(expected, actual, "$"));
    }
    /** Numeric comparisons must not silently round large integers or precise decimals. */
    private static boolean sameValue(JsonElement a, JsonElement b){
        if(a.isJsonObject()&&b.isJsonObject()){
            JsonObject left=a.getAsJsonObject(), right=b.getAsJsonObject();
            if(!left.keySet().equals(right.keySet()))return false;
            for(String key : left.keySet())if(!sameValue(left.get(key),right.get(key)))return false;
            return true;
        }
        if(a.isJsonArray()&&b.isJsonArray()){
            JsonArray left=a.getAsJsonArray(), right=b.getAsJsonArray();
            if(left.size()!=right.size())return false;
            for(int i=0;i<left.size();i++)if(!sameValue(left.get(i),right.get(i)))return false;
            return true;
        }
        if(a.isJsonPrimitive()&&b.isJsonPrimitive()&&a.getAsJsonPrimitive().isNumber()&&b.getAsJsonPrimitive().isNumber()){
            return a.getAsBigDecimal().compareTo(b.getAsBigDecimal())==0;
        }
        return a.equals(b);
    }

    private static String difference(JsonElement expected, JsonElement actual, String path){
        if(sameValue(expected, actual))return "";
        if(expected.isJsonObject()&&actual.isJsonObject()){
            JsonObject a=expected.getAsJsonObject(), b=actual.getAsJsonObject();
            java.util.TreeSet<String> keys=new java.util.TreeSet<>(a.keySet());keys.addAll(b.keySet());
            for(String key : keys){
                if(!a.has(key)||!b.has(key))return path+"."+key+" (missing or added)";
                if(!sameValue(a.get(key),b.get(key)))return difference(a.get(key),b.get(key),path+"."+key);
            }
        }else if(expected.isJsonArray()&&actual.isJsonArray()){
            JsonArray a=expected.getAsJsonArray(), b=actual.getAsJsonArray();
            if(a.size()!=b.size())return path+" (array size "+a.size()+" vs "+b.size()+")";
            for(int i=0;i<a.size();i++)if(!sameValue(a.get(i),b.get(i)))return difference(a.get(i),b.get(i),path+"["+i+"]");
        }
        return path+" (expected "+expected+", actual "+actual+")";
    }
}
