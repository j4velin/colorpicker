package de.j4velin.lib.colorpicker;

public interface ColorView {

    interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    /**
     * Set the color this view should show.
     *
     * @param color    The color that should be selected.
     * @param callback If you want to get a callback to
     *                 your OnColorChangedListener.
     */
    void setColor(int color, boolean callback);

    /**
     * Set a OnColorChangedListener to get notified when the color
     * selected by the user has changed.
     *
     * @param listener
     */
    void setOnColorChangedListener(OnColorChangedListener listener);

    /**
     * Get the current color this view is showing.
     *
     * @return the current color.
     */
    int getColor();

    /**
     * Set if the user is allowed to adjust the alpha panel. Default is false.
     * If it is set to false no alpha will be set.
     *
     * @param visible
     */
    void setAlphaSliderVisible(boolean visible);

    boolean getAlphaSliderVisible();

    /**
     * Get the drawing offset of the color picker view.
     * The drawing offset is the distance from the side of
     * a panel to the side of the view minus the padding.
     * Useful if you want to have your own panel below showing
     * the currently selected color and want to align it perfectly.
     *
     * @return The offset in pixels.
     */
    float getDrawingOffset();
}
