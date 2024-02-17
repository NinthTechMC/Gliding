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
            int level = getGliderLevel(playersp, playerGliding.isGliding());
            boolean gliding = level > 0;
            playerGliding.setGliding(gliding);
            if (gliding) {
                boolean hasAcrobatic = EnchantmentAcrobatic.isOnPlayer(player);
                int aerodynamicLevel = EnchantmentAerodynamic.getPlayerLevel(player);
                updateGlidingMotion(playersp, hasAcrobatic, level, aerodynamicLevel);
            }
        }
    }

    public static int getGliderLevel(EntityPlayer player, boolean isGliding) {
        if (player.capabilities.isFlying || player.motionY >= MIN_MOTION_Y) {
            return 0;
        }
        if (player.onGround || player.isInWater() || player.handleLavaMovement()) {
            return 0;
        }

        if (!isGliding) {
            // falling over some distance then can start gliding.
            // prevent gliding from jumping
            if (player.fallDistance <= 1.5) {
                return 0;
            }
            // check dimension
            int dim = player.worldObj.provider.dimensionId;
            if (!Config.glideableDimensions.contains(dim)) {
                return 0;
            }
        }
        return EnchantmentGlider.getPlayerLevel(player);
    }

    public static void updateGlidingMotion(EntityPlayerSP player, boolean hasAcrobatic, int gliderLevel, int aerodynamicLevel) {
        
        final float DEG2RAD = (float)Math.PI / 180;

        final float minHorizontalSpeed = 0.5f + 0.1f * (gliderLevel + aerodynamicLevel);
        final float horizontalAccel = 0.1f;
        final float dragConstantVertical = 0.5f + 0.2f * gliderLevel;
        final float dragConstantHorizontal = 0.05f - 0.005f * aerodynamicLevel;
        final float turningConstant = (hasAcrobatic ? 5f : 2f) * DEG2RAD;
        final float turningConstraint = hasAcrobatic ? 0 : 0.707f; // cos(45 degrees)

        float motionX = (float) player.motionX;
        float motionY = (float) player.motionY;
        float motionZ = (float) player.motionZ;


        final float lookAngleY = Math.abs(player.rotationPitch) * DEG2RAD;
        final float cosLookAngleY = MathHelper.cos(lookAngleY);
        final float sinLookAngleY = MathHelper.sin(lookAngleY);
        final float verticalSpeedSquared = motionY * motionY;
        final float horizontalSpeedSquared = motionX * motionX + motionZ * motionZ;

        boolean cancelFallDamage = false;
        if (!hasAcrobatic && player.fallDistance > 3) {
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


        final float lookUnitX = MathHelper.sin(-player.rotationYaw * (float) Math.PI/180);
        final float lookUnitZ = MathHelper.cos(-player.rotationYaw * (float) Math.PI/180);

        // drag related to velocity squared
        final float totalSpeedSquared = verticalSpeedSquared + horizontalSpeedSquared;
        final float dragVertical = dragConstantVertical * cosLookAngleY * totalSpeedSquared;
        final float dragHorizontal = dragConstantHorizontal * sinLookAngleY * totalSpeedSquared;

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
            if (Math.abs(newHorizontalSpeed) >= EPSILON) {
                final float cosAngle = (lookUnitX * motionX + lookUnitZ * motionZ) / newHorizontalSpeed;
                if (cosAngle > turningConstraint) {
                    // find turning direction using cross product
                    final float sinAngle = (lookUnitX * motionZ - lookUnitZ * motionX) / newHorizontalSpeed;
                    if (Math.abs(sinAngle) > EPSILON) {
                        final float turningAngle = sinAngle * (turningConstant / newHorizontalSpeed);
                        final float sinTurningAngle = MathHelper.sin(turningAngle);
                        final float cosTurningAngle = MathHelper.cos(turningAngle);
                        final float newMotionX = motionX * cosTurningAngle + motionZ * sinTurningAngle;
                        motionZ = motionZ * cosTurningAngle - motionX * sinTurningAngle;
                        motionX = newMotionX;
                    }
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
