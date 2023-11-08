package vazkii.zetaimplforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import vazkii.zeta.client.event.ZScreen;

import java.util.List;

public class ForgeZScreen implements ZScreen {
    private final ScreenEvent e;

    public ForgeZScreen(ScreenEvent e) {
        this.e = e;
    }

    @Override
    public Screen getScreen() {
        return e.getScreen();
    }

    public static class Init extends ForgeZScreen implements ZScreen.Init {
        private final ScreenEvent.Init e;

        public Init(ScreenEvent.Init e) {
            super(e);
            this.e = e;
        }

        @Override
        public List<GuiEventListener> getListenersList() {
            return e.getListenersList();
        }

        @Override
        public void addListener(GuiEventListener listener) {
            e.addListener(listener);
        }

        @Override
        public void removeListener(GuiEventListener listener) {
            e.removeListener(listener);
        }

        public static class Pre extends ForgeZScreen.Init implements ZScreen.Init.Pre {
            public Pre(ScreenEvent.Init.Pre e) {
                super(e);
            }
        }

        public static class Post extends ForgeZScreen.Init implements ZScreen.Init.Post {
            public Post(ScreenEvent.Init.Post e) {
                super(e);
            }
        }
    }

    public static class Render extends ForgeZScreen implements ZScreen.Render {
        private final ScreenEvent.Render e;

        public Render(ScreenEvent.Render e) {
            super(e);
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

        public static class Pre extends ForgeZScreen.Render implements ZScreen.Render.Pre {
            public Pre(ScreenEvent.Render e) {
                super(e);
            }
        }

        public static class Post extends ForgeZScreen.Render implements ZScreen.Render.Post {
            public Post(ScreenEvent.Render e) {
                super(e);
            }
        }
    }

    public static class MouseButtonPressed extends ForgeZScreen implements ZScreen.MouseButtonPressed {
        private final ScreenEvent.MouseButtonPressed e;

        public MouseButtonPressed(ScreenEvent.MouseButtonPressed e) {
            super(e);
            this.e = e;
        }

        @Override
        public int getButton() {
            return e.getButton();
        }

        @Override
        public double getMouseX() {
            return e.getMouseX();
        }

        @Override
        public double getMouseY() {
            return e.getMouseY();
        }

        @Override
        public boolean isCanceled() {
            return e.isCanceled();
        }

        @Override
        public void setCanceled(boolean cancel) {
            e.setCanceled(cancel);
        }

        public static class Pre extends ForgeZScreen.MouseButtonPressed implements ZScreen.MouseButtonPressed.Pre {
            public Pre(ScreenEvent.MouseButtonPressed.Pre e) {
                super(e);
            }
        }

        public static class Post extends ForgeZScreen.MouseButtonPressed implements ZScreen.MouseButtonPressed.Post {
            public Post(ScreenEvent.MouseButtonPressed.Post e) {
                super(e);
            }
        }
    }

    public static class MouseScrolled extends ForgeZScreen implements ZScreen.MouseScrolled {
        private final ScreenEvent.MouseScrolled e;

        public MouseScrolled(ScreenEvent.MouseScrolled e) {
            super(e);
            this.e = e;
        }

        @Override
        public double getScrollDelta() {
            return e.getScrollDelta();
        }

        @Override
        public boolean isCanceled() {
            return e.isCanceled();
        }

        @Override
        public void setCanceled(boolean cancel) {
            e.setCanceled(cancel);
        }

        public static class Pre extends ForgeZScreen.MouseScrolled implements ZScreen.MouseScrolled.Pre {
            public Pre(ScreenEvent.MouseScrolled.Pre e) {
                super(e);
            }
        }

        public static class Post extends ForgeZScreen.MouseScrolled implements ZScreen.MouseScrolled.Post {
            public Post(ScreenEvent.MouseScrolled.Post e) {
                super(e);
            }
        }
    }

    public static class KeyPressed extends ForgeZScreen implements ZScreen.KeyPressed {
        private final ScreenEvent.KeyPressed e;

        public KeyPressed(ScreenEvent.KeyPressed e) {
            super(e);
            this.e = e;
        }

        @Override
        public int getKeyCode() {
            return e.getKeyCode();
        }

        @Override
        public int getScanCode() {
            return e.getScanCode();
        }

        @Override
        public int getModifiers() {
            return e.getModifiers();
        }

        @Override
        public boolean isCanceled() {
            return e.isCanceled();
        }

        @Override
        public void setCanceled(boolean cancel) {
            e.setCanceled(cancel);
        }

        public static class Pre extends ForgeZScreen.KeyPressed implements ZScreen.KeyPressed.Pre {
            public Pre(ScreenEvent.KeyPressed.Pre e) {
                super(e);
            }
        }

        public static class Post extends ForgeZScreen.KeyPressed implements ZScreen.KeyPressed.Post {
            public Post(ScreenEvent.KeyPressed.Post e) {
                super(e);
            }
        }
    }

    public static class CharacterTyped extends ForgeZScreen implements ZScreen.CharacterTyped {
        private final ScreenEvent.CharacterTyped e;

        public CharacterTyped(ScreenEvent.CharacterTyped e) {
            super(e);
            this.e = e;
        }

        @Override
        public char getCodePoint() {
            return e.getCodePoint();
        }

        @Override
        public int getModifiers() {
            return e.getModifiers();
        }

        @Override
        public boolean isCanceled() {
            return e.isCanceled();
        }

        @Override
        public void setCanceled(boolean cancel) {
            e.setCanceled(true);
        }

        public static class Pre extends ForgeZScreen.CharacterTyped implements ZScreen.CharacterTyped.Pre {
            public Pre(ScreenEvent.CharacterTyped.Pre e) {
                super(e);
            }
        }

        public static class Post extends ForgeZScreen.CharacterTyped implements ZScreen.CharacterTyped.Post {
            public Post(ScreenEvent.CharacterTyped.Post e) {
                super(e);
            }
        }
    }

    public static class Opening extends ForgeZScreen implements ZScreen.Opening {
        private final ScreenEvent.Opening e;

        public Opening(ScreenEvent.Opening e) {
            super(e);
            this.e = e;
        }

        @Override
        public Screen getCurrentScreen() {
            return e.getCurrentScreen();
        }

        @Override
        public Screen getNewScreen() {
            return e.getNewScreen();
        }

        @Override
        public void setNewScreen(Screen newScreen) {
            e.setNewScreen(newScreen);
        }

        @Override
        public boolean isCanceled() {
            return e.isCanceled();
        }

        @Override
        public void setCanceled(boolean cancel) {
            e.setCanceled(true);
        }
    }
}
