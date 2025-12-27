package me.lotiny.misty.bukkit.scenario.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.inventory.XInventoryView;
import io.fairyproject.bukkit.util.LegacyAdventureUtil;
import io.fairyproject.bukkit.util.items.ItemBuilder;
import io.fairyproject.container.Autowired;
import io.fairyproject.mc.MCPlayer;
import io.fairyproject.util.CC;
import me.lotiny.misty.api.customitem.CraftLimit;
import me.lotiny.misty.api.customitem.CustomItem;
import me.lotiny.misty.api.customitem.CustomItemRegistry;
import me.lotiny.misty.api.event.CustomItemCraftEvent;
import me.lotiny.misty.api.scenario.Scenario;
import me.lotiny.misty.api.scenario.ScenarioManager;
import me.lotiny.misty.bukkit.scenario.annotations.IncompatibleWith;
import me.lotiny.misty.bukkit.utils.ItemStackUtils;
import me.lotiny.misty.bukkit.utils.PlayerUtils;
import me.lotiny.misty.bukkit.utils.Utilities;
import me.lotiny.misty.bukkit.utils.VersionUtils;
import me.lotiny.misty.bukkit.utils.chat.message.MessageUtils;
import me.lotiny.misty.shared.event.PlayerPickupItemEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.*;
import org.bukkit.material.MaterialData;

import java.util.*;

@SuppressWarnings("deprecation")
@IncompatibleWith({BarebonesScenario.class, GoldRushScenario.class})
public class CustomCraftScenario extends Scenario {

    @Autowired
    private static CustomItemRegistry customItemRegistry;
    @Autowired
    private static ScenarioManager scenarioManager;

    private boolean isRecipeRegistered = false;

    @Override
    public String getName() {
        return "Custom Craft";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(XMaterial.CRAFTING_TABLE)
                .name("&b" + getName())
                .lore(
                        "&7Added Custom Craft to the game!",
                        "&7You can view the recipes by /recipes"
                )
                .build();
    }

    @EventHandler
    public void handlePrepareCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        CustomItem item = customItemRegistry.getCustomItem(result);
        if (item == null) return;

        InventoryView view = event.getView();
        Player player = (Player) XInventoryView.of(view).getPlayer();
        if (!isCanCraft(player, item)) {
            event.getInventory().setResult(null);
            player.sendMessage(CC.translate("&cYou already reached the limit of this craft!"));
            return;
        }

        boolean cancel = false;
        CraftingInventory inventory = event.getInventory();
        for (ItemStack matrix : inventory.getMatrix()) {
            if (customItemRegistry.isCustomItem(matrix)) {
                cancel = true;
                break;
            }
        }

        if (cancel) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void handleCraftItem(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof CraftingInventory inventory) {
            if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

            ItemStack craftedItem = event.getCurrentItem();
            if (ItemStackUtils.isNull(craftedItem)) return;

            Player player = (Player) event.getWhoClicked();
            CustomItem item = customItemRegistry.getCustomItem(craftedItem);
            if (item == null) return;

            if (!isCanCraft(player, item)) {
                event.setCancelled(true);
                return;
            }

            ItemStack[] matrix = inventory.getMatrix();
            int size = matrix.length;
            int amount = 100;
            for (int i = 0; i < size; i++) {
                ItemStack itemStack = inventory.getMatrix()[i];
                if (ItemStackUtils.isNull(itemStack)) {
                    continue;
                }

                amount = Math.min(itemStack.getAmount(), amount);
            }

            CraftLimit limit = item.getCraftLimit();
            int maxCraft = limit.getAmount();
            if (limit.isUnique()) {
                PlayerUtils.playSound(XSound.UI_TOAST_CHALLENGE_COMPLETE, XSound.ENTITY_ENDER_DRAGON_DEATH);
                Utilities.broadcast("&b" + player.getName() + "&e has crafted the &6" + item.getName() + "&e! This item cannot be crafted again!");

                maxCraft = 1;
            }

            int craftedAmount = item.getPlayerCrafts().getOrDefault(player.getUniqueId(), 0);
            int toCraftAmount = craftedAmount + amount;
            if (toCraftAmount > maxCraft) {
                amount = maxCraft - craftedAmount;
                toCraftAmount = maxCraft;
            }

            ItemStack result = item.getItem().hasItemMeta() ? customItemRegistry.createResult(item) : item.getItem();
            CustomItemCraftEvent calledEvent = new CustomItemCraftEvent(player, item, result, false);
            Bukkit.getServer().getPluginManager().callEvent(calledEvent);

            if (calledEvent.isCancelled()) return;

            item.getPlayerCrafts().put(player.getUniqueId(), toCraftAmount);
            player.sendMessage(CC.translate("&aYou crafted &f" + item.getName() + "&e (" + toCraftAmount + "/" + maxCraft + ")"));

            if (item.isRemoveResult() || event.getClick().isKeyboardClick() || event.getClick().isShiftClick()) {
                event.setCurrentItem(null);

                ItemStack[] newMatrix = new ItemStack[size];
                for (int i = 0; i < size; i++) {
                    ItemStack itemStack = matrix[i];
                    if (itemStack == null || itemStack.getAmount() == amount) {
                        if (itemStack != null && itemStack.getType().name().endsWith("_BUCKET")) {
                            newMatrix[i] = XMaterial.BUCKET.parseItem();
                        } else {
                            newMatrix[i] = null;
                        }
                    } else {
                        ItemStack newItem = itemStack.clone();
                        newItem.setAmount(newItem.getAmount() - amount);
                        newMatrix[i] = newItem;
                    }
                }

                inventory.setMatrix(newMatrix);

                if (item.isRemoveResult()) return;

                for (int i = 0; i < amount; i++) {
                    player.getInventory().addItem(calledEvent.getResult());
                }

                event.setCancelled(true);
            } else {
                event.setCurrentItem(calledEvent.getResult());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleEntityPickup(PlayerPickupItemEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (player.getInventory().firstEmpty() == -1) return;

        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();
        check(player, itemStack);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerBucket(PlayerBucketFillEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        check(player, event.getItemStack());
    }

    private void check(Player player, ItemStack itemStack) {
        if (scenarioManager.isEnabled("Companion Bench")) return;

        List<Recipe> recipes = customItemRegistry.findCraftableRecipes(player, itemStack, false);
        List<Recipe> updateRecipes = customItemRegistry.findCraftableRecipes(player, itemStack, true);

        Set<ItemStack> recipeResults = new HashSet<>();
        for (Recipe recipe : recipes) {
            recipeResults.add(recipe.getResult());
        }

        updateRecipes.removeIf(updateRecipe -> recipeResults.contains(updateRecipe.getResult()));

        for (Recipe recipe : updateRecipes) {
            CustomItem craft = customItemRegistry.getCustomItem(recipe.getResult());
            if (craft == null) {
                continue;
            }

            if (isCanCraft(player, craft)) {
                XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player);

                MCPlayer mcPlayer = MCPlayer.from(player);
                if (mcPlayer != null) {
                    Component component = Component.text()
                            .appendSpace()
                            .appendNewline()
                            .append(LegacyAdventureUtil.decode(MessageUtils.centeredMessage("&fYou have all the items")))
                            .appendNewline()
                            .append(LegacyAdventureUtil.decode(MessageUtils.centeredMessage("&fto craft a " + craft.getName())))
                            .appendNewline()
                            .append(LegacyAdventureUtil.decode(MessageUtils.centeredMessage("&e&lCLICK HERE &r&fto craft it!")))
                            .clickEvent(ClickEvent.runCommand("/itemcraft " + craft.getId()))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to craft " + craft.getName(), NamedTextColor.YELLOW)))
                            .asComponent();

                    mcPlayer.sendMessage(component);
                }
            }
        }
    }

    private boolean isCanCraft(Player player, CustomItem customItem) {
        CraftLimit limit = customItem.getCraftLimit();

        if (limit.isUnique()) {
            return customItem.getPlayerCrafts().isEmpty();
        } else {
            return customItem.getPlayerCrafts().getOrDefault(player.getUniqueId(), 0) < limit.getAmount();
        }
    }

    @SuppressWarnings("removal")
    @Override
    public void onEnable() {
        if (!isRecipeRegistered) {
            isRecipeRegistered = true;
            for (CustomItem customItem : customItemRegistry.getCustomItems().values()) {
                customItem.getRecipes().forEach(recipe -> {
                    if (recipe instanceof ShapedRecipe shapedRecipe) {
                        boolean hasPotion = shapedRecipe.getIngredientMap().values().stream()
                                .filter(Objects::nonNull)
                                .anyMatch(item -> XMaterial.matchXMaterial(item) == XMaterial.POTION);

                        if (hasPotion) {
                            Map<Character, Object> ingredientMap = new HashMap<>();

                            for (Map.Entry<Character, ItemStack> entry : shapedRecipe.getIngredientMap().entrySet()) {
                                ItemStack item = entry.getValue();
                                if (item == null) continue;

                                if (XMaterial.matchXMaterial(item) == XMaterial.POTION && VersionUtils.isLower(12, 2)) {
                                    ingredientMap.put(entry.getKey(), new MaterialData(item.getType(), (byte) -1));
                                } else {
                                    ingredientMap.put(entry.getKey(), item);
                                }
                            }

                            Recipe fixedRecipe = customItemRegistry.getCustomItemRecipeCreator(customItem)
                                    .createShaped(String.join("", shapedRecipe.getShape()), ingredientMap);

                            Bukkit.addRecipe(fixedRecipe);
                        } else {
                            Bukkit.addRecipe(shapedRecipe);
                        }

                    } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                        boolean hasPotion = shapelessRecipe.getIngredientList().stream()
                                .anyMatch(item -> XMaterial.matchXMaterial(item) == XMaterial.POTION);

                        if (hasPotion) {
                            Map<Object, Integer> ingredientMap = new HashMap<>();

                            for (ItemStack ingredient : shapelessRecipe.getIngredientList()) {
                                if (XMaterial.matchXMaterial(ingredient) == XMaterial.POTION && VersionUtils.isLower(12, 2)) {
                                    ingredientMap.put(new MaterialData(ingredient.getType(), (byte) -1), ingredient.getAmount());
                                } else {
                                    ingredientMap.put(ingredient, ingredient.getAmount());
                                }
                            }

                            Recipe fixedRecipe = customItemRegistry.getCustomItemRecipeCreator(customItem)
                                    .createShapeless(ingredientMap);

                            Bukkit.addRecipe(fixedRecipe);
                        } else {
                            Bukkit.addRecipe(shapelessRecipe);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().resetRecipes();
        isRecipeRegistered = false;
    }
}
