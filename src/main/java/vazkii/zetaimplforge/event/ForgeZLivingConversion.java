package vazkii.zetaimplforge.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import vazkii.zeta.event.ZLivingConversion;

public class ForgeZLivingConversion implements ZLivingConversion {
    private final LivingConversionEvent e;

    public ForgeZLivingConversion(LivingConversionEvent e) {
        this.e = e;
    }

    @Override
    public LivingEntity getEntity() {
        return e.getEntity();
    }

    public static class Post extends ForgeZLivingConversion implements ZLivingConversion.Post {
        private final LivingConversionEvent.Post e;

        public Post(LivingConversionEvent.Post e) {
            super(e);
            this.e = e;
        }

        @Override
        public LivingEntity getOutcome() {
            return e.getOutcome();
        }
    }
}
