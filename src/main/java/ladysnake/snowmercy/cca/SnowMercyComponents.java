package ladysnake.snowmercy.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import ladysnake.snowmercy.common.SnowMercy;
import net.minecraft.util.Identifier;

public final class SnowMercyComponents implements WorldComponentInitializer {
    public static final ComponentKey<SnowMercyEventComponent> SNOWMERCY =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(SnowMercy.MODID, "snowmercy"), SnowMercyEventComponent.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        // Add the component to every World instance
        registry.register(SNOWMERCY, world -> new SnowMercyEventComponent());
    }
}