package me.lotiny.misty.api.profile.stats;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum StatType {

    ELO(XMaterial.EXPERIENCE_BOTTLE),
    WINS(XMaterial.NETHER_STAR),
    KILLS(XMaterial.IRON_SWORD),
    DEATHS(XMaterial.SKELETON_SKULL),
    GAME_PLAYED(XMaterial.CHEST),
    TOTAL_DAMAGE(XMaterial.REDSTONE),
    DIAMOND_MINED(XMaterial.DIAMOND_ORE);

    @Getter
    private final XMaterial material;

    public static StatType get(String data) {
        return Arrays.stream(values())
                .filter(s -> s.getData().equals(data.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    public String getData() {
        return this.name().toLowerCase();
    }
}
