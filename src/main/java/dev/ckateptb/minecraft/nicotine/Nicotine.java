package dev.ckateptb.minecraft.nicotine;

import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.common.tableclothcontainer.event.ComponentRegisterEvent;
import dev.ckateptb.common.tableclothcontainer.util.FinderUtil;
import dev.ckateptb.common.tableclothevent.EventBus;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class Nicotine extends JavaPlugin {
    private final Set<Runnable> executeOnEnable = new HashSet<>();

    public Nicotine() {
        this.registerFeatures();
        IoC.registerBean(this, this.getClass());
    }

    @Override
    public void onLoad() {
        IoC.init();
    }

    @Override
    public void onEnable() {
        executeOnEnable.removeIf(runnable -> {
            runnable.run();
            return true;
        });
    }

    private void registerFeatures() {
        EventBus.GLOBAL.registerEventHandler(ComponentRegisterEvent.class, event -> {
            Object instance = event.getInstance();
            Class<?> owner = event.getOwner();
            Plugin plugin = owner.isAssignableFrom(Plugin.class) && IoC.containsBean(owner) ? (Plugin) IoC.getBean(owner) : this;
            if (instance instanceof Listener listener) {
                this.runOnEnable(() -> Bukkit.getPluginManager().registerEvents(listener, plugin));
            }
            FinderUtil.findMethods(event.getClazz(), Schedule.class).forEach(method -> {
                try {
                    Schedule annotation = method.getAnnotation(Schedule.class);
                    int fixedRate = annotation.fixedRate();
                    int initialDelay = annotation.initialDelay();
                    boolean async = annotation.async();
                    BukkitScheduler scheduler = Bukkit.getScheduler();
                    Method task = scheduler.getClass()
                            .getDeclaredMethod(async ? "runTaskTimerAsynchronously" : "runTaskTimer",
                                    Plugin.class,
                                    Runnable.class,
                                    long.class,
                                    long.class);
                    task.setAccessible(true);
                    this.runOnEnable(() -> {
                        try {
                            task.invoke(scheduler, plugin, (Runnable) () -> {
                                try {
                                    method.invoke(instance);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }, initialDelay, fixedRate);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public void runOnEnable(Runnable runnable) {
        if (this.isEnabled()) runnable.run();
        else this.executeOnEnable.add(runnable);
    }
}