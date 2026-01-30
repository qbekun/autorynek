package lipinekrynek.autorynek;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {
   private final String urlString;
   private String content;

   public DiscordWebhook(String urlString) {
      this.urlString = urlString;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public void execute() throws IOException {
      if (content == null || content.isEmpty())
         return;

      java.net.URL url = java.net.URI.create(urlString).toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("User-Agent", "Mozilla/5.0");
      connection.setDoOutput(true);

      String json = "{\"content\": \"" + content.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"")
            + "\"}";

      try (OutputStream os = connection.getOutputStream()) {
         byte[] input = json.getBytes(StandardCharsets.UTF_8);
         os.write(input, 0, input.length);
      }

      int responseCode = connection.getResponseCode();
      if (responseCode >= 300) {
         throw new IOException("Webhook returned response code: " + responseCode);
      }
   }
}
