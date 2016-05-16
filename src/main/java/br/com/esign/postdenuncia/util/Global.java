package br.com.esign.postdenuncia.util;

import java.util.HashMap;
import java.util.Map;

import br.com.esign.postdenuncia.etl.FonteTimer;
import br.com.esign.postdenuncia.etl.cetesb.CETESBTimer;
import br.com.esign.postdenuncia.etl.feam.FEAMTimer;
import br.com.esign.postdenuncia.etl.iap.IAPTimer;
import br.com.esign.postdenuncia.etl.iema.IEMATimer;
import br.com.esign.postdenuncia.etl.inea.INEATimer;
import br.com.esign.postdenuncia.etl.inema.INEMATimer;
import br.com.esign.postdenuncia.etl.smac.SMACTimer;

public class Global {

    private static final Global global = new Global();

    private String imagesFolder;

    private final Map<String, FonteTimer> timers = new HashMap<>();

    private Global() {
    }

    public static Global getInstance() {
        return global;
    }

    public String getImagesFolder() {
        return imagesFolder;
    }

    public void setImagesFolder(String imagesFolder) {
        this.imagesFolder = imagesFolder;
    }

    public FonteTimer getFonteTimerInstance(String name) {
        FonteTimer timer = timers.get(name);
        if (timer == null) {
            if ("CETESB".equals(name)) {
                timer = new CETESBTimer(name);
            } else if ("INEA".equals(name)) {
                timer = new INEATimer(name);
            } else if ("SMAC".equals(name)) {
                timer = new SMACTimer(name);
            } else if ("IAP".equals(name)) {
                timer = new IAPTimer(name);
            } else if ("FEAM".equals(name)) {
                timer = new FEAMTimer(name);
            } else if ("IEMA".equals(name)) {
                timer = new IEMATimer(name);
            } else if ("INEMA".equals(name)) {
                timer = new INEMATimer(name);
            }
            if (timer != null) {
                timers.put(name, timer);
            }
        }
        return timer;
    }

}