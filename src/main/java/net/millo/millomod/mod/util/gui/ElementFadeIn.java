package net.millo.millomod.mod.util.gui;

public class ElementFadeIn {
    private float progress = 0f;
    private final Direction direction;
    public ElementFadeIn(Direction direction) {
        this.direction = direction;
    }

    public ElementFadeIn(ElementFadeIn fade) {
        direction = fade.direction;
        progress = fade.progress;
    }

    public boolean fadeIn(float delta) {
        progress = Math.min(progress + delta * 0.2f, 1f);
        return progress >= 1f;
    }


    public int getXOffset() {
        return (int)(direction.dx * ((1f - progress) * 10f));
    }
    public int getYOffset() {
        return (int)(direction.dy * ((1f - progress) * 10f));
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public enum Direction {
        UP(0, -1),
        DOWN(0, 1),
        LEFT(-1, 0),
        RIGHT(1, 0);

        private final int dx, dy;
        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

    }
}
