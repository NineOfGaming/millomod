package net.millo.millomod.mod.features.gui;

public class ElementFadeIn {
    private float progress = 0f;
    private Direction direction;
    public ElementFadeIn(Direction direction) {
        this.direction = direction;
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

    public enum Direction {
        UP(0, 1),
        DOWN(0, -1),
        LEFT(-1, 0),
        RIGHT(1, 0);

        private int dx, dy;
        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public int getDx() {
            return dx;
        }

        public int getDy() {
            return dy;
        }
    }
}
