package com.chaevsfe.monkeyplush.init;

import com.chaevsfe.monkeyplush.MonkeyPlush;
import com.chaevsfe.monkeyplush.block.MonkeyPlushBlock;
import com.chaevsfe.monkeyplush.block.MonkeyPlushBlockEntity;
import com.chaevsfe.monkeyplush.entity.MonkeyPlushEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModEntities {
    public static final EntityType<MonkeyPlushEntity> EVIL_MONKEY = EntityType.Builder
            .of(MonkeyPlushEntity::new, MobCategory.CREATURE)
            .sized(0.5f, 0.4f)
            .clientTrackingRange(8)
            .build("evil_monkey");

    public static final EntityType<MonkeyPlushEntity> JUMBO_EVIL_MONKEY = EntityType.Builder
            .of(MonkeyPlushEntity::new, MobCategory.CREATURE)
            .sized(2.0f, 1.6f)
            .clientTrackingRange(10)
            .build("jumbo_evil_monkey");

    public static MonkeyPlushBlock MONKEY_PLUSH_BLOCK;
    public static MonkeyPlushBlock JUMBO_MONKEY_PLUSH_BLOCK;
    public static BlockEntityType<MonkeyPlushBlockEntity> PLUSH_BLOCK_ENTITY;

    public static SpawnEggItem EVIL_MONKEY_SPAWN_EGG;
    public static SpawnEggItem JUMBO_EVIL_MONKEY_SPAWN_EGG;

    public static void registerAll() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, MonkeyPlush.resource("evil_monkey"), EVIL_MONKEY);
        FabricDefaultAttributeRegistry.register(EVIL_MONKEY, MonkeyPlushEntity.createAttributes());

        Registry.register(BuiltInRegistries.ENTITY_TYPE, MonkeyPlush.resource("jumbo_evil_monkey"), JUMBO_EVIL_MONKEY);
        FabricDefaultAttributeRegistry.register(JUMBO_EVIL_MONKEY, MonkeyPlushEntity.createJumboAttributes());

        MONKEY_PLUSH_BLOCK = new MonkeyPlushBlock(BlockBehaviour.Properties.of()
                .strength(0.5f).sound(SoundType.WOOL).noOcclusion());
        Registry.register(BuiltInRegistries.BLOCK, MonkeyPlush.resource("monkey_plush"), MONKEY_PLUSH_BLOCK);

        JUMBO_MONKEY_PLUSH_BLOCK = new MonkeyPlushBlock(BlockBehaviour.Properties.of()
                .strength(0.8f).sound(SoundType.WOOL).noOcclusion());
        Registry.register(BuiltInRegistries.BLOCK, MonkeyPlush.resource("jumbo_monkey_plush"), JUMBO_MONKEY_PLUSH_BLOCK);

        PLUSH_BLOCK_ENTITY = BlockEntityType.Builder
                .of(MonkeyPlushBlockEntity::new, MONKEY_PLUSH_BLOCK, JUMBO_MONKEY_PLUSH_BLOCK)
                .build(null);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, MonkeyPlush.resource("monkey_plush"), PLUSH_BLOCK_ENTITY);

        Registry.register(BuiltInRegistries.ITEM, MonkeyPlush.resource("monkey_plush"),
                new BlockItem(MONKEY_PLUSH_BLOCK, new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM, MonkeyPlush.resource("jumbo_monkey_plush"),
                new BlockItem(JUMBO_MONKEY_PLUSH_BLOCK, new Item.Properties()));

        EVIL_MONKEY_SPAWN_EGG = new SpawnEggItem(EVIL_MONKEY, 0xA0784C, 0xF0E6D2, new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, MonkeyPlush.resource("evil_monkey_spawn_egg"), EVIL_MONKEY_SPAWN_EGG);

        JUMBO_EVIL_MONKEY_SPAWN_EGG = new SpawnEggItem(JUMBO_EVIL_MONKEY, 0x7C5A38, 0xF0E6D2, new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, MonkeyPlush.resource("jumbo_evil_monkey_spawn_egg"), JUMBO_EVIL_MONKEY_SPAWN_EGG);
    }
}
