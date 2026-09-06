package com.wildfire.main.config;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class Configuration {
    private static net.minecraftforge.common.config.Configuration forgeConfig;
    private static PlayerGenderSettings localPlayerSettings;
	private static PlayerGenderSettings fakeCreditsSettings;

    public static class PlayerGenderSettings {
        public String gender = "Male";
        public boolean breastsEnabled = false;
        public float breastSize = 50.0F;
        public float bounceMultiplier = 1.0F;
        public float separation = 0.0F;
        public float depth = 0.0F;
        public float height = 0.0F;
        public float rotation = 0.0F;
        public float breastsOffsetX = 0.0F;
        public float breastsOffsetY = 0.0F;
        public float breastsOffsetZ = 0.0F;
        public float breastsCleavage = 0.0F;
        public boolean breastsUniboob = false;
        public float stiffness = 0.1F;
        public float damping = 0.85F;
        public boolean showFirstTimeGui = true;
        public boolean hurtSoundsEnabled = false;
        public boolean physicsEnabled = true;
        public float intensity = 100.0F;
        public float momentum = 50.0F;
        public float voicePitch = 100.0F;
        public boolean darkMode = false;
        public boolean overrideArmorPhysics = false;
        public boolean showArmorTooltip = true;
        public boolean hideInArmor = false;
        public boolean holidayThemes = true;

        public PlayerGenderSettings() {
        }
    }

    public static void loadConfig(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), "Configuration.cfg");
        forgeConfig = new net.minecraftforge.common.config.Configuration(configFile);

        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            forgeConfig.load();

            localPlayerSettings = new PlayerGenderSettings();
            localPlayerSettings.gender = forgeConfig.getString("Gender", "General", "Male", "Default gender (Male, Female, Other)");
            localPlayerSettings.breastsEnabled = forgeConfig.getBoolean("BreastsEnabled", "General", false, "Enable or disable breasts feature");
            localPlayerSettings.breastSize = forgeConfig.getFloat("BreastSize", "General", 50.0F, 0.0F, 100.0F, "Breast size (0-100%)");
            localPlayerSettings.bounceMultiplier = forgeConfig.getFloat("BounceMultiplier", "General", 1.0F, 0.0F, 3.0F, "Default bounce multiplier");
            localPlayerSettings.separation = forgeConfig.getFloat("Separation", "General", 0.0F, -10.0F, 10.0F, "Separation (-10 to 10)");
            localPlayerSettings.depth = forgeConfig.getFloat("Depth", "General", 0.0F, -10.0F, 0.0F, "Depth (0 to -10, inward push)");
            localPlayerSettings.height = forgeConfig.getFloat("Height", "General", 0.0F, -10.0F, 10.0F, "Height (-10 to 10)");
            localPlayerSettings.rotation = forgeConfig.getFloat("Rotation", "General", 0.0F, 0.0F, 10.0F, "Rotation (0-10)");
            localPlayerSettings.breastsOffsetX = forgeConfig.getFloat("BreastsOffsetX", "General", 0.0F, -10.0F, 10.0F, "Default X offset");
            localPlayerSettings.breastsOffsetY = forgeConfig.getFloat("BreastsOffsetY", "General", 0.0F, -10.0F, 10.0F, "Default Y offset");
            localPlayerSettings.breastsOffsetZ = forgeConfig.getFloat("BreastsOffsetZ", "General", 0.0F, -10.0F, 10.0F, "Default Z offset");
            localPlayerSettings.breastsCleavage = forgeConfig.getFloat("BreastsCleavage", "General", 0.0F, 0.0F, 10.0F, "Default cleavage");
            localPlayerSettings.breastsUniboob = forgeConfig.getBoolean("BreastsUniboob", "General", false, "Default uniboob");
            localPlayerSettings.stiffness = forgeConfig.getFloat("Stiffness", "General", 0.1F, 0.05F, 0.5F, "Default stiffness");
            localPlayerSettings.damping = forgeConfig.getFloat("Damping", "General", 0.85F, 0.5F, 0.95F, "Default damping");
            localPlayerSettings.showFirstTimeGui = forgeConfig.getBoolean("ShowFirstTimeGui", "General", true, "Show first-time GUI");
            localPlayerSettings.hurtSoundsEnabled = forgeConfig.getBoolean("HurtSoundsEnabled", "General", false, "Enable female hurt sounds");
            localPlayerSettings.physicsEnabled = forgeConfig.getBoolean("PhysicsEnabled", "General", true, "Enable breast physics");
            localPlayerSettings.intensity = forgeConfig.getFloat("Intensity", "General", 100.0F, 0.0F, 150.0F, "Physics intensity (0-150%)");
            localPlayerSettings.momentum = forgeConfig.getFloat("Momentum", "General", 50.0F, 25.0F, 100.0F, "Physics momentum (25-100%)");
            localPlayerSettings.voicePitch = forgeConfig.getFloat("VoicePitch", "General", 100.0F, 80.0F, 120.0F, "Voice pitch (80-120%)");
            localPlayerSettings.darkMode = forgeConfig.getBoolean("DarkMode", "General", false, "Enable dark mode theme");
            localPlayerSettings.overrideArmorPhysics = forgeConfig.getBoolean("OverrideArmorPhysics", "General", false, "Override armor interaction with breast physics");
            localPlayerSettings.showArmorTooltip = forgeConfig.getBoolean("ShowArmorTooltip", "General", true, "Show armor stats tooltip for breast armor");
            localPlayerSettings.hideInArmor = forgeConfig.getBoolean("HideInArmor", "General", false, "Hide breasts visually while wearing armor");
            localPlayerSettings.holidayThemes = forgeConfig.getBoolean("HolidayThemes", "General", true, "Enable holiday-themed cosmetics in GUI previews");
        } catch (Exception e) {
            System.err.println("Failed to load gender config: " + e.getMessage());
        } finally {
            if (forgeConfig.hasChanged()) {
                forgeConfig.save();
            }
        }
    }

    public static void saveConfig() {
        try {
            if (localPlayerSettings == null) {
                localPlayerSettings = new PlayerGenderSettings();
            }
            forgeConfig.get("General", "Gender", "Male").set(localPlayerSettings.gender);
            forgeConfig.get("General", "BreastsEnabled", false).set(localPlayerSettings.breastsEnabled);
            forgeConfig.get("General", "BreastSize", 50.0F).set(localPlayerSettings.breastSize);
            forgeConfig.get("General", "BounceMultiplier", 1.0F).set(localPlayerSettings.bounceMultiplier);
            forgeConfig.get("General", "Separation", 0.0F).set(localPlayerSettings.separation);
            forgeConfig.get("General", "Depth", 0.0F).set(localPlayerSettings.depth);
            forgeConfig.get("General", "Height", 0.0F).set(localPlayerSettings.height);
            forgeConfig.get("General", "Rotation", 0.0F).set(localPlayerSettings.rotation);
            forgeConfig.get("General", "BreastsOffsetX", 0.0F).set(localPlayerSettings.breastsOffsetX);
            forgeConfig.get("General", "BreastsOffsetY", 0.0F).set(localPlayerSettings.breastsOffsetY);
            forgeConfig.get("General", "BreastsOffsetZ", 0.0F).set(localPlayerSettings.breastsOffsetZ);
            forgeConfig.get("General", "BreastsCleavage", 0.0F).set(localPlayerSettings.breastsCleavage);
            forgeConfig.get("General", "BreastsUniboob", false).set(localPlayerSettings.breastsUniboob);
            forgeConfig.get("General", "Stiffness", 0.1F).set(localPlayerSettings.stiffness);
            forgeConfig.get("General", "Damping", 0.85F).set(localPlayerSettings.damping);
            forgeConfig.get("General", "ShowFirstTimeGui", true).set(localPlayerSettings.showFirstTimeGui);
            forgeConfig.get("General", "HurtSoundsEnabled", false).set(localPlayerSettings.hurtSoundsEnabled);
            forgeConfig.get("General", "PhysicsEnabled", true).set(localPlayerSettings.physicsEnabled);
            forgeConfig.get("General", "Intensity", 100.0F).set(localPlayerSettings.intensity);
            forgeConfig.get("General", "Momentum", 50.0F).set(localPlayerSettings.momentum);
            forgeConfig.get("General", "VoicePitch", 100.0F).set(localPlayerSettings.voicePitch);
            forgeConfig.get("General", "DarkMode", false).set(localPlayerSettings.darkMode);

            forgeConfig.get("General", "OverrideArmorPhysics", false).set(localPlayerSettings.overrideArmorPhysics);
            forgeConfig.get("General", "ShowArmorTooltip", true).set(localPlayerSettings.showArmorTooltip);
            forgeConfig.get("General", "HideInArmor", false).set(localPlayerSettings.hideInArmor);
            forgeConfig.get("General", "HolidayThemes", true).set(localPlayerSettings.holidayThemes);

            if (forgeConfig.hasChanged()) {
                forgeConfig.save();
            }
        } catch (Exception e) {
            System.err.println("Failed to save gender config: " + e.getMessage());
        }
    }

    public static PlayerGenderSettings getPlayerSettings(EntityPlayer player) {
        try {
            if (player == null) return localPlayerSettings;
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null || mc.thePlayer == null) return localPlayerSettings;

            if (player != mc.thePlayer) {

                return null;
            }
        } catch (Throwable ignored) {
            return localPlayerSettings;
        }
        if (localPlayerSettings == null) {
            localPlayerSettings = new PlayerGenderSettings();
            localPlayerSettings.showFirstTimeGui = true;
        }
        return localPlayerSettings;
    }

    public static PlayerGenderSettings getStaticFakeCreditsSettings() {
        if (fakeCreditsSettings == null) {
            fakeCreditsSettings = new PlayerGenderSettings();
            fakeCreditsSettings.gender = "Female";
            fakeCreditsSettings.breastsEnabled = true;
            fakeCreditsSettings.breastSize = 100.0F;
            fakeCreditsSettings.bounceMultiplier = 0.0F;
            fakeCreditsSettings.separation = 2.0F;
            fakeCreditsSettings.depth = 0.0F;
            fakeCreditsSettings.height = 0.0F;
            fakeCreditsSettings.rotation = 0.0F;
            fakeCreditsSettings.breastsCleavage = 1.0F;
            fakeCreditsSettings.breastsUniboob = false;
            fakeCreditsSettings.physicsEnabled = false;
        }
        return fakeCreditsSettings;
    }

    public static void setOverrideArmorPhysics(EntityPlayer player, boolean enabled) {
        if (player != Minecraft.getMinecraft().thePlayer) return;
        PlayerGenderSettings settings = getPlayerSettings(player);
        if (settings != null) {
            settings.overrideArmorPhysics = enabled;
            saveConfig();
        }
    }

    public static boolean getOverrideArmorPhysics(EntityPlayer player) {
        PlayerGenderSettings settings = getPlayerSettings(player);
        return settings != null ? settings.overrideArmorPhysics : false;
    }

    public static void setShowArmorTooltip(EntityPlayer player, boolean enabled) {
        if (player != Minecraft.getMinecraft().thePlayer) return;
        PlayerGenderSettings settings = getPlayerSettings(player);
        if (settings != null) {
            settings.showArmorTooltip = enabled;
            saveConfig();
        }
    }

    public static boolean getShowArmorTooltip(EntityPlayer player) {
        PlayerGenderSettings settings = getPlayerSettings(player);
        return settings != null ? settings.showArmorTooltip : true;
    }

    public static void setHideInArmor(EntityPlayer player, boolean enabled) {
        if (player != Minecraft.getMinecraft().thePlayer) return;
        PlayerGenderSettings settings = getPlayerSettings(player);
        if (settings != null) {
            settings.hideInArmor = enabled;
            saveConfig();
        }
    }

    public static boolean getHideInArmor(EntityPlayer player) {
        PlayerGenderSettings settings = getPlayerSettings(player);
        return settings != null ? settings.hideInArmor : false;
    }

    public static void setHolidayThemes(EntityPlayer player, boolean enabled) {
        if (player != Minecraft.getMinecraft().thePlayer) return;
        PlayerGenderSettings settings = getPlayerSettings(player);
        if (settings != null) {
            settings.holidayThemes = enabled;
            saveConfig();
        }
    }

    public static boolean getHolidayThemes(EntityPlayer player) {
        PlayerGenderSettings settings = getPlayerSettings(player);
        return settings != null ? settings.holidayThemes : true;
    }

    public static void setGender(EntityPlayer player, String gender) {
        if (player != Minecraft.getMinecraft().thePlayer) return;
        PlayerGenderSettings settings = getPlayerSettings(player);
        if (gender != null && (gender.equals("Male") || gender.equals("Female") || gender.equals("Other"))) {
            settings.gender = gender;
            saveConfig();
        }
    }

    public static String getGender(EntityPlayer player) {
        PlayerGenderSettings settings = getPlayerSettings(player);
        return settings != null ? settings.gender : "Male";
    }

    public static void setDarkMode(EntityPlayer player, boolean darkMode) {
        if (player != Minecraft.getMinecraft().thePlayer) return;
        PlayerGenderSettings settings = getPlayerSettings(player);
        if (settings != null) {
            settings.darkMode = darkMode;
            saveConfig();
        }
    }

    public static boolean getDarkMode(EntityPlayer player) {
        PlayerGenderSettings settings = getPlayerSettings(player);
        return settings != null ? settings.darkMode : false;
    }
}
