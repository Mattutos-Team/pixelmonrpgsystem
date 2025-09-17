package com.mattutos.pixelmonrpgsystem.mixin;

import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.PokemonLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonLevel.class)
public class PokemonLevelMixin {

    // Shadowing the pokemon field to access it in the mixin
    @Shadow
    protected Pokemon pixelmon;

    @Inject(method = "canLevelUp", at = @At("HEAD"), cancellable = true)
    private void injectCanLevelUp(CallbackInfoReturnable<Boolean> cir) {
        // This mixin modifies the canLevelUp method of the PokemonLevel class to add a custom rule:
        PokemonLevel self = (PokemonLevel) (Object) this;
        if (pixelmon == null || pixelmon.getOwnerPlayer() == null) {
            return; // se não tiver dono, deixa a lógica normal seguir
        }

        ServerPlayer player = pixelmon.getOwnerPlayer();
        if (player == null) {
            return;
        }

        if (CapabilitiesRegistry.getPlayerRPGCapability(player) instanceof PlayerRPGCapability data) {
            if (self.getPokemonLevel() >= data.getLevel()) {
                cir.setReturnValue(false); // prevents level up if Pokémon level is greater than or equal to player level
            }
        }

    }
}
