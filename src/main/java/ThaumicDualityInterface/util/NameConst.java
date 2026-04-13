package ThaumicDualityInterface.util;

import java.util.ArrayList;

import net.minecraft.util.StatCollector;

import ThaumicDualityInterface.ThaumicDualityInterface;

public class NameConst {

    public static final String BLOCK_ESSENTIA_INTERFACE = "essentia_interface";
    public static final String BLOCK_ESSENTIA_PACKET_DECODER = "essentia_packet_decoder";
    public static final String ITEM_ESSENTIA_PACKET = "essentia_packet";
    public static final String ITEM_PART_ESSENTIA_INTERFACE = "part_essentia_interface";
    public static final String ITEM_PART_ESSENTIA_P2P_INTERFACE = "part_essentia_p2p_interface";

    public static final String MOD_AE2 = "appliedenergistics2";
    public static final String MOD_THE = "thaumicenergistics";

    public static final String AE2_MULTI_PART = "item.ItemMultiPart";
    public static final String AE2_INTERFACE_BLOCK = "tile.BlockInterface";
    public static final String AE2_MATERIAL = "item.ItemMultiMaterial";
    public static final String THE_ESSENTIA_PROVIDER = "thaumicenergistics.block.essentia.provider";

    public static final String RES_CAT_THE = "thaumicenergistics";
    public static final String RES_ESSENTIA_INTERFACE = "TDI_ESSENTIA_INTERFACE";
    public static final String RES_PARENT_ESS_PROV = "thaumicenergistics.TEESSPROV";
    public static final String RES_PAGE_PREFIX = "tc.research_page.";
    public static final String RES_PAGE_INT_1 = RES_PAGE_PREFIX + RES_ESSENTIA_INTERFACE + ".1";
    public static final String RES_PAGE_INT_2 = RES_PAGE_PREFIX + RES_ESSENTIA_INTERFACE + ".2";
    public static final String RES_PAGE_INT_3 = RES_PAGE_PREFIX + RES_ESSENTIA_INTERFACE + ".3";
    public static final String RES_PAGE_INT_4 = RES_PAGE_PREFIX + RES_ESSENTIA_INTERFACE + ".4";

    public static final String TT_KEY = ThaumicDualityInterface.MODID + ".tooltip.";
    public static final String TT_EMPTY = TT_KEY + "empty";
    public static final String TT_SHIFT_FOR_MORE = TT_KEY + "shift_for_more";
    public static final String TT_ESSENTIA_PACKET = TT_KEY + "essentia_packet";
    public static final String TT_INVALID_ESSENTIA = TT_KEY + "invalid_essentia";
    public static final String TT_ESSENTIA_PACKET_DECODER_DESC = TT_KEY + "essentia_packet_decoder.desc";
    public static final String TT_SWITCH_ESSENTIA_INTERFACE = TT_KEY + "switch_essentia_interface";

    public static final String GUI_KEY = ThaumicDualityInterface.MODID + ".gui.";
    public static final String GUI_ESSENTIA_INTERFACE = GUI_KEY + BLOCK_ESSENTIA_INTERFACE;
    public static final String GUI_ESSENTIA_PACKET_DECODER = GUI_KEY + BLOCK_ESSENTIA_PACKET_DECODER;

    public static final String TEX_AE2_P2P_TUNNEL = MOD_AE2 + ":ItemPart.P2PTunnel";

    public static String i18n(String t, String delimiter, boolean hint) {
        if (!hint) {
            return StatCollector.translateToLocal(t);
        }
        ArrayList<String> arr = new ArrayList<>();
        arr.add(StatCollector.translateToLocal(t));
        if (!StatCollector.translateToLocal(t + ".hint")
            .equals(t + ".hint")) {
            arr.add(StatCollector.translateToLocal(t + ".hint"));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.size(); i++) {
            sb.append(arr.get(i));
            if (i < arr.size() - 1) sb.append(delimiter);
        }
        return sb.toString();
    }

    public static String i18n(String t) {
        return i18n(t, "\n", true);
    }

    public static String i18n(String t, boolean warp) {
        if (warp) {
            return i18n(t);
        } else {
            return i18n(t, "", true);
        }
    }
}
