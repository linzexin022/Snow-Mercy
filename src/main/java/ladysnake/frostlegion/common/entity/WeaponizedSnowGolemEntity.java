package ladysnake.frostlegion.common.entity;

import ladysnake.frostlegion.common.init.EntityTypes;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShovelItem;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class WeaponizedSnowGolemEntity extends SnowGolemEntity {
    private static final TrackedData<Integer> HEAD = DataTracker.registerData(WeaponizedSnowGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public WeaponizedSnowGolemEntity(EntityType<? extends WeaponizedSnowGolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, SnowGolemEntity.class, 10, true, false, snowGolemEntity -> !(snowGolemEntity instanceof WeaponizedSnowGolemEntity)));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0D, 1.0000001E-5F));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(5, new LookAroundGoal(this));
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HEAD, 1);
    }

    // HEAD STATUS
    // 0: Headless
    // 1: Hostile (default)
    // 2: Helps player
    public int getHead() {
        return this.dataTracker.get(HEAD);
    }

    public void setHead(int head) {
        this.dataTracker.set(HEAD, head);
    }

    public static DefaultAttributeContainer.Builder createEntityAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 50);
    }

    @Override
    public void tick() {
        super.tick();

        FrostWalkerEnchantment.freezeWater(this, this.world, this.getBlockPos(), 0);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!(source.getAttacker() instanceof WeaponizedSnowGolemEntity)) {
            if (this.getHead() == 1 && !(this instanceof SnowGolemHeadEntity) && source.getAttacker() instanceof ServerPlayerEntity) {
                double eyeHeight = this.getY() + this.getEyeHeight(this.getPose()) - 0.3f;
                SnowGolemHeadEntity entity = new SnowGolemHeadEntity(world, EntityTypes.GOLEM_IDS.inverse().get(this.getType()), this.getX(), eyeHeight, this.getZ());
                PlayerEntity player = ((PlayerEntity) source.getAttacker());
                if (player.getMainHandStack().getItem() instanceof ShovelItem) {
                    this.world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.NEUTRAL, 1.0f, 0.5f);
                    this.world.playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    entity.setProperties(player, player.pitch, player.yaw, 0.0F, amount, amount);
                    world.spawnEntity(entity);

                    player.spawnSweepAttackParticles();
                    ((ServerWorld) this.world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SNOW_BLOCK, 1)), this.getX(), eyeHeight, this.getZ(), 40, random.nextGaussian() / 20f, 0.2D + random.nextGaussian() / 20f, random.nextGaussian() / 20f, 0.1f);

                    this.setHead(0);
                }
            }

            return super.damage(source, amount);
        } else {
            return false;
        }
    }

    @Override
    public boolean hurtByWater() {
        return false;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
    }

    @Override
    public boolean hasPumpkin() {
        return this.getHead() == 2;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getHead() == 0 && player.getStackInHand(hand).getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
            player.getStackInHand(hand).decrement(1);
            this.setHead(2);
            return ActionResult.success(this.world.isClient);
        } else {
            return ActionResult.PASS;
        }
    }
}