package org.violetmoon.quark.datagen;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.GsonHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;

//This is EXTREMELY WIP and nonfunctional - Partonetrain
public interface QuarkDataProvider extends DataProvider {

    ToIntFunction<String> FIXED_ORDER_FIELDS = Util.make(new Object2IntOpenHashMap<>(), map -> {
        map.put("quark:conditions", -1);
        map.put("type", 0);
        map.put("flag", 1);
        map.defaultReturnValue(2);
    });
    Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(FIXED_ORDER_FIELDS).thenComparing(s -> (String)s);

    static <T> CompletableFuture<?> saveStable2(CachedOutput cachedOutput, HolderLookup.Provider holderLookupProvider, Codec<T> codec, T t, Path path, String module, String flag) {
        RegistryOps<JsonElement> registryops = holderLookupProvider.createSerializationContext(JsonOps.INSTANCE);
        JsonElement jsonelement = codec.encodeStart(registryops, t).getOrThrow();
        return saveStable(cachedOutput, jsonelement, path, module, flag);
    }

    static CompletableFuture<?> saveStable(CachedOutput cachedOutput, JsonElement jsonElement, Path path, String module, String flag) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

                try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8))) {
                    jsonwriter.setSerializeNulls(false);
                    jsonwriter.setIndent(" ".repeat(java.lang.Math.max(0, INDENT_WIDTH.get()))); // Neo: Allow changing the indent width without needing to mixin this lambda.
                    GsonHelper.writeValue(jsonwriter, jsonElement, KEY_COMPARATOR);
                }

                cachedOutput.writeIfNeeded(path, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
            } catch (IOException ioexception) {
                LOGGER.error("Failed to save file to {}", path, ioexception);
            }
        }, Util.backgroundExecutor());
    }
}
