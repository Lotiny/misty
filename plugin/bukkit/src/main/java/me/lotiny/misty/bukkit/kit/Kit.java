package me.lotiny.misty.bukkit.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Kit {

    private ItemStack[] armors;
    private ItemStack[] items;
}
