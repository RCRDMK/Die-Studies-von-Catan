package de.uol.swp.client.account.event;

public class ChangeToCertainSizeEvent {

    final private double width;
    final private double height;

    public ChangeToCertainSizeEvent(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
