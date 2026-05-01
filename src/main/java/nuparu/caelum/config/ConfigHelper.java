package nuparu.caelum.config;

//import com.electronwill.nightconfig.core.file.CommentedFileConfig;
//import com.electronwill.nightconfig.core.io.WritingMode;
import net.neoforged.neoforge.common.ModConfigSpec;

//import java.io.File;

public class ConfigHelper {

    //private static final ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();
    private static final ModConfigSpec.Builder clientBuilder = new ModConfigSpec.Builder();
    //public static final ForgeConfigSpec serverConfig;
    public static final ModConfigSpec clientConfig;

    static {/*
        ServerConfig.init(serverBuilder);
        serverConfig = serverBuilder.build();*/


        ClientConfig.init(clientBuilder);
        clientConfig = clientBuilder.build();
    }

//    public static void loadConfig(ModConfigSpec config, String path) {
//        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
//        file.load();
//        config.setConfig(file);
//    }
}
