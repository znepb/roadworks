package me.znepb.roadworks.mixin;

import me.znepb.roadworks.RoadworksRegistry;
import me.znepb.roadworks.attachment.LinkableAttachment;
import me.znepb.roadworks.container.PostContainerBlockEntity;
import me.znepb.roadworks.train.CrossingGateAttachment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(DebugHud.class)
public class DebugScreenMixin {
    @Inject(at=@At("RETURN"), method = "getRightText()Ljava/util/List;")
    public void getRightText(CallbackInfoReturnable<List<String>> cir) {
        var value = cir.getReturnValue();
        var client = MinecraftClient.getInstance();
        var hit = client.crosshairTarget;

        assert hit != null;
        if(hit.getType() == HitResult.Type.BLOCK) {
            assert client.world != null;

            var blockInWorld = client.world.getBlockEntity(((BlockHitResult) hit).getBlockPos());
            if(blockInWorld instanceof PostContainerBlockEntity) {
                var attachment = ((PostContainerBlockEntity) blockInWorld).getAttachmentHit((BlockHitResult) hit);
                if(attachment != null) {
                    value.add("");
                    value.add(Formatting.UNDERLINE + "[Roadworks] Targeted Attachment");
                    value.add(Objects.requireNonNull(RoadworksRegistry.ModAttachments.INSTANCE.getREGISTRY().getId(attachment.getType())).toString());
                    value.add(String.valueOf(attachment.getId()));
                    value.add("Facing: " + attachment.getFacing().asString());

                    if(attachment instanceof LinkableAttachment) {
                        value.add("Linked: " + ((LinkableAttachment) attachment).getLinked());
                        if(((LinkableAttachment) attachment).getLinked()) {
                            value.add("Link position: " + ((LinkableAttachment) attachment).getLinkPosition());
                        }
                    }

                    if(attachment instanceof CrossingGateAttachment) {
                        value.add("Gate progress: " + ((CrossingGateAttachment) attachment).getProgress());
                        value.add("Active: " + ((CrossingGateAttachment) attachment).isActive());
                        value.add("In motion: " + ((CrossingGateAttachment) attachment).isInMotion());
                    }
                }
            }
        }
    }
}
