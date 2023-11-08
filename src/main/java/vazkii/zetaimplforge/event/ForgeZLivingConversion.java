package vazkii.zetaimplforge.event;

import net.minecraft.world.entity.EntityType;
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

    @Override
    public boolean isCanceled() {
        return e.isCanceled();
    }

    @Override
    public void setCanceled(boolean cancel) {
        e.setCanceled(cancel);
    }

    public static class Pre extends ForgeZLivingConversion implements ZLivingConversion.Pre {
        private final LivingConversionEvent.Pre e;

        public Pre(LivingConversionEvent.Pre e) {
            super(e);
            this.e = e;
        }

        @Override
        public EntityType<? extends LivingEntity> getOutcome() {
            return e.getOutcome();
        }
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
