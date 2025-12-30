<div align="center">
    <img width="55%" height="55%" src="assets/misty_logo.png" alt="Misty">

# Misty
Misty is an open-sourced & powerful UHC plugin that come with many features.

[![GNU General Public License v3.0](https://img.shields.io/badge/License-GPL_3.0-blue)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-green)](https://github.com/Lotiny/misty)

</div>

## Requirements
- [Paper](https://papermc.io/)
- [fairy-lib-plugin](https://github.com/FairyProject/fairy-lib-plugin)
- [Chunky](https://modrinth.com/plugin/chunky) (For 1.16.5+)
- [Chunky Border](https://modrinth.com/plugin/chunkyborder) (For 1.16.5+)
- [WorldBorder](https://dev.bukkit.org/projects/worldborder) (For 1.8.8, 1.12.2)

Optional
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- [LuckPerms](https://luckperms.net/)
- [Apollo](https://lunarclient.dev/)

## Support
Currently, there is no Discord server yet. Open an issues for bug-report!

## Server using Misty
- Open PR to add your server!

## API
Getting the API instance.
```java
@Override
public void onEnable() {
    MistyApi api = MistyApi.getInstance();
}
```
Create CustomItem for CustomCraft scenario.
```java
public class ExampleItem extends CustomItem {

    @Override
    public String getId() {
        return "example-item";
    }

    @Override
    public String getName() {
        return "Speed Sword";
    }

    @Override
    public ItemStack getItem() {
        // Create an ItemStack for your item.
        return ItemBuilder.of(XMaterial.GOLDEN_SWORD)
                .name("Speed Sword")
                .lore("Killed a player will give you speed!")
                .build();
    }

    @Override
    public CraftLimit getCraftLimit() {
        // This means each player can craft this item only 4 times per game.
        return CraftLimit.of(4);
    }

    @Override
    public List<Recipe> getRecipes() {
        // Use this getRecipeCreator() or it won't work.
        return List.of(
                // Shaped Recipe
                getRecipeCreator().createShaped(
                        "XAXXAXXBX",
                        Map.of(
                                // Support only XMaterial, ItemStack and MaterialData
                                'A', XMaterial.SUGAR,
                                'B', new ItemStack(Material.STICK)
                        )
                ),
                // Shapeless Recipe
                getRecipeCreator().createShapeless(
                        Map.of(
                                // Support only XMaterial, ItemStack and MaterialData
                                XMaterial.SUGAR, 2,
                                XMaterial.STICK, 1
                        )
                )
        );
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // handle the listener give player speed when kill
    }
}
```

Register the CustomItem.
```java
@Override
public void onPluginEnable() {
    MistyApi mistyApi = MistyApi.getInstance();
    mistyApi.registerCustomItem(new ExampleItem());
}
```

## Building
Clone this repository then running this command.

```
./gradlew :plugin:dist:clean :plugin:dist:shadowJar
```