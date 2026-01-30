package lipinekrynek.autorynek;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

public class AutoRynkClient implements ClientModInitializer {
   public static final String MOD_ID = "autorynek";

   @Override
   public void onInitializeClient() {
      AutoRynkInternal.initialize();
   }

   public static String getCurrentServerName() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client == null)
         return "Unknown";
      if (client.isInSingleplayer())
         return "Singleplayer";
      ServerInfo serverInfo = client.getCurrentServerEntry();
      return (serverInfo != null && serverInfo.address != null) ? serverInfo.address : "Unknown Server";
   }
}
