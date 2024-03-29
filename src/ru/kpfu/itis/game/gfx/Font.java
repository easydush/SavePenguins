package ru.kpfu.itis.game.gfx;

public class Font {
    private static String chars = "" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ      " + "0123456789.,:;'\"!?$%()-=+/      ";

    public static void render(String message, Screen screen, int x, int y, int colour, int scale) {
        message = message.toUpperCase();
        for (int i = 0; i < message.length(); i++) {
            int charIndex = chars.indexOf(message.charAt(i));
            if (charIndex >= 0)
                screen.render(x + (i * 8), y, charIndex + 30 * 32, colour, 0x00, scale);
        }
    }
}
