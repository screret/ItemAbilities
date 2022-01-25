package screret.itemabilities;

import com.google.common.collect.Maps;
import jdk.jpackage.internal.resources.ResourceLocator;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import screret.itemabilities.abilities.Ability;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ItemAbilities.MOD_ID)
public class ItemAbilities {

    public static final String MOD_ID = "itemabilities";

    public static Map<ResourceLocation, Ability> abilityMap = new HashMap<ResourceLocation,Ability>();

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public ItemAbilities() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        //LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public static void rightClickEvent(PlayerInteractEvent.RightClickItem event){
        ItemStack stack = event.getItemStack();
        for(Map.Entry<ResourceLocation, Ability> entry : abilityMap.entrySet()){
            if(stack.getItem().getRegistryName() == entry.getKey()){
                CompoundNBT tag = stack.getTagElement("ability");
                if(tag == null){
                    stack.addTagElement("ability", StringNBT.valueOf(entry.getValue().toString()));
                }
                entry.getValue().Execute(event.getEntity(), event.getWorld().getServer().createCommandSourceStack());
            }
        }
    }
}
