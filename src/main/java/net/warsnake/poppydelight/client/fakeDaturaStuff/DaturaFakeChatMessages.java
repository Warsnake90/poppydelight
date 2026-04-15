package net.warsnake.poppydelight.client.fakeDaturaStuff;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.warsnake.poppydelight.effect.ModEffects;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class DaturaFakeChatMessages {

    private static final Random random = new Random();

    private static final String[] FAKE_MESSAGES = {
            "wya?",
            "Come here",
            "Are you good?",
            "yo, wait",
            "wsg",
            "It's still here",
            "shut up",
            "I know",
            "I can see you",
            "Why did you say that?",
            "you move that yet?",
            "where are you",
            "what are you doing",
            "wait",
            "hold on",
            "come here",
            "look at this",
            "why did you go there",
            "i didnt say that",
            "what do you mean",
            "im not moving",
            "stop for a second",
            "you see that too right",
            "ok wait",
            "thats not right",
            "something changed",
            "did you hear that",
            "why is it like that",
            "thats new",
            "i dont remember that",
            "when did that get there",
            "you left it open",
            "close that",
            "leave it",
            "just wait",
            "its fine",
            "nevermind",
            "ignore that",
            "why did you stop",
            "you just moved",
            "you were just here",
            "you walked past it",
            "you missed it",
            "you dropped something",
            "you left it behind",
            "dont turn around",
            "keep going",
            "stop moving",
            "youre going the wrong way",
            "thats not where you were",
            "you changed it",
            "why did you do that",
            "you opened it",
            "you closed it",
            "you saw it right",
            "you hesitated",
            "you heard that",
            "you didnt see that",
            "you shouldn't be here",
            "you're not supposed to be here",
            "its still there",
            "it didnt move",
            "it moved again",
            "it went behind you",
            "its closer now",
            "it stopped",
            "its watching",
            "its gone now",
            "it wasnt there before",
            "thats not the same one",
            "theres another one",
            "theres two now",
            "it followed you",
            "it came back",
            "its not gone",
            "it never left",
            "you let it out",
            "you saw it too right",
            "no i didnt",
            "i didnt touch it",
            "i already told you",
            "thats not what i said",
            "stop asking",
            "i said no",
            "i said yes",
            "just do it",
            "then dont",
            "why would i do that",
            "you told me to",
            "you said that",
            "thats what you said",
            "i did already",
            "i havent yet",
            "im doing it now",
            "come back",
            "where did you go",
            "im right here",
            "you ran past me",
            "turn around",
            "wrong way",
            "other side",
            "not there",
            "over here",
            "behind you",
            "next to you",
            "youre close",
            "almost",
            "wait there",
            "dont move",
            "move now",
            "go now",
            "not yet",
            "you already did this",
            "we just did that",
            "this already happened",
            "why are we back here",
            "didnt we just",
            "you said that already",
            "you said that twice",
            "this is the same",
            "nothing changed",
            "it reset",
            "it didnt save",
            "it went back",
            "its looping",
            "wait its looping",
            "again",
            "its fine just dont look",
            "just ignore it",
            "leave it alone",
            "dont interact with it",
            "its not important",
            "it doesnt matter",
            "just keep going",
            "its normal",
            "this is normal",
            "nothing is wrong",
            "everything is fine",
            "just dont think about it",
            "youre fine",
            "its fine now",
            "wait",
            "no",
            "stop",
            "dont",
            "again",
            "there",
            "here",
            "behind",
            "now",
            "look",
            "listen",
            "its back",
            "still there",
            "gone",
            "not gone"
    };

    private static int cooldown = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Player player = mc.player;

        if (!player.hasEffect((MobEffect) ModEffects.DATURA.get())) return;

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        if (random.nextFloat() > 0.01f) return;

        var connection = mc.getConnection();
        if (connection == null) return;

        var players = connection.getOnlinePlayers();
        if (players.isEmpty() || FAKE_MESSAGES.length == 0) return;

        var info = players.stream()
                .filter(p -> p.getProfile() != null)
                .toList()

                .get(random.nextInt(players.size()));

        String name = info.getProfile().getName();
        String message = FAKE_MESSAGES[random.nextInt(FAKE_MESSAGES.length)];

        mc.gui.getChat().addMessage(Component.literal("<" + name + "> ").append(Component.literal(message)));

        cooldown = 400 + random.nextInt(500);
    }
}