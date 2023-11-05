package vazkii.zeta.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import vazkii.zeta.event.bus.Cancellable;
import vazkii.zeta.event.bus.IZetaPlayEvent;

import java.util.List;

public interface ZScreen extends IZetaPlayEvent {
    Screen getScreen();

    interface Init extends IZetaPlayEvent, ZScreen {
        List<GuiEventListener> getListenersList();
        void addListener(GuiEventListener listener);
        void removeListener(GuiEventListener listener);

        interface Pre extends ZScreen, Init { }
        interface Post extends ZScreen, Init { }
    }

    interface Render extends IZetaPlayEvent, ZScreen {
        PoseStack getPoseStack();
        int getMouseX();
        int getMouseY();

        interface Pre extends ZScreen, Render { }
        interface Post extends ZScreen, Render { }
    }

    interface MouseButtonPressed extends IZetaPlayEvent, ZScreen, Cancellable {
        int getButton();
        double getMouseX();
        double getMouseY();

        interface Pre extends MouseButtonPressed { }
        interface Post extends MouseButtonPressed { }
    }

    interface KeyPressed extends IZetaPlayEvent, ZScreen, Cancellable {
        int getKeyCode();
        int getScanCode();
        int getModifiers();

        interface Pre extends KeyPressed { }
        interface Post extends KeyPressed { }
    }

    interface CharacterTyped extends IZetaPlayEvent, ZScreen, Cancellable {
        char getCodePoint();
        int getModifiers();

        interface Pre extends ZScreen, CharacterTyped { }
        interface Post extends ZScreen, CharacterTyped { }
    }
}
