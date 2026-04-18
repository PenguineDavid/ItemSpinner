package io.github.penguinedavid.spinplugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class SpinPlugin extends JavaPlugin {

    private BukkitTask spinTask;
    private float radPerTick;
    private static final int INTERP_TICKS = 5;

    @Override
    public void onEnable() {
        String version = Bukkit.getServer().getBukkitVersion().split("-")[0];
        if (!isVersionAtLeast(version, "1.19.4")) {
            getLogger().severe("SpinPlugin requires 1.19.4+. Disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        reloadConfig();
        FileConfiguration config = getConfig();

        double degsPerTick = config.getDouble("rotation-speed-degrees-per-tick", 5.0);
        radPerTick = (float) Math.toRadians(degsPerTick);

        getLogger().info("Rotation: " + degsPerTick + " deg/tick (" + (degsPerTick * 20) + " deg/s)");

        spinTask = new BukkitRunnable() {
            // Step is larger to cover INTERP_TICKS ticks worth of rotation in one update
            private final Quaternionf delta = new Quaternionf(
                new AxisAngle4f(radPerTick * INTERP_TICKS, 0, 1, 0)
            );
            private int tick = 0;

            @Override
            public void run() {
                // Only push a new transform every INTERP_TICKS ticks;
                // the client interpolates smoothly between updates
                if (tick++ % INTERP_TICKS != 0) return;

                for (var world : Bukkit.getWorlds()) {
                    for (ItemDisplay display : world.getEntitiesByClass(ItemDisplay.class)) {
                        if (!display.isValid() || !display.getScoreboardTags().contains("spin")) continue;

                        Transformation t = display.getTransformation();
                        Quaternionf newRot = new Quaternionf(t.getLeftRotation()).mul(delta);

                        display.setInterpolationDelay(0);
                        display.setInterpolationDuration(INTERP_TICKS);
                        display.setTransformation(new Transformation(
                            t.getTranslation(),
                            newRot,
                            t.getScale(),
                            t.getRightRotation()
                        ));
                    }
                }
            }
        }.runTaskTimer(this, 0L, 1L);

        getLogger().info("SpinPlugin enabled.");
    }

    @Override
    public void onDisable() {
        if (spinTask != null && !spinTask.isCancelled()) spinTask.cancel();
        getLogger().info("SpinPlugin disabled.");
    }

    private boolean isVersionAtLeast(String current, String required) {
        try {
            String[] c = current.split("\\.");
            String[] r = required.split("\\.");
            for (int i = 0; i < Math.max(c.length, r.length); i++) {
                int cv = i < c.length ? Integer.parseInt(c[i]) : 0;
                int rv = i < r.length ? Integer.parseInt(r[i]) : 0;
                if (cv < rv) return false;
                if (cv > rv) return true;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}