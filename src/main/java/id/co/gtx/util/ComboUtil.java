package id.co.gtx.util;

import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

public class ComboUtil {

    public static void selectItemById(Combobox combobox, String id) {
        for (Comboitem comboitem : combobox.getItems()) {
            String value = comboitem.getLabel();
            if (value.split("-")[0].trim().equals(id)) {
                combobox.setSelectedItem(comboitem);
                return;
            }
        }
        throw new IllegalArgumentException("Cannot find comboitem with id " +  id);
    }
}
