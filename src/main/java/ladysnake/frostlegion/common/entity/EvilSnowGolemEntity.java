package ladysnake.frostlegion.common.entity;

import ladysnake.frostlegion.common.network.Packets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class EvilSnowGolemEntity extends SnowGolemEntity implements Monster {
    public EvilSnowGolemEntity(EntityType<? extends SnowGolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createEntityAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return Packets.newSpawnPacket(this);
    }

    public void increaseFrost() {

    }
}