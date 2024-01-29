package pistonmc.gliding.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import pistonmc.gliding.ModMain;
import pistonmc.gliding.PacketFallCancel;
import pistonmc.gliding.api.IPlayerGliding;

/**
 * Main gliding logic
 */
@SideOnly(Side.CLIENT)
public class GliderClient {

    public static final float MIN_MOTION_Y = -0.2f;
    public static final float EPSILON = 0.005f;

    public static void tickGliding(EntityPlayer player) {
        // only need to update motion on the client
        if (player instanceof EntityPlayerSP playersp) {
            IPlayerGliding playerGliding = (IPlayerGliding) playersp;
            boolean canGlide = checkCanGlide(playersp, playerGliding.isGliding());
            playerGliding.setGliding(canGlide);
            if (canGlide) {
                updateGlidingMotion(playersp, false);
            }
        }
    }

    public static boolean checkCanGlide(EntityPlayer player, boolean isGliding) {
        if (player.capabilities.isFlying || player.motionY >= MIN_MOTION_Y) {
            return false;
        }
        // TODO: check item

        // TODO: check dimension
        //
        // TODO: check not in water and not in ground
        if (!isGliding) {
            // falling over 1 block then can start gliding.
            // prevent gliding from jumping
            return player.fallDistance > 1;
        }
        return true;
    }

    public static void updateGlidingMotion(EntityPlayerSP player, boolean skipFallDamageCheck) {
        
        // minimum vertical speed to trigger gliding
        final float DEG2RAD = (float)Math.PI / 180;

        final float minHorizontalSpeed = 0.5f;
        final float horizontalAccel = 0.1f;
        final float dragConstantVertical = 0.5f;
        final float dragConstantHorizontal = 0.05f;
        final float turningConstant = 2f * DEG2RAD;
        final float turningConstraint = 0.707f; // 45 degrees

        float motionX = (float) player.motionX;
        float motionY = (float) player.motionY;
        float motionZ = (float) player.motionZ;


        final float lookAngleY = Math.abs(player.rotationPitch) * DEG2RAD;
        final float cosLookAngleY = MathHelper.cos(lookAngleY);
        final float sinLookAngleY = MathHelper.sin(lookAngleY);
        final float verticalSpeedSquared = motionY * motionY;
        final float horizontalSpeedSquared = motionX * motionX + motionZ * motionZ;

        boolean cancelFallDamage = false;
        if (!skipFallDamageCheck && player.fallDistance > 3) {
            // threshold for min vertical speed to trigger fall damage
            final float thresholdVertical = 0.25f;
            if (verticalSpeedSquared < thresholdVertical) {
                cancelFallDamage = true;
            } else {
                // threshold for ((vertical speed)/(horizontal speed)) ^ 2 to trigger fall damage
                // this is around 50 degrees
                final float threshold = 1.44f;
                if (verticalSpeedSquared / horizontalSpeedSquared < threshold) {
                    cancelFallDamage = true;
                }
            }
        }

        if (cancelFallDamage) {
            player.fallDistance = 0;
            ModMain.NETWORK.sendToServer(new PacketFallCancel());
        }


        float lookUnitX = MathHelper.sin(-player.rotationYaw * (float) Math.PI/180);
        float lookUnitZ = MathHelper.cos(-player.rotationYaw * (float) Math.PI/180);

        // drag related to velocity squared
        final float dragVertical = dragConstantVertical * cosLookAngleY * verticalSpeedSquared;
        final float dragHorizontal = dragConstantHorizontal * sinLookAngleY * horizontalSpeedSquared;

        // vertical slowdown
        motionY = Math.min(motionY + dragVertical, MIN_MOTION_Y);

        // horizontal forward movement
        final float horizontalSpeed = MathHelper.sqrt_float(horizontalSpeedSquared);
        if (Math.abs(horizontalSpeed) > EPSILON) {
            final float targetSpeed = Math.max(minHorizontalSpeed * cosLookAngleY, horizontalSpeed - dragHorizontal);
            float newHorizontalSpeed = horizontalSpeed;
            if (Math.abs(horizontalSpeed - targetSpeed) >= EPSILON) {
                if (horizontalSpeed > targetSpeed) {
                    newHorizontalSpeed = horizontalSpeed - horizontalAccel * cosLookAngleY;
                } else {
                    newHorizontalSpeed = horizontalSpeed + horizontalAccel * cosLookAngleY;
                }
                final float ratio = newHorizontalSpeed / horizontalSpeed;
                motionX = motionX * ratio;
                motionZ = motionZ * ratio;
            }
            // turning, can only do if there is speed
            final float strafe = ((IPlayerGliding) player).getGlidingStrafe();
            if (Math.abs(strafe) >= EPSILON && Math.abs(newHorizontalSpeed) >= EPSILON) {
                // dot product 
                final float cosAngle = (lookUnitX * motionX + lookUnitZ * motionZ) / newHorizontalSpeed;
                if (cosAngle > turningConstraint) {
                    float turningAngle = strafe * (turningConstant / horizontalSpeed);
                    float cosTurningAngle = MathHelper.cos(turningAngle);
                    float sinTurningAngle = MathHelper.sin(turningAngle);
                    float newMotionX = motionX * cosTurningAngle + motionZ * sinTurningAngle;
                    motionZ = motionZ * cosTurningAngle - motionX * sinTurningAngle;
                    motionX = newMotionX;
                }
            }
        } else {
            // currently no speed, accelerate toward look direction
            final float newHorizontalSpeed = horizontalAccel * cosLookAngleY;
            motionX = newHorizontalSpeed * lookUnitX;
            motionZ = newHorizontalSpeed * lookUnitZ;
        }


        player.motionX = motionX;
        player.motionY = motionY;
        player.motionZ = motionZ;

    }

}
