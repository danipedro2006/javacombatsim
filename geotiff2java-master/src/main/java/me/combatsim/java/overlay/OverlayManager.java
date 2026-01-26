package me.combatsim.java.overlay;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class OverlayManager {

    private final List<Overlay> overlays = new ArrayList<>();

    /** Add overlay (order matters!) */
    public void addOverlay(Overlay overlay) {
        overlays.add(overlay);
    }

    /** Draw all visible overlays */
    public void drawOverlays(Graphics2D g) {
        for (Overlay o : overlays) {
            if (o.isVisible()) {
                o.draw(g);
            }
        }
    }

    /** Get overlay by type */
    @SuppressWarnings("unchecked")
    public <T extends Overlay> T get(Class<T> type) {
        for (Overlay o : overlays) {
            if (type.isInstance(o)) {
                return (T) o;
            }
        }
        return null;
    }

    public boolean hasActiveOverlays() {
        for (Overlay o : overlays) {
            if (o.isVisible()) {
                return true;
            }
        }
        return false;
    }
    public void setVisible(Class<? extends Overlay> type, boolean visible) {
        Overlay o = get(type);
        if (o != null) {
            o.setVisible(visible);
        }
    }

}
