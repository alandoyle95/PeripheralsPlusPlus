package mods.railcraft.api.tracks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

/**
 * Each type of Track has a single instance of TrackSpec that corresponds with
 * it. Each Track block in the world has a ITrackInstance that corresponds with
 * it.
 *
 * Take note of the difference (similar to block classes and tile entities
 * classes).
 *
 * TrackSpecs must be registered with the TrackRegistry in either the Pre-Init
 * or Init Phases.
 *
 * Track ItemStacks can be acquired from the TrackSpec, but you are required to
 * register a proper display name yourself (during Post-Init).
 *
 * @see TrackRegistry
 * @see ITrackInstance
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TrackSpec {

    @Deprecated
    public static int blockID = 0;
    public static Block blockTrack;
    private final String tag;
    private final short trackId;
    private final ITrackItemIconProvider iconProvider;
    private final Class<? extends ITrackInstance> instanceClass;

    /**
     * Defines a new track spec.
     *
     * @param trackId A unique identifier for the track type. 0-512 are reserved
     * for Railcraft. Capped at Short.MAX_VALUE
     * @param tag A unique internal string identifier (ex.
     * "track.speed.transition")
     * @param iconProvider The provider for Track item icons
     * @param instanceClass The ITrackInstance class that corresponds to this
     * TrackSpec
     * @see ITextureProvider
     */
    public TrackSpec(short trackId, String tag, ITrackItemIconProvider iconProvider, Class<? extends ITrackInstance> instanceClass) {
        this.trackId = trackId;
        this.tag = tag;
        this.iconProvider = iconProvider;
        this.instanceClass = instanceClass;
    }

    public String getTrackTag() {
        return tag;
    }

    public short getTrackId() {
        return trackId;
    }

    /**
     * This function will only work after the Init Phase.
     *
     * @return an ItemStack that can be used to place the track.
     */
    public ItemStack getItem() {
        return getItem(1);
    }

    /**
     * This function will only work after the Init Phase.
     *
     * @return an ItemStack that can be used to place the track.
     */
    public ItemStack getItem(int qty) {
        if(blockTrack != null){
            return new ItemStack(blockTrack, qty, getTrackId());
        }
        if (blockID > 0) {
            return new ItemStack(blockID, qty, getTrackId());
        }
        return null;
    }

    public ITrackInstance createInstanceFromSpec() {
        try {
            return (ITrackInstance) instanceClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Improper Track Instance Constructor");
        }
    }

    public Icon getIcon() {
        return iconProvider.getTrackItemIcon(this);
    }
}
