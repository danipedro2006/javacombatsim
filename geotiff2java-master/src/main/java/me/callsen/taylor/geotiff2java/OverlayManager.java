package me.callsen.taylor.geotiff2java;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class OverlayManager {

    private final List<Overlay> overlays = new ArrayList<>();

    /** Add overlay to manager */
    public void addOverlay(Overlay overlay) {
        overlays.add(overlay);
    }

    /** Draw all visible overlays */
    public void drawOverlays(Graphics2D g) {
        for (Overlay overlay : overlays) {
            if (overlay.isVisible()) {
                overlay.draw(g);
            }
        }
    }

    /** Get overlay by class type (optional helper) */
    @SuppressWarnings("unchecked")
    public <T extends Overlay> T getOverlay(Class<T> cls) {
        for (Overlay o : overlays) {
            if (cls.isInstance(o)) return (T) o;
        }
        return null;
    }
}

