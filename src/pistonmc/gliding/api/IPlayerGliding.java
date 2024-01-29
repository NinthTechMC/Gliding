package pistonmc.gliding.api;

public interface IPlayerGliding {
    boolean isGliding();
    void setGliding(boolean gliding);
    float getGlidingStrafe();
    void setGlidingStrafe(float strafe);
}
