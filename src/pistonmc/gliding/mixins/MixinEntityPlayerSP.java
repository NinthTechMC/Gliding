package pistonmc.gliding.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import pistonmc.gliding.api.IPlayerGliding;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP implements IPlayerGliding {

    private boolean isGliding;
    private float glidingStrafe;

    @Override
    public boolean isGliding() {
        return this.isGliding;
    }

    @Override
    public void setGliding(boolean gliding) {
        this.isGliding = gliding;
    }

    @Override
    public float getGlidingStrafe() {
        return this.glidingStrafe;
    }

    @Override
    public void setGlidingStrafe(float strafe) {
        this.glidingStrafe = strafe;
    }

    @WrapOperation(
        method = "onLivingUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V"
        )
    )
    public void updateFlyingMovementInput(MovementInput input, Operation<Void> op) {
        if (this.isGliding) {
            input.moveStrafe = this.glidingStrafe;
        }
        op.call(input);
        if (this.isGliding) {
            this.glidingStrafe = input.moveStrafe;
            input.moveStrafe = 0;
            input.moveForward = 0;
            input.jump = false;
            input.sneak = false;
        }
    }
}
