package net.warsnake.poppydelight;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.warsnake.poppydelight.effect.ModEffects;

import java.util.Random;

public class MessageGarblerEvent {

    private static final String[] SLUR_REPLACEMENTS = {
            // th → d or f
            // s → sh
            // r → w
            // Hard consonants softened
    };

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!hasAnyGarbleEffect(mc.player)) return;

        String original = event.getMessage();
        event.setMessage(slur(original, mc.player));
    }

    private boolean hasAnyGarbleEffect(Player player) {
        return player.hasEffect((MobEffect) ModEffects.SHROOMHIGH.get())
                || player.hasEffect((MobEffect) ModEffects.POTHIGH.get())
                || player.hasEffect((MobEffect) ModEffects.BADSHROOMHIGH.get())
                || player.hasEffect((MobEffect) ModEffects.OPIUMHIGH.get())
                || player.hasEffect((MobEffect) ModEffects.TUNNELVISION.get())
                || player.hasEffect((MobEffect) ModEffects.DATURA.get());
    }

    private String slur(String input, Player player) {
        float intensity = getIntensity(player);
        Random random = new Random();
        String result = input;

        boolean isShroom    = player.hasEffect((MobEffect) ModEffects.SHROOMHIGH.get());
        boolean isOpium     = player.hasEffect((MobEffect) ModEffects.OPIUMHIGH.get());
        boolean isBadShroom = player.hasEffect((MobEffect) ModEffects.BADSHROOMHIGH.get());
        boolean isTunnel    = player.hasEffect((MobEffect) ModEffects.TUNNELVISION.get());
        boolean isPot       = player.hasEffect((MobEffect) ModEffects.POTHIGH.get());
        boolean isDatura       = player.hasEffect((MobEffect) ModEffects.DATURA.get());

        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)th", random.nextBoolean() ? "d" : "f");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)\\br", "w");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)r(?=[aeiou])", "w");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)s(?=[^h])", "sh");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)ck", "k");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)ing\\b", "in");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)\\bthe\\b", "da");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)\\byou\\b", "u");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)\\bwith\\b", "wif");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)\\bthis\\b", "dis");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)\\bthat\\b", "dat");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)ght\\b", "t");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)tion\\b", "shun");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)wh", "w");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)\\bfor\\b", "fer");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)\\bvery\\b", "vewwy");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)\\bdon't\\b", "dunt");
        if (random.nextFloat() < intensity) result = result.replaceAll("(?i)\\bgoing\\b", "goin");

        // Vowel elongation
        if (random.nextFloat() < intensity * 0.5f) {
            StringBuilder sb = new StringBuilder(result);
            for (int i = sb.length() - 1; i >= 0; i--) {
                if ("aeiou".indexOf(Character.toLowerCase(sb.charAt(i))) >= 0
                        && random.nextFloat() < intensity * 0.3f) {
                    sb.insert(i + 1, sb.charAt(i));
                }
            }
            result = sb.toString();
        }

        // Lowercase drift
        if (random.nextFloat() < intensity * 0.6f) {
            result = result.toLowerCase();
        }

        if (isShroom) {
            String[] laughs = {"haha", "lmao", "hahaha", "lol", "hehe", "LOL", "HAHA", "pfft", "hahahaha"};
            if (random.nextFloat() < 0.6f) {
                String[] words = result.split(" ");
                int insertAt = random.nextInt(words.length);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    if (i == insertAt) sb.append(laughs[random.nextInt(laughs.length)]).append(" ");
                    sb.append(words[i]).append(" ");
                }
                result = sb.toString().trim();
            }

            if (random.nextFloat() < 0.5f) {
                String[] trailingLaughs = {"lol", "haha", "heh", "lmaooo", "hahahaha"};
                result = result + " " + trailingLaughs[random.nextInt(trailingLaughs.length)];
            }

            String[] tangents = {
                    "wait what was i saying",
                    "bro i forgot",
                    "this is so funny",
                    "ok ok ok",
                    "no but actually though",
                    "wait hold on",
                    "im fine im fine",
                    "dude dude dude",
                    "oh my god",
                    "no i swear",
                    "cmon",
                    "haha"
            };
            if (random.nextFloat() < 0.45f) {
                result = result + " " + tangents[random.nextInt(tangents.length)];
            }

            if (random.nextFloat() < 0.5f) {
                String[] words = result.split(" ");
                StringBuilder sb = new StringBuilder();
                for (String word : words) {
                    if (word.length() > 3 && random.nextFloat() < 0.35f) {
                        int idx = 1 + random.nextInt(word.length() - 2);
                        char[] wc = word.toCharArray();
                        char tmp = wc[idx]; wc[idx] = wc[idx - 1]; wc[idx - 1] = tmp;
                        sb.append(new String(wc));
                    } else {
                        sb.append(word);
                    }
                    sb.append(" ");
                }
                result = sb.toString().trim();
            }
        }


        if (isOpium) {
            if (random.nextFloat() < 0.6f) {
                String[] words = result.split(" ");
                if (words.length > 2) {
                    int insertAt = 1 + random.nextInt(words.length - 1);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < words.length; i++) {
                        sb.append(words[i]).append(" ");
                        if (i == insertAt) sb.append("... ");
                    }
                    result = sb.toString().trim();
                }
            }

            // Trailing off entirely
            if (random.nextFloat() < 0.4f && result.length() > 8) {
                int cutAt = (int)(result.length() * (0.4f + random.nextFloat() * 0.4f));
                result = result.substring(0, cutAt) + "...";
            }

            // Slow filler words
            String[] fillers = {"...yeah", "mmm", "...wait", "...hm", "...ugh", "...so"};
            if (random.nextFloat() < 0.5f) {
                result = fillers[random.nextInt(fillers.length)] + " " + result;
            }

            // Extra drawn-out vowels on random words
            if (random.nextFloat() < 0.5f) {
                String[] words = result.split(" ");
                int idx = random.nextInt(words.length);
                String word = words[idx];
                for (int i = 0; i < word.length(); i++) {
                    if ("aeiou".indexOf(Character.toLowerCase(word.charAt(i))) >= 0) {
                        words[idx] = word.substring(0, i + 1)
                                + String.valueOf(word.charAt(i)).repeat(3)
                                + word.substring(i + 1);
                        break;
                    }
                }
                result = String.join(" ", words);
            }

            result = result.toLowerCase();
        }

        if (isBadShroom) {
            // Insert random nonsense words
            String[] nonsense = {
                    "flarb", "wzzzt", "nnng", "uhhHH", "the walls", "wait", "no",
                    "WHAT", "huh", "guh", "bluh", "it's", "they're", "i can see",
                    "is that", "WHO", "bzzz", "aaaa", "oh no", "oh god"
            };
            if (random.nextFloat() < 0.7f) {
                String[] words = result.split(" ");
                StringBuilder sb = new StringBuilder();
                for (String word : words) {
                    sb.append(word).append(" ");
                    if (random.nextFloat() < 0.25f) {
                        sb.append(nonsense[random.nextInt(nonsense.length)]).append(" ");
                    }
                }
                result = sb.toString().trim();
            }

            // Random characters
            if (random.nextFloat() < 0.6f) {
                char[] glitchChars = {'@', '#', '?', '!', '*', 'z', 'x', 'q', 'Z', 'X'};
                StringBuilder sb = new StringBuilder(result);
                int inserts = 1 + random.nextInt(3);
                for (int i = 0; i < inserts; i++) {
                    int pos = random.nextInt(sb.length());
                    sb.insert(pos, glitchChars[random.nextInt(glitchChars.length)]);
                }
                result = sb.toString();
            }

            // Capitalization spiking (random caps mid-word)
            if (random.nextFloat() < 0.5f) {
                StringBuilder sb = new StringBuilder(result);
                for (int i = 0; i < sb.length(); i++) {
                    if (Character.isLetter(sb.charAt(i)) && random.nextFloat() < 0.15f) {
                        sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
                    }
                }
                result = sb.toString();
            }

            // Duplicate a random word (intrusive thought looping)
            if (random.nextFloat() < 0.4f) {
                String[] words = result.split(" ");
                if (words.length > 1) {
                    int idx = random.nextInt(words.length);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < words.length; i++) {
                        sb.append(words[i]).append(" ");
                        if (i == idx) sb.append(words[idx]).append(" ").append(words[idx]).append(" ");
                    }
                    result = sb.toString().trim();
                }
            }
        }


        if (isTunnel) {
            String[] words = result.split(" ");

            // Pick a word and repeat it several times
            if (words.length > 0 && random.nextFloat() < 0.65f) {
                int idx = random.nextInt(words.length);
                String loopWord = words[idx];
                int repeats = 2 + random.nextInt(3);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    sb.append(words[i]).append(" ");
                    if (i == idx) {
                        for (int r = 0; r < repeats; r++) {
                            sb.append(loopWord).append(" ");
                        }
                    }
                }
                result = sb.toString().trim();
            }

            // Repeat the entire message back-to-back
            if (random.nextFloat() < 0.3f) {
                result = result + " " + result;
            }

            // Stutter on first letter (stuck in a loop)
            if (random.nextFloat() < 0.5f && result.length() > 1) {
                char first = result.charAt(0);
                result = first + "-" + first + "-" + first + "-" + result;
            }

            // End with a looping phrase
            String[] loops = {"wait", "wait wait", "what was i", "huh", "i said", "no but", "again"};
            if (random.nextFloat() < 0.4f) {
                result = result + " " + loops[random.nextInt(loops.length)];
            }
        }

        if (isPot) {
            String[] fillers = {"uh", "um", "like", "uhhh", "wait", "no uh", "i mean", "bro", "dude", "ok so"};
            if (random.nextFloat() < 0.5f) {
                String[] words = result.split(" ");
                if (words.length > 1) {
                    int insertAt = random.nextInt(words.length);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < words.length; i++) {
                        if (i == insertAt) sb.append(fillers[random.nextInt(fillers.length)]).append(" ");
                        sb.append(words[i]).append(" ");
                    }
                    result = sb.toString().trim();
                }
            }

            // Stutter
            if (random.nextFloat() < 0.3f && result.length() > 1) {
                char first = result.charAt(0);
                result = first + "-" + first + "-" + result;
            }

            // Extra spaces (sloppy typing)
            if (random.nextFloat() < 0.3f) {
                result = result.replace(" ", random.nextBoolean() ? "  " : " ");
            }
        }

        if (isDatura) {
            // ========================
            // DATURA — coherent but wrong reality
            // ========================

            // Phantom words
            String[] phantomWords = {
                    "someone", "behind", "its", "fine", "they said", "dont move",
                    "he told me", "its watching", "you dropped it", "wait here",
                    "its still there", "not yet", "you missed it"
            };
            if (random.nextFloat() < 0.6f) {
                String[] words = result.split(" ");
                if (words.length > 1) {
                    int insertAt = random.nextInt(words.length);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < words.length; i++) {
                        sb.append(words[i]).append(" ");
                        if (i == insertAt) {
                            sb.append(phantomWords[random.nextInt(phantomWords.length)]).append(" ");
                        }
                    }
                    result = sb.toString().trim();
                }
            }

            // Thought derailment (losing track mid-sentence)
            String[] derail = {
                    "wait", "what was i doing", "no thats not it",
                    "hold on", "i need to check something", "why is that there"
            };
            if (random.nextFloat() < 0.5f && result.length() > 10) {
                int cut = 3 + random.nextInt(result.length() / 2);
                result = result.substring(0, cut) + " ... " + derail[random.nextInt(derail.length)];
            }

            // Second-person intrusion
            String[] intrusion = {
                    "you can see it right",
                    "dont look behind you",
                    "you hear that",
                    "its next to you",
                    "you left it there",
                    "why didnt you move"
            };
            if (random.nextFloat() < 0.5f) {
                result = result + " " + intrusion[random.nextInt(intrusion.length)];
            }

            // Confidently wrong substitutions (reality distortion)
            if (random.nextFloat() < 0.5f) {
                result = result.replaceAll("(?i)\\bdoor\\b", "window");
                result = result.replaceAll("(?i)\\bwindow\\b", "door");
                result = result.replaceAll("(?i)\\bfriend\\b", "man");
                result = result.replaceAll("(?i)\\bhome\\b", "here");
                result = result.replaceAll("(?i)\\bhere\\b", "there");
                result = result.replaceAll("(?i)\\bthere\\b", "here");
            }

            // Verb swapping (common verbs replaced with plausible wrong ones)
            if (random.nextFloat() < 0.6f) {
                String[][] verbs = {
                        {"go", "stay"},
                        {"stay", "leave"},
                        {"leave", "wait"},
                        {"look", "touch"},
                        {"take", "drop"},
                        {"drop", "take"},
                        {"open", "close"},
                        {"close", "open"},
                        {"run", "walk"},
                        {"walk", "stop"},
                        {"stop", "keep going"},
                        {"keep", "lose"},
                        {"lose", "keep"}
                };

                for (String[] pair : verbs) {
                    if (random.nextFloat() < 0.3f) {
                        result = result.replaceAll("(?i)\\b" + pair[0] + "\\b", pair[1]);
                    }
                }
            }

            // False reply (responding to something that never existed)
            String[] falseReplies = {
                    "yeah i see it",
                    "no i didnt touch it",
                    "stop",
                    "i already moved it",
                    "its not there anymore",
                    "i told you already"
            };
            if (random.nextFloat() < 0.45f) {
                result = result + " " + falseReplies[random.nextInt(falseReplies.length)];
            }

            // Duplicate sentence (false memory loop, subtle)
            if (random.nextFloat() < 0.25f) {
                result = result + " " + result;
            }

            // Full override (loss of intent)
            String[] overrides = {
                    "its fine",
                    "im not here",
                    "dont worry about it",
                    "i already did",
                    "its gone now",
                    "nothing happened",
                    "youre fine"
            };
            if (random.nextFloat() < 0.25f) {
                result = overrides[random.nextInt(overrides.length)];
            }

            // Slight normalization (datura is less "stylized")
            if (random.nextFloat() < 0.5f) {
                result = result.toLowerCase();
            }
        }

        return result;
    }

    private float getIntensity(Player player) {
        float intensity = 0f;
        if (player.hasEffect((MobEffect) ModEffects.POTHIGH.get()))      intensity = Math.max(intensity, 0.3f);
        if (player.hasEffect((MobEffect) ModEffects.OPIUMHIGH.get()))    intensity = Math.max(intensity, 0.55f);
        if (player.hasEffect((MobEffect) ModEffects.TUNNELVISION.get())) intensity = Math.max(intensity, 0.4f);
        if (player.hasEffect((MobEffect) ModEffects.SHROOMHIGH.get()))   intensity = Math.max(intensity, 0.7f);
        if (player.hasEffect((MobEffect) ModEffects.BADSHROOMHIGH.get())) intensity = Math.max(intensity, 0.9f);
        return intensity;
    }
}