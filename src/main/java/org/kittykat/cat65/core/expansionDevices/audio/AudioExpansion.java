package org.kittykat.cat65.core.expansionDevices.audio;

import org.kittykat.cat65.core.expansionDevices.ExpansionDevice;

public abstract class AudioExpansion extends ExpansionDevice {
    public AudioExpansion(int mirrorAddressMask, int port) {
        super(mirrorAddressMask, port);
    }

    public abstract float getAudioSample();

    protected static float digitalToAnalog4Bit(int bits) {
        return (bits / 15f);
    }
}
