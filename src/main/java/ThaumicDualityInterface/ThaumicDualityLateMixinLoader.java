package ThaumicDualityInterface;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

@LateMixin
public class ThaumicDualityLateMixinLoader implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.thaumicdualityinterface.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return Collections.singletonList("MixinDualityInterface");
    }
}
