package screret.itemabilities.data;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import screret.itemabilities.ItemAbilities;
import screret.itemabilities.abilities.AbilityType;

public class ItemAbilityInfo {
    protected String name;

    protected ResourceLocation item;
    protected AbilityType abilityType;

    protected boolean enabled = true;
    // Stats:
    public double width = 1.6D;
    public double height = 0.8D;
    public double distanceFromPlayer = 2.0D;
    public boolean damageCaster;
    public int damage;


    public void loadFromJson(JsonObject json) {
        if(json.has("name"))
            this.name = json.get("name").getAsString().toLowerCase();

        // Item Class:
        try {
            this.item = new ResourceLocation(json.get("item").getAsString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ability config loading error. Missing Item: " + json.get("item").getAsString());
        }

        String type = json.get("ability").getAsString();
        for(int i = 0; i < AbilityType.values().length; ++i){
            if(!type.equals(AbilityType.values()[i].toString()) && i == AbilityType.values().length - 1){
                throw new RuntimeException("failed to match ability type");
            } else if(type.equals(AbilityType.values()[i].toString())){
                abilityType = AbilityType.values()[i];
            }
        }

        if(json.has("enabled"))
            this.enabled = json.get("enabled").getAsBoolean();
        if(json.has("width"))
            this.width = json.get("width").getAsDouble();
        if(json.has("height"))
            this.height = json.get("height").getAsDouble();
        if(json.has("distanceFromPlayer"))
            this.distanceFromPlayer = json.get("distanceFromPlayer").getAsDouble();
        if(json.has("damageCaster"))
            this.damageCaster = json.get("damageCaster").getAsBoolean();
        if(json.has("damage"))
            this.damage = json.get("damage").getAsInt();
    }

    public String getName() {
        return this.name;
    }

    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(ItemAbilities.MOD_ID, this.getName() + "_" + item);
    }

}
