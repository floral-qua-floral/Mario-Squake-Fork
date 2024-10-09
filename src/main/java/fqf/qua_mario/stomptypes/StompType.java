package fqf.qua_mario.stomptypes;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioRegistries;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.characters.CharaStat;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public abstract class StompType {
	protected Identifier ID;
	protected boolean onlyFromAbove;
	protected boolean attemptMount;
	protected boolean ignoresHurtsToStomp;
	protected RegistryKey<DamageType> damageType;

	public abstract boolean cannotStompSpecific(Entity target);

	public abstract void executeStompServer(ServerWorld world, ServerPlayerEntity mario, Entity target, boolean harmless);
	public abstract void executeStompClient(Entity target, boolean harmless);
	public abstract void executeStompCommon(World world, PlayerEntity mario, Entity target, boolean harmless);

	public void attemptStomp() {
		List<Entity> targets = MarioClient.player.getWorld().getOtherEntities(MarioClient.player, MarioClient.player.getBoundingBox().offset(0, MarioClient.yVel, 0));
		if(targets.isEmpty()) return;

		if(targets.removeIf(this::cannotStompOverallClient) && targets.isEmpty()) return;

		StompHandler.forbiddenStompTargets.addAll(targets);
		for(Entity entity : targets) {
			ModMarioQuaMario.LOGGER.info("Sending a Stomp Attempt packet");
			sendAttemptPacket(entity);
		}
	}

	public boolean cannotStompOverall(Entity target) {
		return(
			!(target instanceof LivingEntity livingTarget) ||
			livingTarget.isDead() ||
			target.getType().isIn(StompHandler.UNSTOMPABLE_TAG) ||
			cannotStompSpecific(target)
		);
	}
	public boolean cannotStompOverallClient(Entity target) {
		return((onlyFromAbove && MarioClient.player.getY() < target.getY() + target.getHeight()) || cannotStompOverall(target));
	}

	public void damageTarget(ServerPlayerEntity mario, ServerWorld world, Entity target, float baseDamageFactor, float armorFactor, float toughnessFactor) {
		float damageAmount = (float) CharaStat.STOMP_BASE_DAMAGE.getValue(mario) * baseDamageFactor;

		DamageSource damageSource = makeDamageSource(world, MarioRegistries.STOMP_DAMAGE, mario);
		boolean useLegsItem = damageSource.isIn(StompHandler.USES_LEGS_ITEM_TAG);

		ItemStack attackingArmor = mario.getEquippedStack(useLegsItem ? EquipmentSlot.LEGS : EquipmentSlot.FEET);
		float itemArmor = 0;
		float itemToughness = 0;

		AttributeModifiersComponent stackAttributeModifiersComponent =
				attackingArmor.getOrDefault(
						DataComponentTypes.ATTRIBUTE_MODIFIERS,
						AttributeModifiersComponent.DEFAULT
				);
		AttributeModifiersComponent itemAttributeModifiersComponent =
				attackingArmor.getItem().getComponents().getOrDefault(
						DataComponentTypes.ATTRIBUTE_MODIFIERS,
						AttributeModifiersComponent.DEFAULT
				);

		ModMarioQuaMario.LOGGER.info("ItemStack: " + attackingArmor);
		ModMarioQuaMario.LOGGER.info("Stack Component: " + stackAttributeModifiersComponent);
		ModMarioQuaMario.LOGGER.info("Item Component: " + itemAttributeModifiersComponent);

		for(AttributeModifiersComponent.Entry entry : itemAttributeModifiersComponent.modifiers()) {
			RegistryEntry<EntityAttribute> attribute = entry.attribute();

			ModMarioQuaMario.LOGGER.info("Attribute: " + entry.attribute().getIdAsString());
			ModMarioQuaMario.LOGGER.info("Operation: " + entry.modifier().operation());
			ModMarioQuaMario.LOGGER.info("Value: " + entry.modifier().value());
		}


//		attributeModifiersComponent.modifiers().contains();

		target.damage(damageSource, damageAmount + (itemArmor * armorFactor) + (itemToughness + toughnessFactor));
	}

	public static DamageSource makeDamageSource(ServerWorld world, RegistryKey<DamageType> key, Entity attacker) {
		return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), attacker);
	}

	public void sendAttemptPacket(Entity target) {
		ClientPlayNetworking.send(new StompHandler.RequestStompPayload(target.getId(), MarioRegistries.STOMP_TYPES.getRawIdOrThrow(this)));
	}
	public void sendAffirmPacket(ServerPlayerEntity player, Entity target, boolean harmless) {
		ServerPlayNetworking.send(player, new StompHandler.affirmStompPayload(target.getId(), MarioRegistries.STOMP_TYPES.getRawIdOrThrow(this), harmless));
	}

	public Identifier getID() {
		return(this.ID);
	}
	public void register() {
		Registry.register(MarioRegistries.STOMP_TYPES, this.getID(), this);
	}
}
