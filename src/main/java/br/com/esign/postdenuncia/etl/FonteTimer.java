package br.com.esign.postdenuncia.etl;

import java.util.Date;
import java.util.Timer;

public abstract class FonteTimer {

    private Timer timer = null;
    private String name = null;

    public FonteTimer(String name) {
        this.name = name;
    }

    protected abstract FonteTask getFonteTask();

    protected abstract Date getFirstTime();

    protected abstract long getPeriod();

    public void start() {
        if (timer == null) {
            timer = new Timer(name);
            timer.scheduleAtFixedRate(getFonteTask(), getFirstTime(), getPeriod());
        }
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}