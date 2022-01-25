package screret.itemabilities.abilities;

import net.minecraft.advancements.criterion.EnchantedItemTrigger;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class Ability {

    public ResourceLocation name;
    public AbilityType type;

    public int damage;
    public double distanceFromPlayer;

    public String scriptFilePath;

    private Entity owner;

    public void Execute(Entity entity, CommandSource source){
        owner = entity;

        switch (type){
            case CUSTOM:
                source.getServer().getCommands().performCommand(source, "function " + scriptFilePath);
                break;
            case LIGHTNING:
                lightningAbility(entity);
                break;
            case DAMAGE:
                damageAbility(entity);
                break;
            case EFFECT:
                effectAbility(entity);
                break;
        }
    }

    private void lightningAbility(Entity entity){
        if(entity.level.isClientSide()){
            return;
        }
        LightningBoltEntity boltEntity = EntityType.LIGHTNING_BOLT.create(entity.level);
        boltEntity.setDamage(damage);

        double spawnX = entity.getX() + distanceFromPlayer * entity.getLookAngle().x;
        double spawnY = entity.getY() + entity.getEyeHeight() + distanceFromPlayer * entity.getLookAngle().y;
        double spawnZ = entity.getZ() + distanceFromPlayer * entity.getLookAngle().z;
        boltEntity.moveTo(Vector3d.atBottomCenterOf(new Vector3i(spawnX, spawnY, spawnZ)));

        ServerWorld level = (ServerWorld) entity.level;
        level.addFreshEntity(boltEntity);
    }

    private void damageAbility(Entity entity){
        World level = entity.level;
        if(level.isClientSide()){
            return;
        }

        Vector3d end = entity.position().add(entity.getDeltaMovement());
        RayTraceResult raytraceresult = level.clip(new RayTraceContext(entity.position(), end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
        if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            end = raytraceresult.getLocation();
        }
        EntityRayTraceResult entityRayTraceResult = ProjectileHelper.getEntityHitResult(level, entity, entity.position(), end, entity.getBoundingBox().expandTowards(entity.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
        if (entityRayTraceResult != null) {
            entityRayTraceResult.getEntity().hurt(DamageSource.GENERIC, damage);
        }
    }

    private void effectAbility(Entity entity){

    }

    protected boolean canHitEntity(Entity entity) {
        if (!entity.isSpectator() && entity.isAlive() && entity.isPickable()) {
            Entity entity1 = this.getOwner();
            return entity1 == null || !entity1.isPassengerOfSameVehicle(entity);
        } else {
            return false;
        }
    }

    public Entity getOwner(){
        return owner;
    }

    @Override
    public String toString(){
        return name.toString();
    }
}
