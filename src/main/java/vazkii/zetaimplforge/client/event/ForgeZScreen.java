package vazkii.zetaimplforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.event.ScreenEvent;
import vazkii.zeta.client.event.ZScreen;

public class ForgeZScreen implements ZScreen {
    public static class Render extends ForgeZScreen implements ZScreen.Render {
        private final ScreenEvent.Render e;

        public Render(ScreenEvent.Render e) {
            this.e = e;
        }

        @Override
        public PoseStack getPoseStack() {
            return e.getPoseStack();
        }

        @Override
        public int getMouseX() {
            return e.getMouseX();
        }

        @Override
        public int getMouseY() {
            return e.getMouseY();
        }

        public static class Post extends ForgeZScreen.Render implements ZScreen.Render.Post {
            public Post(ScreenEvent.Render e) {
                super(e);
            }
        }
    }
}
