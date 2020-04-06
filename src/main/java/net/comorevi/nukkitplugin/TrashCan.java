package net.comorevi.nukkitplugin;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemGhastTear;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.comorevi.cphone.cphone.data.StringsData;

import java.io.File;
import java.util.LinkedHashMap;

public class TrashCan extends PluginBase implements Listener {
    private static int TRASH_CAN_BLOCK_ID;
    private static String MESSAGE;

    @Override
    public void onEnable() {
        if (!this.getDataFolder().exists()) getDataFolder().mkdirs();
        Config config = new Config(
                new File(this.getDataFolder(), "config.yml"),
                Config.YAML,
                new LinkedHashMap<String, Object>() {
                    {
                        put("TrashCanBlockID", Block.MONSTER_SPAWNER);
                        put("Message", "TrashCan>> アイテムを消去しました。");
                    }
                });
        config.save();
        TRASH_CAN_BLOCK_ID = config.getInt("TrashCanBlockID");
        MESSAGE = config.getString("Message");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (isTrashCan(event.getBlock())) {
            Player player = event.getPlayer();
            Item offhand = player.getOffhandInventory().getItem(0);
            Item[] arm = new Item[4];
            for (int i = 0; i < 4; i++) {
                arm[i] = player.getInventory().getArmorItem(i);
            }
            player.getInventory().clearAll();
            player.sendMessage(MESSAGE);

            for (int i = 0; i < 4; i++) {
                player.getInventory().setArmorItem(i, arm[i]);
            }
            player.getOffhandInventory().addItem(offhand);
            player.getInventory().sendArmorContents(player);
            Item ghastTear = new ItemGhastTear();
            ghastTear.setCustomName(StringsData.get(player, "cphone_title"));
            player.getInventory().setItem(0, ghastTear);
            event.setCancelled();
        }
    }

    public boolean isTrashCan(Block block) {
        return block.getId() == TRASH_CAN_BLOCK_ID;
    }
}
