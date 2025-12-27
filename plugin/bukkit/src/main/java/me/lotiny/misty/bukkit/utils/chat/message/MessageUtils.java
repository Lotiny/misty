package me.lotiny.misty.bukkit.utils.chat.message;

import io.fairyproject.util.CC;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class MessageUtils {

    public void sendCenteredMessage(Player player, String message) {
        if (message == null || message.isEmpty()) {
            player.sendMessage("");
            return;
        }

        message = CC.translate(message);
        int messageWidth = calculateTextWidth(message);
        String padding = getPadding(messageWidth);

        player.sendMessage(padding + message);
    }

    public String centeredMessage(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        message = CC.translate(message);
        int messageWidth = calculateTextWidth(message);
        String padding = getPadding(messageWidth);

        return padding + message;
    }

    public String getPadding(int messageWidth) {
        int halvedMessageWidth = messageWidth / 2;
        int totalPadding = 154 - halvedMessageWidth;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int numSpaces = totalPadding / spaceLength;

        return " ".repeat(Math.max(0, numSpaces));
    }

    public int calculateTextWidth(String text) {
        int messageWidth = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : text.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = (c == 'l' || c == 'L');
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messageWidth += isBold ? dFI.getBoldLength() : dFI.getLength();
                messageWidth++;
            }
        }
        return messageWidth;
    }
}
